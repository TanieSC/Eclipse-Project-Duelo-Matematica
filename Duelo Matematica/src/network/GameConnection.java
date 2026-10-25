package network;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * A live 1v1 TCP link to the opponent. Messages are single text lines
 * (they must not contain '\n'), e.g. "ANSWER:2" or "READY".
 *
 * Callbacks run on a background thread: wrap UI work in Platform.runLater(...).
 *
 * STATUS: NEW FILE in package network.
 *
 * WHAT IT IS
 * The finished link between two players. MatrixMatchmakingNode creates it (the constructor is
 * package-private) after the HELLO / ACCEPT handshake succeeded, then passes it to your
 * onMatched callback. The game itself never opens sockets.
 *
 * HOW TO USE
 *   conn.listen(msg -> Platform.runLater(() -> handle(msg)),
 *               () -> Platform.runLater(this::opponentLeft));
 *   conn.send("SOMETHING:payload");
 *   conn.close();        // when leaving the match
 *
 * DETAILS
 *   - listen() starts ONE daemon thread that reads lines until the socket closes. Call it once.
 *   - onDisconnect runs only when the OTHER player drops, not when you call close() yourself.
 *   - send() is synchronized and returns false if the link is already dead.
 *   - Text is UTF-8, one message per line. The game decides what the messages look like.
 *     The 1v1 message format is not finalized yet.
 *   - getOpponentName() is the name the other player announced in the handshake.
 */
public class GameConnection implements Closeable {
    private final String opponentName;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private volatile boolean closed = false;

    GameConnection(String opponentName, Socket socket, BufferedReader in, PrintWriter out) {
        this.opponentName = opponentName;
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public String getOpponentName() {
        return opponentName;
    }

    /** Starts the background reader. Call once. */
    public void listen(Consumer<String> onMessage, Runnable onDisconnect) {
        Thread reader = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    onMessage.accept(line);
                }
            } catch (IOException ignored) {
                // connection dropped
            } finally {
                boolean closedByUs = closed;
                close();
                if (!closedByUs && onDisconnect != null) onDisconnect.run();
            }
        }, "game-connection-reader");
        reader.setDaemon(true);
        reader.start();
    }

    /** Sends one message line. Returns false if the link is already dead. */
    public synchronized boolean send(String message) {
        if (closed) return false;
        out.println(message);
        return !out.checkError();
    }

    public boolean isOpen() {
        return !closed && !socket.isClosed();
    }

    @Override
    public void close() {
        closed = true;
        try { socket.close(); } catch (IOException ignored) { }
    }
}