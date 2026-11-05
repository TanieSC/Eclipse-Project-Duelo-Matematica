
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
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import model.StoryBattleModel.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * STORY BATTLE VIEW — Same visual design as Game1v1View.
 */
public class StoryBattleView {

    private static final String BASE_ANSWER_STYLE =
        "-fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;";

    private static final String HOVER_ANSWER_STYLE =
        "-fx-background-color: rgba(255, 255, 255, 0.2); -fx-border-color:transparent; -fx-cursor: hand;";

    private final StackPane rootStack;
    private final BorderPane gameLayout;

    private final Label categoryLabel;
    private final VBox questionContainer;
    private final GridPane answersArea;

    private final List<StackPane> currentAnswerBoxes = new ArrayList<>();

    // 1v1 health / push visuals
    private final Rectangle greenBox;
    private final Rectangle redBox;
    private final Region redHealth;
    private final Region greenHealth;

    private final Button surrenderButton;
    private final Button gearButton;

    private int currentCorrectIndex = -1;
    private boolean answered = false;

    private Consumer<Integer> onAnswerPicked;

    public StoryBattleView() {
        rootStack = new StackPane();
        gameLayout = new BorderPane();
        gameLayout.setStyle("-fx-background-color: transparent;");

        // --- Top bar (Surrender) ---
        surrenderButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_concede.png", 48, null);
        HBox topBar = new HBox(surrenderButton);
        topBar.setPadding(new Insets(10, 15, 0, 15));
        topBar.setAlignment(Pos.TOP_LEFT);

        // --- Bottom bar (Gear) ---
        gearButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_settings.png", 42, null);
        HBox bottomBar = new HBox(gearButton);
        bottomBar.setPadding(new Insets(0, 15, 12, 15));
        bottomBar.setAlignment(Pos.BOTTOM_LEFT);

        // --- Question area ---
        categoryLabel = ViewUtils.createPopupHeaderLabel("CATEGORY");
        HBox categoryContainer = new HBox(categoryLabel);
        categoryContainer.setAlignment(Pos.CENTER);
        categoryContainer.setPadding(new Insets(10, 0, 10, 0)); 
        
        questionContainer = new VBox();
        questionContainer.setPadding(new Insets(12));
        questionContainer.setAlignment(Pos.CENTER);
        questionContainer.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        
        // --- Answers: two equal-width columns ---
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
        healthBars.layoutXProperty().bind(platformPane.widthProperty().subtract(healthBars.widthProperty()).divide(2));
        healthBars.layoutYProperty().bind(platformPane.heightProperty().subtract(healthBars.heightProperty()).divide(2));

        Rectangle clip = new Rectangle(480, 36);
        clip.setArcWidth(4);
        clip.setArcHeight(4);
        healthBars.setClip(clip);

        Region frame = new Region();
        frame.setStyle("-fx-border-color: #3cd1e0; -fx-border-width: 3px;" +
                       " -fx-border-radius: 4px; -fx-background-color: transparent;");
        frame.setPrefSize(480, 36);
        frame.layoutXProperty().bind(platformPane.widthProperty().subtract(frame.widthProperty()).divide(2));
        frame.layoutYProperty().bind(platformPane.heightProperty().subtract(frame.heightProperty()).divide(2));

        greenBox = new Rectangle(35, 50);
        greenBox.setFill(Color.web("#00a800"));
        greenBox.setStroke(Color.web("#005a00"));
        greenBox.setStrokeWidth(2);
        greenBox.layoutXProperty().bind(platformPane.widthProperty().subtract(greenBox.widthProperty()).divide(2));
        greenBox.layoutYProperty().bind(platformPane.heightProperty().subtract(greenBox.heightProperty()).divide(2));

        redBox = new Rectangle(35, 50);
        redBox.setFill(Color.web("#d00000"));
        redBox.setStroke(Color.web("#7a0000"));
        redBox.setStrokeWidth(2);
        redBox.layoutXProperty().bind(platformPane.widthProperty().subtract(redBox.widthProperty()).divide(2));
        redBox.layoutYProperty().bind(platformPane.heightProperty().subtract(redBox.heightProperty()).divide(2));

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

    public Node getRoot() { return rootStack; }
    public Button getSurrenderButton() { return surrenderButton; }
    public Button getGearButton() { return gearButton; }

    public void setOnAnswerPicked(Consumer<Integer> handler) { this.onAnswerPicked = handler; }

    public void showQuestion(Question q) {
        // THE FIX: Change q.category to q.topic, and q.prompt to q.text
        categoryLabel.setText(q.topic.toUpperCase() + " (" + q.difficulty + ")");
        
        TextFlow questionFlow = LatexRenderer.toTextFlow(q.text, 10);
        questionContainer.getChildren().setAll(questionFlow);

        String[] shuffled = q.options.toArray(new String[0]);
        currentCorrectIndex = q.correctIndex;
        
        setOptions(shuffled);
    }

    private void setOptions(String[] options) {
        answersArea.getChildren().clear();
        currentAnswerBoxes.clear();
        answered = false;

        for (int i = 0; i < 4 && i < options.length; i++) {
            final int picked = i;

            TextFlow optionFlow = LatexRenderer.toTextFlow(options[i], 10);
            optionFlow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            VBox textWrapper = new VBox(optionFlow);
            textWrapper.setAlignment(Pos.CENTER);
            textWrapper.setMouseTransparent(true);

            BorderPane contentLayout = new BorderPane();
            contentLayout.setPadding(new Insets(10, 15, 10, 15));
            contentLayout.setMouseTransparent(true); 
            contentLayout.setCenter(textWrapper);
            BorderPane.setAlignment(textWrapper, Pos.CENTER); 

            StackPane answerContainer = new StackPane();
            answerContainer.setMinHeight(75);
            answerContainer.setMaxWidth(Double.MAX_VALUE);
            answerContainer.setMinWidth(0); 
            answerContainer.setStyle(BASE_ANSWER_STYLE);
            answerContainer.setAlignment(Pos.CENTER); 

            NineSlice bg = ViewUtils.createAutoNineSlice("/misc/ui_answer_selection.png", 3.0, true);
            if (bg != null) {
                bg.setMouseTransparent(true);
                bg.setMinSize(0, 0);
                bg.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                answerContainer.getChildren().add(bg); 
            }

            answerContainer.getChildren().add(contentLayout); 

            GridPane.setHgrow(answerContainer, Priority.ALWAYS);
            GridPane.setFillWidth(answerContainer, true);

            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), answerContainer);
            scaleUp.setToX(1.04);
            scaleUp.setToY(1.04);

            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), answerContainer);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);

            answerContainer.setOnMouseClicked(e -> { if (!answered) handlePick(picked); });
            answerContainer.setOnMouseEntered(e -> { 
                if (!answered) {
                    answerContainer.setStyle(HOVER_ANSWER_STYLE);
                    scaleUp.playFromStart();
                    answerContainer.toFront();
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

    private void handlePick(int picked) {
        if (answered) return;
        answered = true;
        if (onAnswerPicked != null) onAnswerPicked.accept(picked);
    }

    public void disableOptions() {
        answered = true;
        for (StackPane box : currentAnswerBoxes) {
            box.setDisable(true);
            box.setCursor(javafx.scene.Cursor.DEFAULT);
        }
    }

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
    
    // ==========================================
    // INLINE DROPDOWN POPUP
    // ==========================================
    public void showGameOverPopup(boolean isWin, Runnable onReturnToMenu) {
        VBox popupBox = new VBox(20);
        popupBox.setAlignment(Pos.CENTER);
        popupBox.setPadding(new Insets(40, 60, 40, 60)); 
        
        // Lock the size so the wood background wraps around it perfectly
        popupBox.setPrefSize(450, 250);
        popupBox.setMaxSize(450, 250);

        Label titleLabel = new Label(isWin ? "VICTORY!" : "DEFEAT");
        titleLabel.setFont(ViewUtils.getCustomFont(20));
        titleLabel.setStyle("-fx-text-fill: " + (isWin ? "#00a800;" : "#d00000;"));

        Label messageLabel = new Label(isWin ? "You defeated the math warrior!" : "You were defeated...");
        messageLabel.setFont(ViewUtils.getCustomFont(10));
        messageLabel.setStyle("-fx-text-fill: #333333;");
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(TextAlignment.CENTER);

        Button returnBtn = ViewUtils.createSpriteButton("/buttons/btn.png", "RETURN TO MAP", null);
        returnBtn.setFont(ViewUtils.getCustomFont(16));
        
        // Use ViewUtils to roll up the popup cleanly, then trigger the exit callback
        returnBtn.setOnAction(e -> {
            ViewUtils.closePopup(rootStack, onReturnToMenu);
        });

        popupBox.getChildren().addAll(titleLabel, messageLabel, returnBtn);
        
        // Use ViewUtils to drop down the wooden scroll animation
        ViewUtils.showOverlayContent(rootStack, popupBox);
    }
}