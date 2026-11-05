package network;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * LAN matchmaker: advertises this player over mDNS (JmDNS), grades the peers it finds
 * with MatchEvaluator, picks the best one, and opens a TCP link.
 *
 * Connection rule: every node listens on a ServerSocket. For any pair, the player whose
 * name sorts HIGHER connects, the other accepts. A tiny HELLO / ACCEPT / BUSY handshake
 * makes sure both sides agree before either one commits, so two players can never end up
 * half-connected to different people.
 *
 * STATUS: NEW FILE in package network. 
 *
 * HOW IT WORKS, STEP BY STEP
 *   1. start() opens a ServerSocket on a free port and registers this player on the network
 *      with JmDNS (service type _kahoot1v1._tcp.local., name = player name, text record
 *      "elo" = player Elo). It then listens for other players doing the same.
 *   2. Every player found is graded by MatchEvaluator (0.0 to 1.0). Anyone below 0.35 is
 *      dropped. The rest go into candidatePool and are removed again if they disappear.
 *      Proximity is measured locally: the time between asking for the peer's info and
 *      receiving it.
 *   3. Every 3 seconds (SCAN_WINDOW_MS) the auction loop picks the best-scoring candidate.
 *        - If MY name sorts alphabetically HIGHER than theirs, I connect to them.
 *        - Otherwise I wait up to 4 seconds (INBOUND_WAIT_MS) for them to connect to me.
 *        - If nothing happens, it loops and re-scores with fresh data.
 *   4. TCP handshake: the connecting side sends "HELLO name". The other side answers
 *      "ACCEPT" if it is free or "BUSY" if it is already in a handshake or matched. Only one
 *      handshake can run at a time (tryBegin / endBusy), so a player can never commit to two
 *      opponents.
 *   5. After ACCEPT both sides set matched = true, shut down discovery on a separate thread
 *      (JmDNS close can be slow) and call onMatched(GameConnection).
 *
 * THREADS (all daemon): accept loop, auction loop, one short thread per incoming socket, the
 * JmDNS callback threads, and a one-time shutdown thread. Nothing here touches JavaFX, so
 * wrap anything UI-related in Platform.runLater.
 *
 * HOW TO USE
 *   MatrixMatchmakingNode node = new MatrixMatchmakingNode(name, elo,
 *           conn -> Platform.runLater(() -> startMatch(conn)));
 *   new Thread(() -> { try { node.start(); } catch (IOException e) { ... } }).start();
 *   node.stop();     // user pressed Cancel (safe to call any time)
 *
 * CHANGES compared with the version you pasted
 *   - Proximity measured with a local clock. The old version compared a timestamp written by
 *     the other PC against this PC's clock, which breaks when the clocks differ.
 *   - Real handshake (HELLO / ACCEPT / BUSY). Before, two players could each pick a different
 *     "best" opponent and end up connected to nobody.
 *   - The server socket uses a free port chosen by the OS instead of a fixed port, so two
 *     copies can run on one PC.
 *   - Uses the first IPv4 address. The old call returned an array where a String was expected.
 *   - A while-loop replaces the recursive retry. Flags are volatile and access is synchronized.
 *   - GameInputHook is replaced by the onMatched callback plus GameConnection.
 *   - Added public stop() so the Cancel button can end the search.
 *
 * REQUIREMENTS AND LIMITS
 *   - Needs the jmdns and slf4j-api jars on the classpath.
 *   - Same local network only (mDNS multicast). Some school or guest Wi-Fi blocks it
 *     ("client isolation"), and a firewall can block the TCP port.
 *   - Player names must be unique: the name is the network service name and the tie-breaker.
 */
public class MatrixMatchmakingNode {
    private static final String SERVICE_TYPE = "_kahoot1v1._tcp.local.";
    private static final double MINIMUM_VIABILITY_FLOOR = 0.35;
    private static final long SCAN_WINDOW_MS = 3000;      // time to collect peers before choosing
    private static final long INBOUND_WAIT_MS = 4000;     // how long to wait for the other side to connect to us
    private static final int CONNECT_TIMEOUT_MS = 2000;
    private static final int HANDSHAKE_TIMEOUT_MS = 2000;

    private final String myPlayerName;
    private final int myElo;
    private final Consumer<GameConnection> onMatched;

    private JmDNS jmdns;
    private ServerSocket serverSocket;
    private Thread acceptThread;
    private Thread auctionThread;

