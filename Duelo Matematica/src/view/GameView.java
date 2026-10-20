package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameView {

    private static final String BASE_ANSWER_STYLE =
        "-fx-background-color: #ffffff;" +
        "-fx-border-color: #555555;" +
        "-fx-border-width: 2px;" +
        "-fx-cursor: hand;";

    private static final String HOVER_ANSWER_STYLE =
        "-fx-background-color: #eef2f7;" +
        "-fx-border-color: #333333;" +
        "-fx-border-width: 2px;" +
        "-fx-cursor: hand;";

    private static final String CORRECT_ANSWER_STYLE =
        "-fx-background-color: #c8e6c9;" +
        "-fx-border-color: #2e7d32;" +
        "-fx-border-width: 3px;";

    private static final String WRONG_ANSWER_STYLE =
        "-fx-background-color: #ffcdd2;" +
        "-fx-border-color: #c62828;" +
        "-fx-border-width: 3px;";

    private static final String BASE_BADGE_STYLE =
        "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;" +
        "-fx-background-color: #666666; -fx-background-radius: 6;" +
        "-fx-min-width: 42px; -fx-min-height: 42px;" +
        "-fx-max-width: 42px; -fx-max-height: 42px;" +
        "-fx-alignment: center;";

    private static final String CORRECT_BADGE_STYLE =
        "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;" +
        "-fx-background-color: #2e7d32; -fx-background-radius: 6;" +
        "-fx-min-width: 42px; -fx-min-height: 42px;" +
        "-fx-max-width: 42px; -fx-max-height: 42px;" +
        "-fx-alignment: center;";

    private static final String WRONG_BADGE_STYLE =
        "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;" +
        "-fx-background-color: #c62828; -fx-background-radius: 6;" +
        "-fx-min-width: 42px; -fx-min-height: 42px;" +
        "-fx-max-width: 42px; -fx-max-height: 42px;" +
        "-fx-alignment: center;";

    private final VBox root;
    private final Label scoreLabel;
    private final Label difficultyLabel;
    private final Button settingsButton;
    private final Button backButton;
    private final VBox questionArea;
    private final VBox answersArea;
    private final Button nextButton;
    private final Button playAgainButton;

    private final List<HBox> currentAnswerBoxes = new ArrayList<>();
    private final List<Label> currentLetterBadges = new ArrayList<>();
    private int correctIndex = -1;
    private boolean answered = false;

    private Consumer<Integer> onAnswerPicked;
    private Runnable onNext;
    private Runnable onPlayAgain;
    private Runnable onBack;
    private Runnable onSettings;

    public GameView() {
        scoreLabel = new Label("Q 0/0 | Score: 0");
        scoreLabel.setStyle("-fx-font-size: 13px;");

        difficultyLabel = new Label("");
        difficultyLabel.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 2px 10px;" +
            "-fx-background-radius: 8; -fx-text-fill: white;"
        );

        // --- Settings and Back Buttons ---
        settingsButton = new Button("⚙");
        settingsButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 18px; -fx-cursor: hand;"
        );
        settingsButton.setOnAction(e -> { if (onSettings != null) onSettings.run(); });

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );
        backButton.setOnAction(e -> { if (onBack != null) onBack.run(); });

        HBox leftCluster = new HBox(15, scoreLabel, difficultyLabel);
        leftCluster.setAlignment(Pos.CENTER_LEFT);

        HBox rightCluster = new HBox(15, settingsButton, backButton);
        rightCluster.setAlignment(Pos.CENTER_RIGHT);

        BorderPane header = new BorderPane();
        header.setCenter(leftCluster);
        header.setRight(rightCluster);
        BorderPane.setAlignment(leftCluster, Pos.CENTER_LEFT);

        questionArea = new VBox();
        questionArea.setPadding(new Insets(12));
        questionArea.setStyle(
            "-fx-background-color: #ffffff; -fx-border-color: #cccccc;" +
            "-fx-border-width: 0 0 0 4px;"
        );

        answersArea = new VBox(12);
        answersArea.setFillWidth(true);
        answersArea.setPadding(new Insets(4, 0, 4, 0));

        VBox contentBox = new VBox(14, questionArea, answersArea);

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        nextButton      = actionButton("NEXT");
        playAgainButton = actionButton("PLAY AGAIN");
        nextButton.setVisible(false);      nextButton.setManaged(false);
        playAgainButton.setVisible(false); playAgainButton.setManaged(false);
        nextButton.setOnAction(e -> { if (onNext != null) onNext.run(); });
        playAgainButton.setOnAction(e -> { if (onPlayAgain != null) onPlayAgain.run(); });

        HBox bottom = new HBox(10, nextButton, playAgainButton);
        bottom.setAlignment(Pos.CENTER);

        root = new VBox(12, header, scroll, bottom);
        root.setPadding(new Insets(15, 20, 20, 20));
        root.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #777777; -fx-border-width: 1px;");
    }

    private Button actionButton(String text) {
        Button b = new Button(text);
        b.setFont(javafx.scene.text.Font.font("Arial", 16));
        b.setPrefSize(150, 35);
        b.setStyle("-fx-background-color: #999999; -fx-text-fill: black; -fx-background-radius: 0; -fx-cursor: hand;");
        return b;
    }

    public Node getRoot()             { return root; }
    public Button getBackButton()     { return backButton; }
    public Button getSettingsButton() { return settingsButton; }
    public Button getNextButton()     { return nextButton; }
    public Button getPlayAgainButton(){ return playAgainButton; }
    public void setOnAnswerPicked(Consumer<Integer> h) { this.onAnswerPicked = h; }
    public void setOnNext(Runnable r)        { this.onNext = r; }
    public void setOnPlayAgain(Runnable r)   { this.onPlayAgain = r; }
    public void setOnBack(Runnable r)        { this.onBack = r; }
    public void setOnSettings(Runnable r)    { this.onSettings = r; }

    public void showQuestion(String rawQuestion,
                             String[] rawOptions,
                             int correctIndex,
                             String topic,
                             String difficulty,
                             int questionNumber,
                             int totalQuestions,
                             int score) {

        scoreLabel.setText(topic + " | Q " + questionNumber + "/" + totalQuestions
                         + " | Score: " + score);
        applyDifficultyBadge(difficulty);

        TextFlow questionFlow = LatexRenderer.toTextFlow(rawQuestion, 15);
        questionArea.getChildren().setAll(questionFlow);

        answersArea.getChildren().clear();
        currentAnswerBoxes.clear();
        currentLetterBadges.clear();
        this.correctIndex = correctIndex;
        this.answered = false;

        for (int i = 0; i < rawOptions.length; i++) {
            final int picked = i;

            Label letter = new Label(String.valueOf((char) ('A' + i)));
            letter.setStyle(BASE_BADGE_STYLE);
            currentLetterBadges.add(letter);

            TextFlow optionFlow = LatexRenderer.toTextFlow(rawOptions[i], 16);
            
            HBox answerBox = new HBox(14, letter, optionFlow);
            answerBox.setAlignment(Pos.CENTER_LEFT);
            answerBox.setPadding(new Insets(10, 20, 10, 20));
            answerBox.setMinHeight(75);
            answerBox.setMaxWidth(Double.MAX_VALUE);
            answerBox.setStyle(BASE_ANSWER_STYLE);

            HBox.setHgrow(optionFlow, Priority.ALWAYS);

            answerBox.setOnMouseClicked(e -> {
                if (!answered) handlePick(picked);
            });
            answerBox.setOnMouseEntered(e -> {
                if (!answered) answerBox.setStyle(HOVER_ANSWER_STYLE);
            });
            answerBox.setOnMouseExited(e -> {
                if (!answered) answerBox.setStyle(BASE_ANSWER_STYLE);
            });

            currentAnswerBoxes.add(answerBox);
            answersArea.getChildren().add(answerBox);
        }

        nextButton.setVisible(false);      nextButton.setManaged(false);
        playAgainButton.setVisible(false); playAgainButton.setManaged(false);
    }

    private void handlePick(int picked) {
        if (answered) return;
        answered = true;

        if (correctIndex >= 0 && correctIndex < currentAnswerBoxes.size()) {
            currentAnswerBoxes.get(correctIndex).setStyle(CORRECT_ANSWER_STYLE);
            currentLetterBadges.get(correctIndex).setStyle(CORRECT_BADGE_STYLE);
        }
        if (picked != correctIndex) {
            currentAnswerBoxes.get(picked).setStyle(WRONG_ANSWER_STYLE);
            currentLetterBadges.get(picked).setStyle(WRONG_BADGE_STYLE);
        }

        if (onAnswerPicked != null) onAnswerPicked.accept(picked);
    }

    public void showNextButton() {
        nextButton.setText("NEXT");
        nextButton.setVisible(true);
        nextButton.setManaged(true);
    }

    public void showPlayAgainButton() {
        playAgainButton.setVisible(true);
        playAgainButton.setManaged(true);
    }

    private void applyDifficultyBadge(String difficulty) {
        if (difficulty == null || difficulty.isEmpty()) {
            difficultyLabel.setText("");
            difficultyLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 2px 10px;");
            return;
        }
        String color;
        switch (difficulty) {
            case "Easy":      color = "#2e7d32"; break;
            case "Medium":    color = "#e65100"; break;
            case "Hard":      color = "#c62828"; break;
            case "Very Hard": color = "#6a1b9a"; break;
            default:          color = "#555555";
        }
        difficultyLabel.setText(difficulty.toUpperCase());
        difficultyLabel.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 2px 10px;" +
            "-fx-background-radius: 8; -fx-text-fill: white;" +
            "-fx-background-color: " + color + ";"
        );
    }
}