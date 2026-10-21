package controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Game1v1Model;
import model.Question;
import view.Game1v1View;
import view.GameSettingsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * GAME 1V1 CONTROLLER — The brain behind the 1v1 Battle Screen.
 */
public class Game1v1Controller {

    private final Game1v1View view;
    private final Game1v1Model model;
    private final Navigator nav;

    private String[] currentOptions;
    private int correctIndex;

    public Game1v1Controller(Game1v1View view, Game1v1Model model, Navigator nav) {
        this.view = view;
        this.model = model;
        this.nav = nav;

        // Wire buttons
        view.getSurrenderButton().setOnAction(e -> handleSurrender());
        view.getGearButton().setOnAction(e -> openSettings());
        
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

        // Prepare shuffled options
        List<String> opts = new ArrayList<>();
        opts.add(q.getCorrectAnswer());
        opts.addAll(Arrays.asList(q.getWrongAnswers()));
        Collections.shuffle(opts);

        currentOptions = opts.toArray(new String[0]);
        correctIndex = opts.indexOf(q.getCorrectAnswer());

        view.setOptions(currentOptions);
        view.showQuestion(q, correctIndex);
    }

    private void handleAnswer(int selectedIndex) {
        Game1v1Model.Result result = model.submitAnswer(selectedIndex, correctIndex);

        // Animate the boxes
        view.animateBoxes(model.greenX, model.redX, () -> {
            if (result == Game1v1Model.Result.VICTORY) {
                showAlert("VICTORY!", "Red box fell off the platform! You Win!");
                nav.goToGameStartMenu();
            } else if (result == Game1v1Model.Result.DEFEAT) {
                showAlert("DEFEAT!", "Green box fell off the platform! You Lost!");
                nav.goToGameStartMenu();
            } else {
                loadNextQuestion();
            }
        });
    }

    private void handleSurrender() {
        showAlert("SURRENDER", "You raised the white flag! Battle Surrendered.");
        nav.goToGameStartMenu();
    }

    private void openSettings() {
        Stage owner = (Stage) view.getRoot().getScene().getWindow();
        GameSettingsView v = new GameSettingsView(owner);
        new GameSettingsController(v);
        v.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