    private volatile boolean running = false;
    private volatile boolean matched = false;
    private boolean busy = false; // guarded by "this": a handshake is in progress
    private final AtomicBoolean shutDown = new AtomicBoolean(false);

    private final ConcurrentHashMap<String, PeerCandidate> candidatePool = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> requestTimes = new ConcurrentHashMap<>();

    /**
     * @param onMatched called ONCE with the open connection (on a background thread,
     *                  so use Platform.runLater for anything touching the UI)
     */
    public MatrixMatchmakingNode(String playerName, int elo, Consumer<GameConnection> onMatched) {
        this.myPlayerName = playerName;
        this.myElo = elo;
        this.onMatched = onMatched;
    }

    /** Starts advertising and searching. Call from a background thread (JmDNS setup can block). */
    public void start() throws IOException {
        if (running) return;
        running = true;
        try {
            serverSocket = new ServerSocket(0); // OS picks a free port, so two instances on one PC don't clash
            int myPort = serverSocket.getLocalPort();

            jmdns = JmDNS.create(InetAddress.getLocalHost());

            Map<String, String> txt = new HashMap<>();
            txt.put("elo", String.valueOf(myElo));
            jmdns.registerService(ServiceInfo.create(SERVICE_TYPE, myPlayerName, myPort, 0, 0, true, txt));
            System.out.println("[" + myPlayerName + " (Elo " + myElo + ")] Matchmaker started on port " + myPort);

            jmdns.addServiceListener(SERVICE_TYPE, new ServiceListener() {
                @Override
                public void serviceAdded(ServiceEvent event) {
                    if (event.getName().equals(myPlayerName)) return;
                    requestTimes.putIfAbsent(event.getName(), System.nanoTime());
                    jmdns.requestServiceInfo(event.getType(), event.getName(), true);
                }

                @Override
                public void serviceResolved(ServiceEvent event) {
                    gradePeer(event.getInfo());
                }

                @Override
                public void serviceRemoved(ServiceEvent event) {
                    candidatePool.remove(event.getName());
                    requestTimes.remove(event.getName());
                }
            });

            acceptThread = startDaemon("matchmaker-accept", this::acceptLoop);
            auctionThread = startDaemon("matchmaker-auction", this::auctionLoop);
        } catch (IOException | RuntimeException e) {
            stop();
            throw e;
        }
    }

    /** Cancels matchmaking (e.g. player pressed Back). Safe to call any time. */
    public void stop() {
        running = false;
        if (auctionThread != null) auctionThread.interrupt();
        shutdownDiscovery();
    }

    // ------------------------------------------------------------------
    // Grading
    // ------------------------------------------------------------------
    private void gradePeer(ServiceInfo info) {
        String peerName = info.getName();
        if (peerName.equals(myPlayerName) || matched) return;

        Long requestedAt = requestTimes.remove(peerName);
        if (requestedAt == null) return; // already graded this peer

        String eloStr = info.getPropertyString("elo");
        Inet4Address[] addresses = info.getInet4Addresses();
        if (eloStr == null || addresses.length == 0) {
            requestTimes.put(peerName, requestedAt); // info not complete yet, wait for the next resolve
            return;
        }

        int peerElo;
        try {
            peerElo = Integer.parseInt(eloStr);
        } catch (NumberFormatException e) {
            return;
        }

        // Measured on THIS machine only, so it works without clock sync between PCs
        long latencyMs = Math.max(1, (System.nanoTime() - requestedAt) / 1_000_000);
        double grade = MatchEvaluator.evaluatePeer(myElo, peerElo, latencyMs);

        if (grade >= MINIMUM_VIABILITY_FLOOR) {
            System.out.printf("Evaluated [%s] -> grade %.2f (Elo diff %d, %dms)%n",
                    peerName, grade, Math.abs(myElo - peerElo), latencyMs);
            candidatePool.put(peerName,
                    new PeerCandidate(peerName, addresses[0].getHostAddress(), info.getPort(), grade));
        } else {
            System.out.printf("Dropped [%s] -> grade %.2f below floor%n", peerName, grade);
        }
    }

