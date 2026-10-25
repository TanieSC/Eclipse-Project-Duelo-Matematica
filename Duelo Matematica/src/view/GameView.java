package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * GAME VIEW — The visual screen where the actual quiz takes place.
 *
 * WHAT DOES THIS CLASS DO?
 * Think of this class as the "Painter" and the "Listener".
 * - As a Painter, it draws the header, the math question, the four big
 *   A/B/C/D answer boxes (in two columns), and the bottom buttons.
 * - As a Listener, it waits for the user to click an answer. When they do,
 *   it immediately paints the correct answer green and the wrong answer red.
 *
 * It does NOT keep score or pick the next question. That is the GameController's job.
 */
public class GameView {

    // --- CSS STYLES (The "Paint Buckets") ---
    // These are just text strings that tell JavaFX how to color the answer boxes.

    // Normal look: white background, grey border.
    private static final String BASE_ANSWER_STYLE =
        "-fx-background-color: #ffffff;" + "-fx-border-color: #555555;" +
        "-fx-border-width: 2px;" + "-fx-cursor: hand;";

    // Hover look: light blue background, dark grey border (when mouse is over it).
    private static final String HOVER_ANSWER_STYLE =
        "-fx-background-color: #eef2f7;" + "-fx-border-color: #333333;" +
        "-fx-border-width: 2px;" + "-fx-cursor: hand;";

    // Correct look: light green background, dark green border.
    private static final String CORRECT_ANSWER_STYLE =
        "-fx-background-color: #c8e6c9;" + "-fx-border-color: #2e7d32;" +
        "-fx-border-width: 3px;";

    // Wrong look: light red background, dark red border.
    private static final String WRONG_ANSWER_STYLE =
        "-fx-background-color: #ffcdd2;" + "-fx-border-color: #c62828;" +
        "-fx-border-width: 3px;";

    // Styles for the A, B, C, D squares next to the answers.
    private static final String BASE_BADGE_STYLE =
        "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;" +
        "-fx-background-color: #666666; -fx-background-radius: 6;" +
        "-fx-min-width: 42px; -fx-min-height: 42px; -fx-max-width: 42px; -fx-max-height: 42px; -fx-alignment: center;";

    private static final String CORRECT_BADGE_STYLE =
        "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;" +
        "-fx-background-color: #2e7d32; -fx-background-radius: 6;" +
        "-fx-min-width: 42px; -fx-min-height: 42px; -fx-max-width: 42px; -fx-max-height: 42px; -fx-alignment: center;";

    private static final String WRONG_BADGE_STYLE =
        "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;" +
        "-fx-background-color: #c62828; -fx-background-radius: 6;" +
        "-fx-min-width: 42px; -fx-min-height: 42px; -fx-max-width: 42px; -fx-max-height: 42px; -fx-alignment: center;";

    // --- THE BUILDING BLOCKS ---
    private final VBox root;                // The main container holding everything
    private final Label scoreLabel;         // Text showing "Q 1/10 | Score: 0"
    private final Label difficultyLabel;    // The colored badge (EASY, HARD, etc.)
    private final Button settingsButton;    // The gear icon "⚙"
    private final Button backButton;        // The "X" to quit the game
    private final VBox questionArea;        // The white box holding the math question
    private final GridPane answersArea;     // 2-column grid holding the 4 answer boxes
    private final Button nextButton;        // The "NEXT" button (hidden initially)
    private final Button playAgainButton;   // The "PLAY AGAIN" button (hidden initially)

    // We keep lists of the answer boxes and badges so we can paint them green/red later.
    private final List<HBox> currentAnswerBoxes = new ArrayList<>();
    private final List<Label> currentLetterBadges = new ArrayList<>();

    private int correctIndex = -1;          // Which answer (0-3) is correct
    private boolean answered = false;       // Has the user clicked yet?

    // --- CALLBACKS ("Phones" to call the Controller) ---
    private Consumer<Integer> onAnswerPicked;
    private Runnable onNext;
    private Runnable onPlayAgain;
    private Runnable onBack;
    private Runnable onSettings;

