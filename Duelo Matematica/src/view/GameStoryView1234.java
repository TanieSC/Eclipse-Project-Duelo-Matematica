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
import javafx.util.Duration;
import model.GameStoryModel.Question;

public class GameStoryView1234 {

    private final BorderPane root;
    private final Label categoryLabel;
    private final Label equationLabel;
    private final ScrollPane questionScrollPane;
    private final Button[] optionButtons = new Button[4];
    private final Rectangle greenBox;
    private final Rectangle redBox;

    private final Button pauseButton;
    private final Button gearButton;

    public GameStoryView1234() {
        root = new BorderPane();
        // Transparent background
        root.setStyle("-fx-background-color: transparent;");

        pauseButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_settings.png", 42, null);
        HBox topBar = new HBox(pauseButton);
        topBar.setPadding(new Insets(10, 15, 0, 15));
        topBar.setAlignment(Pos.TOP_LEFT);

        gearButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_settings.png", 42, null);
        HBox bottomBar = new HBox(gearButton);
        bottomBar.setPadding(new Insets(0, 15, 12, 15));
        bottomBar.setAlignment(Pos.BOTTOM_LEFT);

        categoryLabel = new Label("CATEGORY");
        categoryLabel.setFont(ViewUtils.getCustomFont(14));
        categoryLabel.setStyle("-fx-text-fill: #555555;");

        equationLabel = new Label();
        equationLabel.setFont(ViewUtils.getCustomFont(18));
        equationLabel.setStyle("-fx-text-fill: #000000;");
        equationLabel.setWrapText(true);
        equationLabel.setAlignment(Pos.TOP_CENTER);

        questionScrollPane = new ScrollPane(equationLabel);
        questionScrollPane.setFitToWidth(true);
        questionScrollPane.setPrefHeight(115);
        questionScrollPane.setMaxHeight(125);
        questionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        questionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        questionScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-viewport-background: transparent; -fx-padding: 0;");

        VBox equationHeaderBox = new VBox(4, categoryLabel, questionScrollPane);
        equationHeaderBox.setAlignment(Pos.TOP_CENTER);

        GridPane optionsGrid = new GridPane();
        optionsGrid.setHgap(15);
        optionsGrid.setVgap(10);
        optionsGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = ViewUtils.createSpriteButton("/buttons/btn.png", "", null);
            int row = i / 2;
            int col = i % 2;
            optionsGrid.add(optionButtons[i], col, row);
        }

        VBox questionCard = new VBox(10, equationHeaderBox, optionsGrid);
        questionCard.setAlignment(Pos.TOP_CENTER);
        questionCard.setStyle("-fx-background-color: rgba(220, 220, 220, 0.9); -fx-padding: 12px 25px; -fx-border-color: #b0b0b0; -fx-border-width: 2px;");
        questionCard.setPrefSize(720, 290);
        questionCard.setMaxSize(720, 290);

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

        root.setTop(topBar);
        root.setCenter(centerContent);
        root.setBottom(bottomBar);
    }

    public Node getRoot() { return root; }
    public Button getPauseButton() { return pauseButton; }
    public Button getGearButton() { return gearButton; }
    public Button[] getOptionButtons() { return optionButtons; }

    public void showQuestion(Question q) {
        categoryLabel.setText(q.category.toUpperCase() + " (" + q.difficulty + ")");
        equationLabel.setText(q.prompt);
        questionScrollPane.setVvalue(0.0);

        char prefix = 'A';
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText((char)(prefix + i) + ".  " + q.options.get(i));
            optionButtons[i].setDisable(false);
        }
    }

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