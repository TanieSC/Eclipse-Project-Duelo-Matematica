package controller;

import model.GameService;
import model.Question;
import model.Session;
import view.GameView;
import view.GameSettingsView;
import javafx.stage.Stage;

public class GameController {

    private final GameView view;
    private final GameService service;
    private final Navigator nav;

    public GameController(GameView view, GameService service, Navigator nav, boolean resume) {
        this.view = view;
        this.service = service;
        this.nav = nav;

        view.setOnAnswerPicked(this::handleAnswer);
        view.setOnNext(this::handleNext);
        view.setOnPlayAgain(this::handlePlayAgain);
        view.setOnBack(this::handleBack);
        
        // Wire the new Settings button
        view.setOnSettings(this::openSettings);

        if (resume) {
            renderCurrentQuestion();
        } else {
            startGame();
        }
    }

    private void startGame() {
        service.startNewGame();
        renderCurrentQuestion();
    }

    private void renderCurrentQuestion() {
        Question q = service.getCurrentQuestion();
        view.showQuestion(
            q.getText(),
            service.getCurrentOptions(),
            service.getCorrectOptionIndex(),
            service.getCategory(),
            q.getDifficulty(),
            service.getCurrentIndex() + 1,
            service.getTotal(),
            service.getScore()
        );
    }

    private void handleAnswer(int pickedIndex) {
        service.submitAnswer(pickedIndex);
        view.showNextButton();
    }

    private void handleNext() {
        service.advance();
        if (service.isGameOver()) {
            Session.clearSavedGame();
            view.showPlayAgainButton();
        } else {
            renderCurrentQuestion();
        }
    }

    private void handlePlayAgain() {
        startGame();
    }

    private void handleBack() {
        if (!service.isGameOver()) {
            Session.setSavedGame(service);
        } else {
            Session.clearSavedGame();
        }
        nav.goToMenu();
    }

    /**
     * Opens the Settings popup on top of the Game.
     */
    private void openSettings() {
        // Get the Stage from the GameView's scene
        Stage owner = (Stage) view.getRoot().getScene().getWindow();
        GameSettingsView v = new GameSettingsView(owner);
        new GameSettingsController(v);
        v.show(); 
    }
}