package controller;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import model.AudioManager;
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

        AudioManager.playBGM("mus_battle.wav"); 
        
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
        
        //System.out.println("DEBUG: Answer result is " + result); // <-- ADD THIS

        if (result == Game1v1Model.Result.CORRECT || result == Game1v1Model.Result.VICTORY) {
          //  System.out.println("DEBUG: Playing Correct Animation"); // <-- ADD THIS
            view.getGreenSprite().playCorrect(); 
            view.getRedSprite().playWrong();     
        } else if (result == Game1v1Model.Result.INCORRECT || result == Game1v1Model.Result.DEFEAT) {
           // System.out.println("DEBUG: Playing Wrong Animation"); // <-- ADD THIS
            view.getGreenSprite().playWrong();   
            view.getRedSprite().playCorrect();   
        }


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