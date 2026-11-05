package controller;

import javafx.application.Platform;
import model.AuthService;
import model.Session;
import model.User;
import network.GameConnection;
import network.MatrixMatchmakingNode;
import view.MatchmakingView;

public class MatchmakingController {

    private final MatchmakingView view;
    private final Navigator nav;
    private final int durationSeconds;
    private final MatrixMatchmakingNode node;

    private volatile boolean matched = false;

    public MatchmakingController(MatchmakingView view, Navigator nav,
                                 AuthService authService, int durationSeconds) {
        this.view = view;
        this.nav = nav;
        this.durationSeconds = durationSeconds;

        User me = Session.getCurrentUser();
        String playerName = me != null ? me.getUsername() : "Guest";
        int elo = me != null ? me.getElo() : 1000;

        view.getCancelButton().setOnAction(e -> handleCancel());

        this.node = new MatrixMatchmakingNode(playerName, elo, this::handleMatched);

        // start() can block — run off the FX thread
        Thread t = new Thread(() -> {
            try {
                node.start();
                Platform.runLater(() -> view.setStatus("Searching local network…"));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    view.setStatus("Matchmaking failed.");
                    view.setDetail(ex.getMessage() != null ? ex.getMessage()
                                                            : ex.getClass().getSimpleName());
                });
            }
        }, "matchmaker-start");
        t.setDaemon(true);
        t.start();
    }

    private void handleMatched(GameConnection connection) {
        if (matched) { connection.close(); return; }
        matched = true;

        Platform.runLater(() -> nav.goTo1v1Battle(connection, durationSeconds));
    }

    private void handleCancel() {
        node.stop();
        // Return to time selection, replacing the matchmaking popup
        nav.goTo1v1TimeSelection();
    }
}
