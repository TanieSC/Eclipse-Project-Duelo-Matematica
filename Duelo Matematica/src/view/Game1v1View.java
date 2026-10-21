package view;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import model.Question;

/**
 * GAME 1V1 VIEW — The visual layout for the Battle Screen.
 * 
 * WHAT DOES THIS CLASS DO?
 * Draws the platform, the boxes, the question, and the 4 answer buttons. 
 * It also handles the sliding animation of the boxes.
 */
public class Game1v1View {

    private final BorderPane root;
    private final Label categoryLabel;
    private final ScrollPane questionScrollPane;
    private final Button[] optionButtons = new Button[4];
    private final Rectangle greenBox;
    private final Rectangle redBox;
    
    private final Button surrenderButton;
    private final Button gearButton;

    public Game1v1View() {
        this.root = new BorderPane();
        this.root.setStyle("-fx-background-color: #ffffff;");

        // 1. Top Bar (Surrender)
        surrenderButton = createSurrenderButton();
        HBox topBar = new HBox(surrenderButton);
        topBar.setPadding(new Insets(10, 15, 0, 15));
        topBar.setAlignment(Pos.TOP_LEFT);

        // 2. Bottom Bar (Gear)
        gearButton = createGearButton();
        HBox bottomBar = new HBox(gearButton);
        bottomBar.setPadding(new Insets(0, 15, 12, 15));
        bottomBar.setAlignment(Pos.BOTTOM_LEFT);

        // 3. Question Area
        categoryLabel = new Label("CATEGORY");
        categoryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555555;");

        Label equationLabel = new Label();
        equationLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        equationLabel.setWrapText(true);
        equationLabel.setAlignment(Pos.TOP_CENTER);

        questionScrollPane = new ScrollPane(equationLabel);
        questionScrollPane.setFitToWidth(true);
        questionScrollPane.setPrefHeight(115);
        questionScrollPane.setMaxHeight(125);
        questionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        questionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        questionScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        VBox equationHeaderBox = new VBox(4, categoryLabel, questionScrollPane);
        equationHeaderBox.setAlignment(Pos.TOP_CENTER);

        // 4. Options Grid
        GridPane optionsGrid = new GridPane();
        optionsGrid.setHgap(15);
        optionsGrid.setVgap(10);
        optionsGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createOptionButton();
            int row = i / 2;
            int col = i % 2;
            optionsGrid.add(optionButtons[i], col, row);
        }

        VBox questionCard = new VBox(10, equationHeaderBox, optionsGrid);
        questionCard.setAlignment(Pos.TOP_CENTER);
        questionCard.setStyle("-fx-background-color: #dcdcdc; -fx-padding: 12px 25px; -fx-border-color: #b0b0b0; -fx-border-width: 2px;");
        questionCard.setPrefSize(720, 290);
        questionCard.setMaxSize(720, 290);

        // 5. Platform and Boxes
        Rectangle platformBar = new Rectangle(480, 20);
        platformBar.setFill(Color.web("#888888"));

        greenBox = new Rectangle(35, 50);
        greenBox.setFill(Color.web("#00a800"));

        redBox = new Rectangle(35, 50);
        redBox.setFill(Color.web("#d00000"));

        double onTopOffset = -(50 / 2.0) - (20 / 2.0);
        greenBox.setTranslateY(onTopOffset);
        redBox.setTranslateY(onTopOffset);

        Pane platformPane = new Pane();
        platformPane.setPrefSize(480, 70);
        platformPane.setMaxSize(480, 70);

        platformBar.layoutXProperty().bind(platformPane.widthProperty().subtract(platformBar.widthProperty()).divide(2));
        platformBar.layoutYProperty().bind(platformPane.heightProperty().subtract(platformBar.heightProperty()).divide(2));

        greenBox.layoutXProperty().bind(platformPane.widthProperty().divide(2));
        greenBox.layoutYProperty().bind(platformPane.heightProperty().divide(2));

