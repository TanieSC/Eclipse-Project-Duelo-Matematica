package view;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class StoryScreenView {

    private final BorderPane root;
    private final VBox storyContentBox;
    private final ScrollPane scrollPane;
    private final Button pauseButton;
    private final Button skipButton;

    public StoryScreenView() {
        root = new BorderPane();
        // Transparent background
        root.setStyle("-fx-background-color: transparent;");

        pauseButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_settings.png", 48, null);
        skipButton = ViewUtils.createSpriteButton("/buttons/btn.png", "SKIP >", null);

        BorderPane topBar = new BorderPane();
        topBar.setLeft(pauseButton);
        topBar.setRight(skipButton);
        topBar.setPadding(new Insets(20));

        storyContentBox = new VBox(20);
        storyContentBox.setAlignment(Pos.CENTER);
        storyContentBox.setPadding(new Insets(40, 20, 100, 20));

        scrollPane = new ScrollPane(storyContentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-viewport-background: transparent;");

        root.setTop(topBar);
        root.setCenter(scrollPane);
    }

    public Node getRoot() { return root; }
    public Button getPauseButton() { return pauseButton; }
    public Button getSkipButton() { return skipButton; }

    public void addLine(String text) {
        Label label = new Label(text);
        label.setFont(ViewUtils.getCustomFont(26));
        label.setStyle("-fx-text-fill: #000000;"); // Black text since wood background is light
        label.setWrapText(true);
        storyContentBox.getChildren().add(label);

        Timeline scrollAnimation = new Timeline(
            new KeyFrame(Duration.millis(400), new KeyValue(scrollPane.vvalueProperty(), 1.0, Interpolator.EASE_BOTH))
        );
        scrollAnimation.play();
    }
}