    /**
     * CONSTRUCTOR — Builds the visual skeleton of the game screen.
     */
    public GameView() {
        scoreLabel = new Label("Q 0/0 | Score: 0");
        scoreLabel.setStyle("-fx-font-size: 13px;");

        difficultyLabel = new Label("");
        difficultyLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 2px 10px; -fx-background-radius: 8; -fx-text-fill: white;");

        // 1. Top right buttons (Settings and Quit)
        settingsButton = new Button("⚙");
        settingsButton.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-font-size: 18px; -fx-cursor: hand;");
        settingsButton.setOnAction(e -> { if (onSettings != null) onSettings.run(); }); // Call the controller

        backButton = new Button("X");
        backButton.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        backButton.setOnAction(e -> { if (onBack != null) onBack.run(); }); // Call the controller

        HBox leftCluster = new HBox(15, scoreLabel, difficultyLabel);
        leftCluster.setAlignment(Pos.CENTER_LEFT);

        HBox rightCluster = new HBox(15, settingsButton, backButton);
        rightCluster.setAlignment(Pos.CENTER_RIGHT);

        BorderPane header = new BorderPane();
        header.setCenter(leftCluster);
        header.setRight(rightCluster);

        // 2. The middle area (Question and Answers)
        questionArea = new VBox();
        questionArea.setPadding(new Insets(12));
        questionArea.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 0 0 0 4px;");

        // Two equal-width columns, 12px gap between them (and between rows).
        answersArea = new GridPane();
        answersArea.setHgap(12);
        answersArea.setVgap(12);
        answersArea.setPadding(new Insets(4, 0, 4, 0));
        for (int c = 0; c < 2; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);          // each column takes half the width
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            answersArea.getColumnConstraints().add(cc);
        }

        VBox contentBox = new VBox(14, questionArea, answersArea);

        // We wrap the content in a ScrollPane so if the math is super long, the user can scroll.
        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");
        VBox.setVgrow(scroll, Priority.ALWAYS); // Let it take up all remaining vertical space

        // 3. The bottom area (Next / Play Again)
        nextButton      = actionButton("NEXT");
        playAgainButton = actionButton("PLAY AGAIN");

        // Hide them both at the start.
        nextButton.setVisible(false);      nextButton.setManaged(false);
        playAgainButton.setVisible(false); playAgainButton.setManaged(false);

        nextButton.setOnAction(e -> { if (onNext != null) onNext.run(); });
        playAgainButton.setOnAction(e -> { if (onPlayAgain != null) onPlayAgain.run(); });

        HBox bottom = new HBox(10, nextButton, playAgainButton);
        bottom.setAlignment(Pos.CENTER);

