package controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import model.NpcOpponent;
import model.StoryBattleModel;
import view.StoryBattleView;

public class StoryBattleController {

    private final StoryBattleView view;
    private final StoryBattleModel model;
    private final Navigator nav;
    private final NpcOpponent bot;
    
    // Prevents the bot and player from answering at the exact same time
    private boolean roundResolved = false;

    public StoryBattleController(StoryBattleView view, StoryBattleModel model, Navigator nav, int level) {
        this.view = view;
        this.model = model;
        this.nav = nav;
        
        // 1. Instantiate the Bot using the chosen level (1, 2, or 3)
        this.bot = new NpcOpponent(level);

        view.getSurrenderButton().setOnAction(e -> handleSurrender());
        
        view.setOnAnswerPicked(this::handlePlayerAnswer);


        model.startGame();
        loadNextQuestion();
    }

    private void loadNextQuestion() {
        roundResolved = false;
        model.nextQuestion();
        view.showQuestion(model.getCurrentQuestion());

        // Start the Bot timer!
        bot.answerQuestion(
            model.getCurrentQuestion().correctIndex, 
            4, 
            10000, 
            this::handleBotAnswer
        );
    }

    private void handlePlayerAnswer(int selectedIndex) {
        if (roundResolved) return;
        roundResolved = true;
        
        bot.cancel();
        view.disableOptions();

        StoryBattleModel.Result result = model.submitAnswer(selectedIndex);
        processTurnResult(result);
    }

    private void handleBotAnswer(NpcOpponent.NpcAnswer answer) {
        Platform.runLater(() -> {
            if (roundResolved) return;
            roundResolved = true;

            view.disableOptions();

            StoryBattleModel.Result result = model.submitOpponentAnswer(answer.getChosenIndex(), answer.getDelayMs());
            processTurnResult(result);
        });
    }

    private void processTurnResult(StoryBattleModel.Result result) {
        view.animateBoxes(model.greenX, model.redX, () -> {
            
            // THE FIX: Call the View's custom dropdown instead of an Alert
            if (result == StoryBattleModel.Result.VICTORY) {
                view.showGameOverPopup(true, this::exitGame);
            } else if (result == StoryBattleModel.Result.DEFEAT) {
                view.showGameOverPopup(false, this::exitGame);
            } else {
                loadNextQuestion();
            }
            
        });
    }

    private void handleSurrender() {
        bot.shutdown(); 
        nav.goToConcede();
    }
    
    private void exitGame() {
        bot.shutdown();
        nav.goToStoryLevels();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}