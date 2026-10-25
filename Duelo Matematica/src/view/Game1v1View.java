package view;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 1v1 GAME VIEW — GameView's visual design + Game1v1's rules.
 *
 * Answer boxes use the same two-column, A/B/C/D-badge design as GameView,
 * including hover and correct-green / wrong-red reveal. The health bar,
 * platform, and green/red boxes encode the 1v1 push rule:
 *   - Correct answer  -> green box advances, red box is pushed right.
 *   - Wrong answer    -> green box retreats, red box follows left.
 *   - 3 mistakes / pushed off left edge  -> DEFEAT
 *   - Red box pushed off right edge     -> VICTORY
 */
public class Game1v1View {

    // ---------- Answer-box styles (same as GameView) ----------
    // Normal look: fully transparent background, subtle grey border
    private static final String BASE_ANSWER_STYLE =
        "-fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;";

    // Hover look: slight white tint so the user knows it's clickable, darker border
    private static final String HOVER_ANSWER_STYLE =
        "-fx-background-color: rgba(255, 255, 255, 0.2); -fx-border-color:transparent; -fx-cursor: hand;";
    
    // Correct look: semi-transparent green (the 0.6 at the end is the transparency level)
    private static final String CORRECT_ANSWER_STYLE =
        "-fx-background-color: rgba(200, 230, 201, 0.6); -fx-border-color: transparent;";

    // Wrong look: semi-transparent red
    private static final String WRONG_ANSWER_STYLE =
        "-fx-background-color: rgba(255, 205, 210, 0.6); -fx-border-color: #c62828; -fx-border-width: 3px;";

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

    // ---------- Structure ----------
    private final StackPane rootStack;
    private final BorderPane gameLayout;

    private final Label categoryLabel;
    private final VBox questionContainer;
    private final GridPane answersArea;               // 2-column grid (GameView style)

    private final List<StackPane> currentAnswerBoxes = new ArrayList<>();
    private final List<Label> currentLetterBadges = new ArrayList<>();

    // 1v1 health / push visuals
    private final Rectangle greenBox;
    private final Rectangle redBox;
    private final Region redHealth;
    private final Region greenHealth;

    private final Button surrenderButton;
    private final Button gearButton;

    private int currentCorrectIndex = -1;
    private boolean answered = false;

    // GameView-style callback: tells the controller which 0..3 option was clicked.
    private Consumer<Integer> onAnswerPicked;

