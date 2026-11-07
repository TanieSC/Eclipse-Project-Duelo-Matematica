package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import model.AudioManager;
import model.AuthService;
import model.Network1v1Model;
import model.Question;
import model.Session;
import model.User;
import network.GameConnection;
import view.Game1v1View;

import java.util.concurrent.ThreadLocalRandom;

/**
 * NETWORK 1v1 CONTROLLER — drives a live match over a GameConnection.
 *
 * PROTOCOL
 *   INIT:<myElo>                        (each side, once at match start)
 *   SEED:<long>                         (host → guest, once)
 *   ANS:<qIdx>:<optIdx>:<elapsedMs>     (each answer, both directions)
 *   RESULT:WIN|LOSE|DRAW                (sender's own perspective, once)
 *
 * Both clients compute the outcome independently; RESULT is a belt-and-braces
 * signal so a timer edge-case can never leave one side hanging.
 */
public class Network1v1Controller {

    private final Game1v1View view;
    private final Network1v1Model model;
    private final Navigator nav;
    private final AuthService authService;
    private final GameConnection conn;
    private final boolean isHost;

    private int opponentElo = -1;
    private long questionStartMs;
    private boolean playerAnswered;
    private boolean opponentAnswered;
    private boolean seedReceived;       // guest only
    private boolean started;
    private boolean matchEnded;

    private Timeline timerTick;

    public Network1v1Controller(Game1v1View view,
                                Network1v1Model model,
                                Navigator nav,
                                AuthService authService,
                                GameConnection conn) {
        this.view = view;
        this.model = model;
        this.nav = nav;
        this.authService = authService;
        this.conn = conn;

        User me = Session.getCurrentUser();
        String myName = me != null ? me.getUsername() : "";
        this.isHost = myName.compareTo(conn.getOpponentName()) > 0;

        // Wire the view
        view.showTimer(true);
        view.setOnAnswerPicked(this::handlePlayerAnswer);
        view.getSurrenderButton().setOnAction(e -> handleSurrender());
        view.getGearButton().setOnAction(e -> nav.goToSettings());

        // Network
        conn.listen(this::handleMessage, this::handleDisconnect);

        int myElo = me != null ? me.getElo() : 1000;
        conn.send("INIT:" + myElo);
    }

    // ==========================================================
    // Network dispatch
    // ==========================================================

    private void handleMessage(String line) {
        Platform.runLater(() -> handleMessageFX(line));
    }

    private void handleMessageFX(String line) {
        if (line == null) return;

        if (line.startsWith("INIT:")) {
            try { opponentElo = Integer.parseInt(line.substring(5)); } catch (NumberFormatException ignored) { return; }
            tryStart();

        } else if (line.startsWith("SEED:")) {
            if (isHost) return; // host already has its own seed
            try {
                long seed = Long.parseLong(line.substring(5));
                model.setSeed(seed);
                seedReceived = true;
            } catch (NumberFormatException ignored) { return; }
            tryStart();

        } else if (line.startsWith("ANS:")) {
            String[] parts = line.split(":");
            if (parts.length < 4) return;
            try {
                int qIdx   = Integer.parseInt(parts[1]);
                int optIdx = Integer.parseInt(parts[2]);
                long elapsed = Long.parseLong(parts[3]);
                handleOpponentAnswer(qIdx, optIdx, elapsed);
            } catch (NumberFormatException ignored) { }

        } else if (line.startsWith("RESULT:")) {
            String res = line.substring(7);
            Network1v1Model.Result myResult;
            if ("WIN".equals(res))       myResult = Network1v1Model.Result.OPPONENT_VICTORY;
            else if ("LOSE".equals(res)) myResult = Network1v1Model.Result.PLAYER_VICTORY;
            else if ("DRAW".equals(res)) myResult = Network1v1Model.Result.DRAW;
            else return;
            endMatch(myResult);
        }
    }

    private void handleDisconnect() {
        Platform.runLater(() -> {
            if (matchEnded) return;
            matchEnded = true;
            stopTimer();
            // Opponent vanished = forfeit = win for me
            updateElo(true, false);
            view.showGameOverPopup(true, () -> nav.goToGameStartMenu());
        });
    }

    // ==========================================================
    // Match lifecycle
    // ==========================================================

    private void tryStart() {
        if (started) return;
        if (opponentElo < 0) return;

        if (isHost) {
            long seed = ThreadLocalRandom.current().nextLong();
            model.setSeed(seed);
            conn.send("SEED:" + seed);
            startGame();
        } else {
            if (!seedReceived) return;
            startGame();
        }
    }

    private void startGame() {
        if (started) return;
        started = true;
        model.startGame();
        showCurrentQuestion();
        startTimer();
    }

    private void showCurrentQuestion() {
        playerAnswered = false;
        opponentAnswered = false;
        questionStartMs = System.currentTimeMillis();

        Question q = model.getCurrentQuestion();
        view.showQuestion(q, model.getCurrentOptions(), model.getCurrentCorrectIndex());
    }