    // ------------------------------------------------------------------
    // Auction loop: pick the best candidate, then connect or wait
    // ------------------------------------------------------------------
    private void auctionLoop() {
        try {
            while (running && !matched) {
                Thread.sleep(SCAN_WINDOW_MS);
                if (!running || matched) return;

                PeerCandidate best = pickBest();
                if (best == null) {
                    System.out.println("No viable candidates yet, still searching...");
                    continue;
                }

                System.out.printf("Best candidate: [%s] grade %.2f%n", best.name, best.score);

                if (myPlayerName.compareTo(best.name) > 0) {
                    if (!connectTo(best)) {
                        candidatePool.remove(best.name); // unreachable or busy: re-evaluate next round
                    }
                } else {
                    // They should connect to us; give them time, then re-score if nothing arrives
                    Thread.sleep(INBOUND_WAIT_MS);
                }
            }
        } catch (InterruptedException ignored) {
            // stop() was called
        }
    }

    private PeerCandidate pickBest() {
        PeerCandidate best = null;
        for (PeerCandidate c : candidatePool.values()) {
            if (best == null || c.score > best.score) best = c;
        }
        return best;
    }

    // ------------------------------------------------------------------
    // Outbound: we connect to them
    // ------------------------------------------------------------------
    private boolean connectTo(PeerCandidate c) {
        if (!tryBegin()) return false;

        Socket socket = null;
        boolean success = false;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(c.host, c.port), CONNECT_TIMEOUT_MS);
            socket.setSoTimeout(HANDSHAKE_TIMEOUT_MS);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            out.println("HELLO " + myPlayerName);
            String reply = in.readLine();

            if ("ACCEPT".equals(reply)) {
                socket.setSoTimeout(0);
                finishMatch(new GameConnection(c.name, socket, in, out));
                success = true;
            } else {
                System.out.println("[" + c.name + "] replied " + reply + ", will retry later.");
            }
        } catch (IOException e) {
            System.out.println("Could not reach [" + c.name + "]: " + e.getMessage());
        } finally {
            if (!success) {
                closeQuietly(socket);
                endBusy();
            }
        }
        return success;
    }

    // ------------------------------------------------------------------
    // Inbound: they connect to us
    // ------------------------------------------------------------------
    private void acceptLoop() {
        while (running && !matched) {
            try {
                Socket incoming = serverSocket.accept();
                startDaemon("matchmaker-inbound", () -> handleInbound(incoming));
            } catch (IOException e) {
                if (!running || matched || serverSocket.isClosed()) return;
            }
        }
    }

    private void handleInbound(Socket socket) {
        boolean began = false;
        boolean success = false;
        try {
            socket.setSoTimeout(HANDSHAKE_TIMEOUT_MS);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            String hello = in.readLine();
            if (hello == null || !hello.startsWith("HELLO ")) return;

            if (!tryBegin()) {
                out.println("BUSY");
                return;
            }
            began = true;

            out.println("ACCEPT");
            socket.setSoTimeout(0);
            finishMatch(new GameConnection(hello.substring(6), socket, in, out));
            success = true;
        } catch (IOException ignored) {
            // peer vanished mid-handshake
        } finally {
            if (!success) {
                if (began) endBusy();
                closeQuietly(socket);
            }
        }
    }

    // ------------------------------------------------------------------
    // Shared state helpers
    // ------------------------------------------------------------------
    /** Only one handshake at a time, and none once matched. */
    private synchronized boolean tryBegin() {
        if (matched || busy) return false;
        busy = true;
        return true;
    }

    private synchronized void endBusy() {
        busy = false;
    }

    private void finishMatch(GameConnection connection) {
        synchronized (this) {
            matched = true;
            busy = false;
        }
        System.out.println("Matched with [" + connection.getOpponentName() + "]");
        startDaemon("matchmaker-shutdown", this::shutdownDiscovery); // JmDNS close can be slow, keep it off this thread
        onMatched.accept(connection);
    }

    private void shutdownDiscovery() {
        if (!shutDown.compareAndSet(false, true)) return;
        candidatePool.clear();
        requestTimes.clear();
        closeQuietly(serverSocket);
        if (jmdns != null) {
            try {
                jmdns.unregisterAllServices();
                jmdns.close();
            } catch (IOException ignored) { }
        }
    }

    private static Thread startDaemon(String name, Runnable task) {
        Thread t = new Thread(task, name);
        t.setDaemon(true);
        t.start();
        return t;
    }

    private static void closeQuietly(java.io.Closeable c) {
        if (c == null) return;
        try { c.close(); } catch (IOException ignored) { }
    }

    private static final class PeerCandidate {
        final String name;
        final String host;
        final int port;
        final double score;

        PeerCandidate(String name, String host, int port, double score) {
            this.name = name;
            this.host = host;
            this.port = port;
            this.score = score;
        }
    }
}