    public Game1v1View() {
        rootStack = new StackPane();
        gameLayout = new BorderPane();
        gameLayout.setStyle("-fx-background-color: transparent;");

        // --- Top bar (Surrender) ---
        surrenderButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_concede.png", 48, null);//ViewUtils.createSpriteButton("/buttons/btn.png", "SURRENDER", null);
        HBox topBar = new HBox(surrenderButton);
        topBar.setPadding(new Insets(10, 15, 0, 15));
        topBar.setAlignment(Pos.TOP_LEFT);

        // --- Bottom bar (Gear) ---
        gearButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_settings.png", 42, null);
        HBox bottomBar = new HBox(gearButton);
        bottomBar.setPadding(new Insets(0, 15, 12, 15));
        bottomBar.setAlignment(Pos.BOTTOM_LEFT);

        // --- Question area ---
        categoryLabel = new Label("CATEGORY");
        //categoryLabel.setAlignment(Pos.CENTER);
        categoryLabel.setFont(ViewUtils.getCustomFont(18));
        categoryLabel.setStyle("-fx-background-color: transparent;");

        HBox categoryContainer = new HBox(categoryLabel);
        categoryContainer.setAlignment(Pos.CENTER);
        categoryContainer.setPadding(new Insets(10, 0, 10, 0)); 
        
        questionContainer = new VBox();
        questionContainer.setPadding(new Insets(12));
        questionContainer.setAlignment(Pos.CENTER);
        questionContainer.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        
        // --- Answers: two equal-width columns (GameView design) ---
        answersArea = new GridPane();
        answersArea.setHgap(12);
        answersArea.setVgap(12);
        answersArea.setPadding(new Insets(4, 0, 4, 0));
        for (int c = 0; c < 2; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            answersArea.getColumnConstraints().add(cc);
        }

        VBox contentBox = new VBox(14, questionContainer, answersArea);
        contentBox.setMinHeight(80); 
        //contentBox.setPadding(new Insets(4, 40, 4, 40));

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");
        scroll.setMaxWidth(760);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        
        VBox scrollContainer = new VBox(scroll);
        scrollContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(scrollContainer, Priority.ALWAYS); 
        
        VBox topContentWrapper = new VBox(categoryContainer, scrollContainer);
        topContentWrapper.setAlignment(Pos.TOP_CENTER);
        topContentWrapper.setPadding(new Insets(45, 50, 45, 50)); 

        StackPane backgroundContainer = new StackPane();      
        backgroundContainer.setPrefSize(720, 480);
        backgroundContainer.setMinSize(720, 480);
        backgroundContainer.setMaxSize(720, 480);
        
        NineSlice scrollBg = ViewUtils.createAutoNineSlice("/panels/ui_scroll.png", 3.0, true);
        
        // Add the background first, then the content on top
        backgroundContainer.getChildren().addAll(scrollBg, topContentWrapper);
        
        // --- 1v1 platform: health bar + green/red boxes ---
        Pane platformPane = new Pane();
        platformPane.setPrefSize(480, 70);
        platformPane.setMaxSize(480, 70);
        
        StackPane platformContainer = new StackPane(platformPane);
        platformContainer.setAlignment(Pos.CENTER);
        platformContainer.setPadding(new Insets(15, 0, 0, 0));

        greenHealth = new Region();
        greenHealth.setStyle("-fx-background-color: #97c486; -fx-background-radius: 2px;");

        redHealth = new Region();
        redHealth.setStyle("-fx-background-color: #a34548; -fx-background-radius: 2px;");

        double initialGreenWidth = 222.5;
        double initialRedWidth = 480.0 - initialGreenWidth;
        greenHealth.setPrefWidth(initialGreenWidth);
        redHealth.setPrefWidth(initialRedWidth);

        HBox healthBars = new HBox(greenHealth, redHealth);
        healthBars.setPrefSize(480, 36);
        healthBars.setMaxSize(480, 36);
        healthBars.layoutXProperty().bind(
            platformPane.widthProperty().subtract(healthBars.widthProperty()).divide(2));
        healthBars.layoutYProperty().bind(
            platformPane.heightProperty().subtract(healthBars.heightProperty()).divide(2));

        Rectangle clip = new Rectangle(480, 36);
        clip.setArcWidth(4);
        clip.setArcHeight(4);
        healthBars.setClip(clip);

        Region frame = new Region();
        frame.setStyle("-fx-border-color: #3cd1e0; -fx-border-width: 3px;" +
                       " -fx-border-radius: 4px; -fx-background-color: transparent;");
        frame.setPrefSize(480, 36);
        frame.layoutXProperty().bind(
            platformPane.widthProperty().subtract(frame.widthProperty()).divide(2));
        frame.layoutYProperty().bind(
            platformPane.heightProperty().subtract(frame.heightProperty()).divide(2));

        greenBox = new Rectangle(35, 50);
        greenBox.setFill(Color.web("#00a800"));
        greenBox.setStroke(Color.web("#005a00"));
        greenBox.setStrokeWidth(2);
        greenBox.layoutXProperty().bind(
            platformPane.widthProperty().subtract(greenBox.widthProperty()).divide(2));
        greenBox.layoutYProperty().bind(
            platformPane.heightProperty().subtract(greenBox.heightProperty()).divide(2));

        redBox = new Rectangle(35, 50);
        redBox.setFill(Color.web("#d00000"));
        redBox.setStroke(Color.web("#7a0000"));
        redBox.setStrokeWidth(2);
        redBox.layoutXProperty().bind(
            platformPane.widthProperty().subtract(redBox.widthProperty()).divide(2));
        redBox.layoutYProperty().bind(
            platformPane.heightProperty().subtract(redBox.heightProperty()).divide(2));

        platformPane.getChildren().addAll(healthBars, frame, greenBox, redBox);

        // --- Center composition ---
        VBox centerContent = new VBox(15, backgroundContainer, platformContainer);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(10, 40, 10, 40));
        
        VBox.setVgrow(backgroundContainer, Priority.ALWAYS);

        gameLayout.setTop(topBar);
        gameLayout.setCenter(centerContent);
        gameLayout.setBottom(bottomBar);