        // 4. Combine everything into the main root VBox.
        root = new VBox(12, header, scroll, bottom);
        root.setPadding(new Insets(15, 20, 20, 20));
        root.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #777777; -fx-border-width: 1px;");
    }

    // A small helper to style the Next/Play Again buttons identically.
    private Button actionButton(String text) {
        Button b = new Button(text);
        b.setFont(javafx.scene.text.Font.font("Arial", 16));
        b.setPrefSize(150, 35);
        b.setStyle("-fx-background-color: #999999; -fx-text-fill: black; -fx-background-radius: 0; -fx-cursor: hand;");
        return b;
    }

    // --- GETTERS & SETTERS for the GameController ---
    public Node getRoot()             { return root; }
    public Button getBackButton()     { return backButton; }
    public Button getSettingsButton() { return settingsButton; }
    public Button getNextButton()     { return nextButton; }
    public Button getPlayAgainButton(){ return playAgainButton; }

    // These let the controller plug in its own methods to handle clicks.
    public void setOnAnswerPicked(Consumer<Integer> h) { this.onAnswerPicked = h; }
    public void setOnNext(Runnable r)        { this.onNext = r; }
    public void setOnPlayAgain(Runnable r)   { this.onPlayAgain = r; }
    public void setOnBack(Runnable r)        { this.onBack = r; }
    public void setOnSettings(Runnable r)    { this.onSettings = r; }

    /**
     * SHOW QUESTION — Updates the screen with a brand new question and answers.
     *
     * This is the most important method in the view! It wipes the screen clean
     * and draws the new data.
     */
    public void showQuestion(String rawQuestion, String[] rawOptions, int correctIndex,
                             String topic, String difficulty, int questionNumber,
                             int totalQuestions, int score) {

        // 1. Update the header text.
        scoreLabel.setText(topic + " | Q " + questionNumber + "/" + totalQuestions + " | Score: " + score);
        applyDifficultyBadge(difficulty);

        // 2. Draw the new question text (using our custom LaTeX renderer for math).
        TextFlow questionFlow = LatexRenderer.toTextFlow(rawQuestion, 10);
        questionArea.getChildren().setAll(questionFlow);

        // 3. Clear the old answers out of memory and off the screen.
        answersArea.getChildren().clear();
        currentAnswerBoxes.clear();
        currentLetterBadges.clear();
        this.correctIndex = correctIndex;
        this.answered = false;

        // 4. Build the answer boxes — two per row.
        for (int i = 0; i < rawOptions.length; i++) {
            final int picked = i; // Keep track of which button this is (0, 1, 2, or 3)

            // Create the A, B, C, D badge.
            Label letter = new Label(String.valueOf((char) ('A' + i)));
            letter.setStyle(BASE_BADGE_STYLE);
            currentLetterBadges.add(letter);

            // Draw the answer text (using LaTeX for math).
            TextFlow optionFlow = LatexRenderer.toTextFlow(rawOptions[i], 10);

            // Put the badge and the text side-by-side in an HBox.
            HBox answerBox = new HBox(14, letter, optionFlow);
            answerBox.setAlignment(Pos.CENTER_LEFT);
            answerBox.setPadding(new Insets(10, 14, 10, 14));
            answerBox.setMinHeight(75); // Make it nice and big.
            answerBox.setMaxWidth(Double.MAX_VALUE);
            answerBox.setStyle(BASE_ANSWER_STYLE);

            HBox.setHgrow(optionFlow, Priority.ALWAYS);
            GridPane.setHgrow(answerBox, Priority.ALWAYS);
            GridPane.setFillWidth(answerBox, true);

            // Wire up the mouse events to look like a button.
            answerBox.setOnMouseClicked(e -> {
                if (!answered) handlePick(picked); // If they click it, grade it!
            });
            answerBox.setOnMouseEntered(e -> {
                if (!answered) answerBox.setStyle(HOVER_ANSWER_STYLE); // Hover effect
            });
            answerBox.setOnMouseExited(e -> {
                if (!answered) answerBox.setStyle(BASE_ANSWER_STYLE); // Remove hover effect
            });

            // Add it to our list and place it in the grid:
            // A -> (col 0, row 0), B -> (col 1, row 0),
            // C -> (col 0, row 1), D -> (col 1, row 1)
            currentAnswerBoxes.add(answerBox);
            answersArea.add(answerBox, i % 2, i / 2);
        }

        // Hide the bottom buttons until they actually answer.
        nextButton.setVisible(false);      nextButton.setManaged(false);
        playAgainButton.setVisible(false); playAgainButton.setManaged(false);
    }

    /**
     * HANDLE PICK — Grades the question visually.
     *
     * When a user clicks, we paint the correct answer green, and the wrong
     * answer (if they clicked one) red. We also notify the controller so it
     * can update the actual score.
     */
    private void handlePick(int picked) {
        if (answered) return; // Don't let them click twice.
        answered = true;

        // Paint the correct answer green.
        if (correctIndex >= 0 && correctIndex < currentAnswerBoxes.size()) {
            currentAnswerBoxes.get(correctIndex).setStyle(CORRECT_ANSWER_STYLE);
            currentLetterBadges.get(correctIndex).setStyle(CORRECT_BADGE_STYLE);
        }
        // If they picked a wrong answer, paint it red.
        if (picked != correctIndex) {
            currentAnswerBoxes.get(picked).setStyle(WRONG_ANSWER_STYLE);
            currentLetterBadges.get(picked).setStyle(WRONG_BADGE_STYLE);
        }

        // Tell the GameController which button was clicked so it can score it.
        if (onAnswerPicked != null) onAnswerPicked.accept(picked);
    }

    /** Reveals the "NEXT" button at the bottom of the screen. */
    public void showNextButton() {
        nextButton.setText("NEXT");
        nextButton.setVisible(true);
        nextButton.setManaged(true);
    }

    /** Reveals the "PLAY AGAIN" button at the bottom of the screen. */
    public void showPlayAgainButton() {
        playAgainButton.setVisible(true);
        playAgainButton.setManaged(true);
    }

    /**
     * APPLY DIFFICULTY BADGE — Changes the color of the difficulty label.
     * Green for Easy, Orange for Medium, Red for Hard, Purple for Very Hard.
     */
    private void applyDifficultyBadge(String difficulty) {
        if (difficulty == null || difficulty.isEmpty()) {
            difficultyLabel.setText("");
            return;
        }
        String color;
        switch (difficulty) {
            case "Easy":      color = "#2e7d32"; break; // Green
            case "Medium":    color = "#e65100"; break; // Orange
            case "Hard":      color = "#c62828"; break; // Red
            case "Very Hard": color = "#6a1b9a"; break; // Purple
            default:          color = "#555555";        // Grey (fallback)
        }
        difficultyLabel.setText(difficulty.toUpperCase());
        difficultyLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 2px 10px; -fx-background-radius: 8; -fx-text-fill: white; -fx-background-color: " + color + ";");
    }
}