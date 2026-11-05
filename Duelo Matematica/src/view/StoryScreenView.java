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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class StoryScreenView {

    private final StackPane root;
    private final BorderPane layout;
    private final VBox storyContentBox;
    private final ScrollPane scrollPane;
    private final Button pauseButton;
    private final Button skipButton;

    public StoryScreenView() {
        // Changed root to StackPane so we can overlay the popup
        root = new StackPane();
        // Added the map background so it looks like the rest of the game
        ViewUtils.applyTiledBackground("/backgrounds/bg_map_level.png", root);
        
        layout = new BorderPane();
        layout.setStyle("-fx-background-color: black;");

        pauseButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_pause.png", 50, null);
        skipButton = ViewUtils.createSpriteButton("/buttons/btn.png", 120, 60, null);
        skipButton.setText("SKIP >");

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

        layout.setTop(topBar);
        layout.setCenter(scrollPane);
        
        root.getChildren().add(layout);
    }

    public Node getRoot() { return root; }
    public Button getPauseButton() { return pauseButton; }
    public Button getSkipButton() { return skipButton; }

    public void addLine(String text) {
        Label label = ViewUtils.createBodyTextLabel(text);
        label.setStyle("-fx-text-fill: white"); 
        label.setWrapText(true);
        storyContentBox.getChildren().add(label);

        Timeline scrollAnimation = new Timeline(
            new KeyFrame(Duration.millis(400), new KeyValue(scrollPane.vvalueProperty(), 1.0, Interpolator.EASE_BOTH))
        );
        scrollAnimation.play();
    }

    // NEW METHOD: Shows the standard wooden dropdown popup
    public void showPauseOverlay(Runnable onResume, Runnable onExitToMap) {
        VBox popupBox = new VBox(20);
        popupBox.setAlignment(Pos.CENTER);
        popupBox.setPadding(new Insets(40, 60, 40, 60)); 
        
        // Standard popup size (same as Game1v1View)
        popupBox.setPrefSize(450, 250);
        popupBox.setMaxSize(450, 250);

        Label titleLabel = ViewUtils.createPopupHeaderLabel("PAUSED");
        
        Label messageLabel = ViewUtils.createBodyTextLabel("The story has been paused.");
        
        Button resumeBtn = ViewUtils.createSpriteButton("/buttons/btn.png", "RESUME", null);
        Button exitBtn = ViewUtils.createSpriteButton("/buttons/btn.png", "BACK TO MAP", null);
        
        HBox buttonBox = new HBox(15, resumeBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // Resume closes the popup and runs the resume callback
        resumeBtn.setOnAction(e -> {
            ViewUtils.closePopup(root, onResume);
        });

        // Exit closes the popup and runs the exit callback
        exitBtn.setOnAction(e -> {
            ViewUtils.closePopup(root, onExitToMap);
        });

        popupBox.getChildren().addAll(titleLabel, messageLabel, buttonBox);
        
        // Trigger the wooden dropdown animation
        ViewUtils.showOverlayContent(root, popupBox);
    }
}