package controller;

import javafx.scene.control.Alert;
import model.GameStoryModel;
import model.GameStoryModel.Result;
import model.GameStoryModel.Question;
import view.GameStoryView;

/**
 * GAME STORY CONTROLLER — The brain behind the Tug-of-War screen.
 */
public class GameStoryController {

    private final GameStoryView view;
    private final GameStoryModel model;
    private final Navigator nav;

    public GameStoryController(GameStoryView view, GameStoryModel model, Navigator nav) {
        this.view = view;
        this.model = model;
        this.nav = nav;

        // Wire buttons
        view.getPauseButton().setOnAction(e -> handlePause());
        view.getGearButton().setOnAction(e -> nav.goToSettings());
        view.getPauseButton().setOnAction(e -> nav.goToConcede());
        
        for (int i = 0; i < 4; i++) {
            final int index = i;
            view.getOptionButtons()[i].setOnAction(e -> handleAnswer(index));
        }

        // Start the game
        model.startGame();
        loadNextQuestion();
    }

    private void loadNextQuestion() {
        model.nextQuestion();
        Question q = model.getCurrentQuestion();
        view.showQuestion(q);
    }

    private void handleAnswer(int selectedIndex) {
        Result result = model.submitAnswer(selectedIndex);
        
        // Animate the boxes, then check if the game is over
        view.animateBoxes(model.greenX, model.redX, () -> {
            if (result == Result.VICTORY) {
                showAlert("VICTORY!", "Red box fell off the platform! You Win!");
                nav.goToGameStartMenu();
            } else if (result == Result.DEFEAT) {
                showAlert("DEFEAT!", "Green box fell off the platform! You Lost!");
                nav.goToGameStartMenu();
            } else {
                loadNextQuestion();
            }
        });
    }

    private void handlePause() {
        showAlert("PAUSE", "Game Paused.");
        nav.goToGameStartMenu();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}