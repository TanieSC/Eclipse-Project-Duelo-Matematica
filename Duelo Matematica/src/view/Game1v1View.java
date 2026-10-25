package view;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game1v1View {

    private final StackPane rootStack;
    private final BorderPane gameLayout;
    
    private final Label categoryLabel;
    private final VBox questionContainer; 
    private final Button[] optionButtons = new Button[4];
    
    private final Rectangle greenBox;
    private final Rectangle redBox;
    private final Region redHealth;
    private final Region greenHealth;

    private final Button surrenderButton;
    private final Button gearButton;

    private String[] currentShuffledOptions;
    private int currentCorrectIndex;

    public Game1v1View() {
        rootStack = new StackPane();
        gameLayout = new BorderPane();
        gameLayout.setStyle("-fx-background-color: transparent;");

        // 1. Top Bar (Surrender)
        surrenderButton = ViewUtils.createSpriteButton("/buttons/btn.png", "SURRENDER", null);
        HBox topBar = new HBox(surrenderButton);
        topBar.setPadding(new Insets(10, 15, 0, 15));
        topBar.setAlignment(Pos.TOP_LEFT);

        // 2. Bottom Bar (Gear)
        gearButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_settings.png", 42, null);
        HBox bottomBar = new HBox(gearButton);
        bottomBar.setPadding(new Insets(0, 15, 12, 15));
        bottomBar.setAlignment(Pos.BOTTOM_LEFT);

        // 3. Question Area Content
        categoryLabel = new Label("CATEGORY");
        categoryLabel.setFont(ViewUtils.getCustomFont(18));
        categoryLabel.setStyle("-fx-text-fill: #555555;");

        questionContainer = new VBox();
        questionContainer.setAlignment(Pos.CENTER);
        questionContainer.setMinHeight(80); // Give it a base height so short questions don't look weird

        VBox equationHeaderBox = new VBox(10, categoryLabel, questionContainer);
        equationHeaderBox.setAlignment(Pos.TOP_CENTER);

        // 4. Options Grid
        GridPane optionsGrid = new GridPane();
        optionsGrid.setHgap(15);
        optionsGrid.setVgap(15);
        optionsGrid.setAlignment(Pos.CENTER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);
        optionsGrid.getColumnConstraints().addAll(col1, col2);

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createScrollOptionButton();
            int row = i / 2;
            int col = i % 2;
            optionsGrid.add(optionButtons[i], col, row);
            GridPane.setHgrow(optionButtons[i], Priority.ALWAYS);
        }

        // ==========================================
        // 5. ASSEMBLE CONTENT WITH FIXED OPTIONS
        // ==========================================
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.TOP_CENTER); // Align to the top
        contentBox.setPadding(new Insets(60, 80, 60, 80)); 

        // This spacer absorbs all extra vertical space, pushing the options to the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Add header, spacer, and options to the VBox
        contentBox.getChildren().addAll(equationHeaderBox, spacer, optionsGrid);

        ViewUtils.NineSlice scrollBg = ViewUtils.createAutoNineSlice(
                "/_assets/panels/ui_scroll.png", 
                3.0,  
                true  
        );

        StackPane questionCard = new StackPane();
        if (scrollBg != null) {
            questionCard.getChildren().addAll(scrollBg, contentBox);
            scrollBg.prefWidthProperty().bind(questionCard.widthProperty());
            scrollBg.prefHeightProperty().bind(questionCard.heightProperty());
        } else {
            questionCard.getChildren().add(contentBox);
        }

        // FIX: Set a fixed size for the card. This guarantees it NEVER stretches 
        // when the question text grows, keeping the options perfectly fixed on screen.
        questionCard.setPrefSize(720, 480);
        questionCard.setMinSize(720, 480);
        questionCard.setMaxSize(720, 480);

        // 7. Platform and Boxes
        Pane platformPane = new Pane();
        platformPane.setPrefSize(480, 70);
        platformPane.setMaxSize(480, 70);

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
        frame.setStyle("-fx-border-color: #3cd1e0; -fx-border-width: 3px; -fx-border-radius: 4px; -fx-background-color: transparent;");
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

        VBox centerContent = new VBox(25, questionCard, platformPane);
        centerContent.setAlignment(Pos.CENTER);

        gameLayout.setTop(topBar);
        gameLayout.setCenter(centerContent);
        gameLayout.setBottom(bottomBar);

        rootStack.getChildren().add(gameLayout);
    }
    
    // --- Public API for the Controller ---
    public Node getRoot() { return rootStack; }
    public Button getSurrenderButton() { return surrenderButton; }
    public Button getGearButton() { return gearButton; }
    public Button[] getOptionButtons() { return optionButtons; }
    public int getCurrentCorrectIndex() { return currentCorrectIndex; }

    private Button createScrollOptionButton() {
        Button b = new Button();
        b.setMaxWidth(Double.MAX_VALUE);
        
        // Increase minHeight to accommodate up to 3 lines of text
        b.setMinHeight(80); 
        b.setMaxHeight(Double.MAX_VALUE); 
        
        b.setWrapText(true);
        b.setAlignment(Pos.CENTER);
        b.setFont(ViewUtils.getCustomFont(10)); 
        b.setCursor(javafx.scene.Cursor.HAND); 

        String imagePath = "/_assets/misc/ui_input.png"; 
        
        String baseStyle = 
            "-fx-background-image: url('" + imagePath + "'); " +
            "-fx-background-size: 100% 100%; " + 
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #2b1409; " +         
            "-fx-background-repeat: no-repeat; " +
            "-fx-padding: 10px 15px;"; 

        String hoverStyle = 
            "-fx-background-image: url('" + imagePath + "'); " +
            "-fx-background-size: 100% 100%; " +
            "-fx-background-color: rgba(0,0,0,0.1); " + 
            "-fx-text-fill: #2b1409; " +
            "-fx-background-repeat: no-repeat; " +
            "-fx-padding: 10px 15px;";

        b.setStyle(baseStyle);
        b.setOnMouseEntered(e -> { if (!b.isDisabled()) b.setStyle(hoverStyle); });
        b.setOnMouseExited(e -> { if (!b.isDisabled()) b.setStyle(baseStyle); });

        return b;
    }
    
    public void showQuestion(Question q) {
        categoryLabel.setText(q.getTopic().toUpperCase() + " (" + q.getDifficulty() + ")");
        
        TextFlow questionFlow = LatexRenderer.toTextFlow(q.getText(), 10); 
        questionContainer.getChildren().setAll(questionFlow);

        List<String> optionsList = new ArrayList<>();
        optionsList.add(q.getCorrectAnswer());
        Collections.addAll(optionsList, q.getWrongAnswers());
        
        Collections.shuffle(optionsList);
        currentShuffledOptions = optionsList.toArray(new String[0]);
        
        for (int i = 0; i < currentShuffledOptions.length; i++) {
            if (currentShuffledOptions[i].equals(q.getCorrectAnswer())) {
                currentCorrectIndex = i;
                break;
            }
        }
        setOptions(currentShuffledOptions);
    }
    
    public void setOptions(String[] options) {
        String BASE_ANSWER_STYLE = "-fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;";
        
		
		  for (int i = 0; i < 4; i++) { 
			  TextFlow flow = LatexRenderer.toTextFlow(options[i], 10);
			  flow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
			  
			  BorderPane answerBox = new BorderPane(); 
			  answerBox.setPadding(new Insets(10, 15, 10, 15));
			  answerBox.setStyle(BASE_ANSWER_STYLE);
			  answerBox.setMaxHeight(Double.MAX_VALUE);
			  
			  
			  // FIX 4: Wrap the text in an HBox or set max width so it doesn't bleed out
			  VBox textWrapper = new VBox(flow); textWrapper.setAlignment(Pos.CENTER);
			  textWrapper.setMaxHeight(Double.MAX_VALUE); textWrapper.setFillWidth(true);
			  
			 answerBox.setCenter(textWrapper);
			  
			  optionButtons[i].setGraphic(answerBox); optionButtons[i].setText("");
			  optionButtons[i].setDisable(false); optionButtons[i].setVisible(true); 
		  }
		 
    }
    public void disableOptions() {
        for (Button b : optionButtons) {
            b.setDisable(true);
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

    public void showOverlay(javafx.scene.Node overlay) { rootStack.getChildren().add(overlay); }
    public void hideOverlay(javafx.scene.Node overlay) { rootStack.getChildren().remove(overlay); }

    public void showGameOverPopup(boolean isWin, Runnable onReturnToMenu) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        VBox popupBox = new VBox(20);
        popupBox.setAlignment(Pos.CENTER);
        popupBox.setMaxSize(400, 250);
        popupBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px; -fx-border-color: #333333; -fx-border-width: 3px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        Label titleLabel = new Label(isWin ? "VICTORY!" : "DEFEAT");
        titleLabel.setFont(ViewUtils.getCustomFont(36));
        titleLabel.setStyle("-fx-text-fill: " + (isWin ? "#00a800;" : "#d00000;") + " -fx-font-weight: bold;");

        Label messageLabel = new Label(isWin ? "You defeated the opponent!" : "You made 3 mistakes or got pushed off!");
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