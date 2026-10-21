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

/**
 * STORY SCREEN VIEW — The cinematic text reveal screen.
 */
public class StoryScreenView {

    private final BorderPane root;
    private final VBox storyContentBox;
    private final ScrollPane scrollPane;
    private final Button pauseButton;
    private final Button skipButton;

    public StoryScreenView() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #000000;");

        // Top Bar
        pauseButton = new Button("II");
        pauseButton.setPrefSize(48, 48);
        pauseButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-cursor: hand;");

        skipButton = new Button("SKIP >");
        skipButton.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #ffffff; -fx-cursor: hand;");

        BorderPane topBar = new BorderPane();
        topBar.setLeft(pauseButton);
        topBar.setRight(skipButton);
        topBar.setPadding(new Insets(20));

        // Story Container
        storyContentBox = new VBox(20);
        storyContentBox.setAlignment(Pos.CENTER);
        storyContentBox.setPadding(new Insets(40, 20, 100, 20));

        scrollPane = new ScrollPane(storyContentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #000000; -fx-background-color: #000000; -fx-viewport-background: #000000;");

        root.setTop(topBar);
        root.setCenter(scrollPane);
    }

    public Node getRoot() { return root; }
    public Button getPauseButton() { return pauseButton; }
    public Button getSkipButton() { return skipButton; }

    public void addLine(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        label.setWrapText(true);
        storyContentBox.getChildren().add(label);

        // Auto-scroll to bottom with soft animation
        Timeline scrollAnimation = new Timeline(
            new KeyFrame(Duration.millis(400), new KeyValue(scrollPane.vvalueProperty(), 1.0, Interpolator.EASE_BOTH))
        );
        scrollAnimation.play();
    }
}