    // ==========================================================
    // Player input
    // ==========================================================

    private void handlePlayerAnswer(int selectedIdx) {
        if (playerAnswered || matchEnded) return;
        playerAnswered = true;

        long elapsed = System.currentTimeMillis() - questionStartMs;
        conn.send("ANS:" + model.getCurrentQuestionIndex() + ":" + selectedIdx + ":" + elapsed);
        
        // Check if the player's answer was correct
        boolean isCorrect = (selectedIdx == model.getCurrentCorrectIndex());
        
        // --- TRIGGER PLAYER ANIMATIONS ---
        if (isCorrect) {
            view.getGreenSprite().playCorrect();
            view.getRedSprite().playWrong();
        } else {
            view.getGreenSprite().playWrong();
            view.getRedSprite().playCorrect();
        }

        model.applyPlayerAnswer(selectedIdx, elapsed);
        view.animateBoxes(model.getGreenX(), model.getRedX(), this::afterAnimation);
    }

    private void handleOpponentAnswer(int qIdx, int optIdx, long elapsed) {
        if (matchEnded) return;
        if (qIdx != model.getCurrentQuestionIndex()) return;
        if (opponentAnswered) return;
        opponentAnswered = true;

        boolean oppCorrect = (optIdx == model.getCurrentCorrectIndex());
        
        // --- TRIGGER OPPONENT ANIMATIONS ---
        if (oppCorrect) {
            view.getRedSprite().playCorrect();
            view.getGreenSprite().playWrong();
        } else {
            view.getRedSprite().playWrong();
            view.getGreenSprite().playCorrect();
        }
        
        model.applyOpponentAnswer(oppCorrect, elapsed);
        view.animateBoxes(model.getGreenX(), model.getRedX(), this::afterAnimation);
    }

    private void afterAnimation() {
        Network1v1Model.Result result = model.checkResult();
        if (result != Network1v1Model.Result.ONGOING) {
            detectAndHandleEnd(result);
            return;
        }
        if (playerAnswered && opponentAnswered) {
            model.nextQuestion();
            showCurrentQuestion();
        }
    }

    private void handleSurrender() {
        if (matchEnded) return;
        detectAndHandleEnd(Network1v1Model.Result.OPPONENT_VICTORY);
    }

    // ==========================================================
    // Match end
    // ==========================================================

    private void detectAndHandleEnd(Network1v1Model.Result result) {
        if (matchEnded) return;

        if(result == Network1v1Model.Result.OPPONENT_VICTORY) {
        	
        	AudioManager.playSFX("sfx_death.wav");
        }
             
        String sendResult =
                result == Network1v1Model.Result.PLAYER_VICTORY   ? "WIN"  :
                result == Network1v1Model.Result.OPPONENT_VICTORY ? "LOSE" : "DRAW";
        conn.send("RESULT:" + sendResult);

        endMatch(result);
    }

    private void endMatch(Network1v1Model.Result result) {
        if (matchEnded) return;
        matchEnded = true;
        stopTimer();

        boolean win  = result == Network1v1Model.Result.PLAYER_VICTORY;
        boolean draw = result == Network1v1Model.Result.DRAW;

        updateElo(win, draw);

        view.showGameOverPopup(win, () -> {
            try { conn.close(); } catch (Exception ignored) { }
            nav.goToGameStartMenu();
        });
    }

    private void updateElo(boolean win, boolean draw) {
        User me = Session.getCurrentUser();
        if (me == null || opponentElo < 0) return;

        int myElo = me.getElo();
        double score = draw ? 0.5 : (win ? 1.0 : 0.0);
        int K = 32;
        double expected = 1.0 / (1.0 + Math.pow(10.0, (opponentElo - myElo) / 400.0));
        int newElo = (int) Math.round(myElo + K * (score - expected));
        newElo = Math.max(0, newElo);

        if (authService.updateElo(me.getEmail(), newElo)) {
            User refreshed = authService.getUserByEmail(me.getEmail());
            if (refreshed != null) Session.setCurrentUser(refreshed);
            System.out.println("ELO updated: " + myElo + " -> " + newElo);
        }
    }

    // ==========================================================
    // Timer
    // ==========================================================

    private void startTimer() {
        timerTick = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimerDisplay()));
        timerTick.setCycleCount(Timeline.INDEFINITE);
        timerTick.play();
        updateTimerDisplay();
    }

    private void updateTimerDisplay() {
        int remain = model.getRemainingSeconds();
        int min = remain / 60;
        int sec = remain % 60;
        view.setTimerText(String.format("%02d:%02d", min, sec));

        if (remain <= 0 && !matchEnded) {
            Network1v1Model.Result r = model.checkResult();
            if (r != Network1v1Model.Result.ONGOING) detectAndHandleEnd(r);
        }
    }

    private void stopTimer() {
        if (timerTick != null) {
            timerTick.stop();
            timerTick = null;
        }
    }
}