        rootStack.getChildren().add(gameLayout);
        
    }

    // --- Public API ---
    public Node getRoot() { return rootStack; }
    public Button getSurrenderButton() { return surrenderButton; }
    public Button getGearButton() { return gearButton; }
    public int getCurrentCorrectIndex() { return currentCorrectIndex; }

    /** Same callback-style wiring that GameView uses. */
    public void setOnAnswerPicked(Consumer<Integer> handler) { this.onAnswerPicked = handler; }

    public void showQuestion(Question q) {
        categoryLabel.setText(q.getTopic().toUpperCase() + " (" + q.getDifficulty() + ")");

        TextFlow questionFlow = LatexRenderer.toTextFlow(q.getText(), 10);
        questionContainer.getChildren().setAll(questionFlow);

        List<String> optionsList = new ArrayList<>();
        optionsList.add(q.getCorrectAnswer());
        Collections.addAll(optionsList, q.getWrongAnswers());
        Collections.shuffle(optionsList);

        String[] shuffled = optionsList.toArray(new String[0]);
        currentCorrectIndex = -1;
        for (int i = 0; i < shuffled.length; i++) {
            if (shuffled[i].equals(q.getCorrectAnswer())) { currentCorrectIndex = i; break; }
        }
        setOptions(shuffled);
    }

    public void setOptions(String[] options) {
        answersArea.getChildren().clear();
        currentAnswerBoxes.clear();
        currentLetterBadges.clear();
        answered = false;

        for (int i = 0; i < 4 && i < options.length; i++) {
            final int picked = i;

            // 1. Create the Badge (A, B, C, D)
            //Label letter = new Label(String.valueOf((char) ('A' + i)));
            //letter.setFont(ViewUtils.getCustomFont(15));
           // letter.setStyle(BASE_BADGE_STYLE);
           // currentLetterBadges.add(letter);

            // 2. Create the Text
            TextFlow optionFlow = LatexRenderer.toTextFlow(options[i], 10);
            optionFlow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            // --- FIX: Wrap TextFlow in a VBox to force vertical centering ---
            VBox textWrapper = new VBox(optionFlow);
            textWrapper.setAlignment(Pos.CENTER);
            textWrapper.setMouseTransparent(true);
            // ---------------------------------------------------------------

            // 3. Use BorderPane to put Badge on Left, Text in Center
            BorderPane contentLayout = new BorderPane();
            contentLayout.setPadding(new Insets(10, 15, 10, 15));
            contentLayout.setMouseTransparent(true); 

            //contentLayout.setLeft(letter);
            
            // Set the WRAPPER as the center instead of the raw TextFlow
            contentLayout.setCenter(textWrapper);
            
            //BorderPane.setAlignment(letter, Pos.CENTER_LEFT); 
            BorderPane.setAlignment(textWrapper, Pos.CENTER); 

            // 4. Outer StackPane for the background
            StackPane answerContainer = new StackPane();
            answerContainer.setMinHeight(75);
            answerContainer.setMaxWidth(Double.MAX_VALUE);
            answerContainer.setMinWidth(0); 
            answerContainer.setStyle(BASE_ANSWER_STYLE);
            answerContainer.setAlignment(Pos.CENTER); 

            // 5. Add the NineSlice Background
            NineSlice bg = ViewUtils.createAutoNineSlice("/misc/ui_answer_selection.png", 3.0, true);
            if (bg != null) {
                bg.setMouseTransparent(true);
                bg.setMinSize(0, 0);
                bg.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                answerContainer.getChildren().add(bg); 
            }

            // 6. Add the BorderPane content on top
            answerContainer.getChildren().add(contentLayout); 

            GridPane.setHgrow(answerContainer, Priority.ALWAYS);
            GridPane.setFillWidth(answerContainer, true);

            // 7. Mouse events
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), answerContainer);
            scaleUp.setToX(1.04); // Grow by 4%
            scaleUp.setToY(1.04);

            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), answerContainer);
            scaleDown.setToX(1.0); // Shrink back to normal
            scaleDown.setToY(1.0);

            answerContainer.setOnMouseClicked(e -> { if (!answered) handlePick(picked); });
            
            answerContainer.setOnMouseEntered(e -> { 
                if (!answered) {
                    answerContainer.setStyle(HOVER_ANSWER_STYLE);
                    scaleUp.playFromStart();
                    answerContainer.toFront(); // Bring to front so the glow overlaps neighbors
                } 
            });
            
            answerContainer.setOnMouseExited(e -> { 
                if (!answered) {
                    answerContainer.setStyle(BASE_ANSWER_STYLE);
                    scaleDown.playFromStart();
                } 
            });
            currentAnswerBoxes.add(answerContainer);
            answersArea.add(answerContainer, i % 2, i / 2);
        }
    }

    /** Reveal correct/wrong (GameView behaviour), then notify the controller. */
    private void handlePick(int picked) {
        if (answered) return;
        answered = true;

		/*
		 * if (currentCorrectIndex >= 0 && currentCorrectIndex <
		 * currentAnswerBoxes.size()) {
		 * currentAnswerBoxes.get(currentCorrectIndex).setStyle(CORRECT_ANSWER_STYLE);
		 * currentLetterBadges.get(currentCorrectIndex).setStyle(CORRECT_BADGE_STYLE); }
		 * if (picked != currentCorrectIndex && picked >= 0 && picked <
		 * currentAnswerBoxes.size()) {
		 * currentAnswerBoxes.get(picked).setStyle(WRONG_ANSWER_STYLE);
		 * currentLetterBadges.get(picked).setStyle(WRONG_BADGE_STYLE); }
		 */
        if (onAnswerPicked != null) onAnswerPicked.accept(picked);
    }

    public void disableOptions() {
        answered = true;
        for (StackPane box : currentAnswerBoxes) {
            box.setDisable(true);
            box.setCursor(javafx.scene.Cursor.DEFAULT);
        }
    }

    /** 1v1 rule visualisation: pushes the boxes and rebalances the health bar. */
    public void animateBoxes(double targetGreenX, double targetRedX, Runnable onFinished) {
        TranslateTransition greenAnim = new TranslateTransition(Duration.millis(350), greenBox);
        greenAnim.setToX(targetGreenX);
        greenAnim.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition redAnim = new TranslateTransition(Duration.millis(350), redBox);
        redAnim.setToX(targetRedX);
        redAnim.setInterpolator(Interpolator.EASE_OUT);

        double splitPoint = 240.0 + (targetGreenX + targetRedX) / 2.0;
        splitPoint = Math.max(0.0, Math.min(480.0, splitPoint));
        double targetGreenWidth = splitPoint;
        double targetRedWidth = 480.0 - splitPoint;

        Timeline healthAnim = new Timeline(
            new KeyFrame(Duration.millis(350),
                new KeyValue(greenHealth.prefWidthProperty(), targetGreenWidth, Interpolator.EASE_OUT),
                new KeyValue(redHealth.prefWidthProperty(), targetRedWidth, Interpolator.EASE_OUT)
            )
        );

        ParallelTransition syncAnim = new ParallelTransition(greenAnim, redAnim, healthAnim);
        syncAnim.setOnFinished(e -> { if (onFinished != null) onFinished.run(); });
        syncAnim.play();
    }

    public void showOverlay(Node overlay) { rootStack.getChildren().add(overlay); }
    public void hideOverlay(Node overlay) { rootStack.getChildren().remove(overlay); }

    public void showGameOverPopup(boolean isWin, Runnable onReturnToMenu) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        VBox popupBox = new VBox(20);
        popupBox.setAlignment(Pos.CENTER);
        popupBox.setMaxSize(400, 250);
        popupBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;" +
                          " -fx-border-color: #333333; -fx-border-width: 3px;" +
                          " -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label titleLabel = new Label(isWin ? "VICTORY!" : "DEFEAT");
        titleLabel.setFont(ViewUtils.getCustomFont(36));
        titleLabel.setStyle("-fx-text-fill: " + (isWin ? "#00a800;" : "#d00000;") + " -fx-font-weight: bold;");

        Label messageLabel = new Label(isWin ? "You defeated the opponent!"
                                             : "You made 3 mistakes or got pushed off!");
        messageLabel.setFont(ViewUtils.getCustomFont(16));
        messageLabel.setStyle("-fx-text-fill: #333333;");

        Button returnBtn = ViewUtils.createSpriteButton("/buttons/btn.png", "RETURN TO MENU", null);
        returnBtn.setFont(ViewUtils.getCustomFont(16));
        returnBtn.setOnAction(e -> {
            rootStack.getChildren().remove(overlay);
            if (onReturnToMenu != null) onReturnToMenu.run();
        });

        popupBox.getChildren().addAll(titleLabel, messageLabel, returnBtn);
        overlay.getChildren().add(popupBox);
        rootStack.getChildren().add(overlay);
    }
}