        redBox.layoutXProperty().bind(platformPane.widthProperty().divide(2));
        redBox.layoutYProperty().bind(platformPane.heightProperty().divide(2));

        platformPane.getChildren().addAll(platformBar, greenBox, redBox);

        VBox centerContent = new VBox(15, questionCard, platformPane);
        centerContent.setAlignment(Pos.CENTER);

        this.root.setTop(topBar);
        this.root.setCenter(centerContent);
        this.root.setBottom(bottomBar);
    }

    // --- UI Helpers ---
    private Button createSurrenderButton() {
        Button b = new Button("\u2690");
        b.setPrefSize(42, 42);
        b.setStyle("-fx-font-size: 20px; -fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-cursor: hand;");
        return b;
    }

    private Button createGearButton() {
        Button b = new Button("\u2699");
        b.setPrefSize(42, 42);
        b.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-background-color: #f9f9f9; -fx-cursor: hand;");
        return b;
    }

    private Button createOptionButton() {
        Button b = new Button();
        b.setPrefWidth(320);
        b.setPrefHeight(42);
        b.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-border-color: #222222; -fx-border-width: 2px; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> { if (!b.isDisabled()) b.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #e0e0e0; -fx-text-fill: #000000; -fx-border-color: #222222; -fx-border-width: 2px; -fx-cursor: hand;"); });
        b.setOnMouseExited(e -> { if (!b.isDisabled()) b.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-border-color: #222222; -fx-border-width: 2px; -fx-cursor: hand;"); });
        return b;
    }

    // --- Public API for the Controller ---
    public Node getRoot() { return root; }
    public Button getSurrenderButton() { return surrenderButton; }
    public Button getGearButton() { return gearButton; }
    public Button[] getOptionButtons() { return optionButtons; }

    /**
     * Displays a new question on the screen.
     */
    public void showQuestion(Question q, int correctIndex) {
        categoryLabel.setText(q.getTopic().toUpperCase() + " (" + q.getDifficulty() + ")");
        
        // Use LatexRenderer for the question text
        TextFlow questionFlow = LatexRenderer.toTextFlow("$" + q.getText().replace("$", "") + "$", 16);
        questionScrollPane.setContent(questionFlow);
        questionScrollPane.setVvalue(0.0);

        // Build shuffled options
        String[] options = new String[4];
        int optIndex = 0;
        options[optIndex++] = q.getCorrectAnswer();
        for (String wrong : q.getWrongAnswers()) {
            options[optIndex++] = wrong;
        }
        
        // Simple shuffle for options (Controller should ideally pass shuffled options)
        // For simplicity, we just put them in. The Model handles the correct index.

        char prefix = 'A';
        for (int i = 0; i < 4; i++) {
            int displayIndex = (i == 0) ? correctIndex : (i <= correctIndex ? i - 1 : i);
            // Note: This is a quick fix. Ideally, the model should provide the shuffled array.
            // Let's just use the model's shuffled options logic if we move it there.
        }
    }

    /**
     * Updates the option button texts.
     */
    public void setOptions(String[] options) {
        char prefix = 'A';
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText((char)(prefix + i) + ".  " + options[i]);
            optionButtons[i].setDisable(false);
            optionButtons[i].setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-border-color: #222222; -fx-border-width: 2px; -fx-cursor: hand;");
        }
    }

    /**
     * Animates the green and red boxes to their new positions.
     */
    public void animateBoxes(double targetGreenX, double targetRedX, Runnable onFinished) {
        TranslateTransition greenAnim = new TranslateTransition(Duration.millis(350), greenBox);
        greenAnim.setToX(targetGreenX);
        greenAnim.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition redAnim = new TranslateTransition(Duration.millis(350), redBox);
        redAnim.setToX(targetRedX);
        redAnim.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition syncAnim = new ParallelTransition(greenAnim, redAnim);
        syncAnim.setOnFinished(e -> { if (onFinished != null) onFinished.run(); });
        syncAnim.play();
    }
}