package controller;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import model.Game1v1Model;
import view.Game1v1View;

public class Game1v1Controller {

    private final Game1v1View view;
    private final Game1v1Model model;
    private final MainMenuController mainMenuController;

    public Game1v1Controller(Game1v1View view, Game1v1Model model, MainMenuController mainMenuController) {
        this.view = view;
        this.model = model;
        this.mainMenuController = mainMenuController;

        setupHandlers();
        startGame();

        view.getSurrenderButton().setOnAction(e -> mainMenuController.goToConcede());
        
        view.getGearButton().setOnAction(e -> mainMenuController.goToSettings());
    }

    private void setupHandlers() {
        // GameView-style callback: the view has already coloured the answers,
        // it just tells us which index was clicked.
        view.setOnAnswerPicked(this::handleAnswer);

        view.getSurrenderButton().setOnAction(e -> {
            view.showGameOverPopup(false, () -> mainMenuController.goToGameStartMenu());
        });
    }

    private void startGame() {
        model.startGame();
        view.showQuestion(model.getCurrentQuestion());
    }

    private void handleAnswer(int selectedIndex) {
        // Note: view.handlePick() already set answered=true and painted
        // correct=green / wrong=red before this callback fired.
        view.disableOptions(); // harmless, kept for API compatibility

        Game1v1Model.Result result = model.submitAnswer(selectedIndex, view.getCurrentCorrectIndex());

        double targetGreenX = model.getGreenX();
        double targetRedX = model.getRedX();

        // View calculates health based on box positions automatically
        view.animateBoxes(targetGreenX, targetRedX, () -> {

            if (result == Game1v1Model.Result.VICTORY) {
                view.showGameOverPopup(true, () -> mainMenuController.goToGameStartMenu());

            } else if (result == Game1v1Model.Result.DEFEAT) {
                view.showGameOverPopup(false, () -> mainMenuController.goToGameStartMenu());

            } else {
                PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
                pause.setOnFinished(e -> {
                    model.nextQuestion();
                    view.showQuestion(model.getCurrentQuestion());
                });
                pause.play();
            }
        });
    }
}