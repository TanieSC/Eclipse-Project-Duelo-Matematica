package view;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * STORY LEVELS VIEW — The map screen for selecting a story level.
 */
public class StoryLevelsView {

    private final StackPane root;
    private final Pane gamePane;
    private final Rectangle greenAsset;
    private final Button[] levelButtons = new Button[3];
    private final Button pauseButton;
    private final Button gearButton;
    private TranslateTransition currentTransition;

    public StoryLevelsView() {
        gamePane = new Pane();

        // Diagonal target positions
        double[][] buttonPositions = {
            {140, 340}, // Level 1
            {370, 230}, // Level 2
            {600, 60}   // Level 3
        };

        // Green UI Asset Placeholder
        greenAsset = new Rectangle(55, 55);
        greenAsset.setFill(Color.web("#00a800"));
        greenAsset.setStroke(Color.BLACK);
        greenAsset.setStrokeWidth(2);
        greenAsset.setCache(true);
        greenAsset.setCacheHint(CacheHint.SPEED);

        // Initial position above button 1
        greenAsset.setTranslateX(buttonPositions[0][0]);
        greenAsset.setTranslateY(buttonPositions[0][1] - 65);
        gamePane.getChildren().add(greenAsset);

        // Red Square Buttons
        for (int i = 0; i < buttonPositions.length; i++) {
            double targetX = buttonPositions[i][0];
            double targetY = buttonPositions[i][1];

            Button redBtn = createRedButton();
            redBtn.setLayoutX(targetX);
            redBtn.setLayoutY(targetY);
            redBtn.setUserData(new double[]{targetX, targetY - 65}); // Store target coords

            levelButtons[i] = redBtn;
            gamePane.getChildren().add(redBtn);
        }

        // Overlay Buttons
        pauseButton = createPauseButton();
        gearButton = createGearButton();

        root = new StackPane(gamePane, pauseButton, gearButton);
        StackPane.setAlignment(pauseButton, Pos.TOP_LEFT);
        StackPane.setMargin(pauseButton, new Insets(20));
        StackPane.setAlignment(gearButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(gearButton, new Insets(20));
    }

    public Node getRoot() { return root; }
    public Button[] getLevelButtons() { return levelButtons; }
    public Button getPauseButton() { return pauseButton; }
    public Button getGearButton() { return gearButton; }

    public void animateAssetTo(double targetX, double targetY, Runnable onComplete) {
        if (Math.abs(greenAsset.getTranslateX() - targetX) < 1.0 && 
            Math.abs(greenAsset.getTranslateY() - targetY) < 1.0) {
            if (onComplete != null) onComplete.run();
            return;
        }

        if (currentTransition != null) currentTransition.stop();

        currentTransition = new TranslateTransition(Duration.millis(500), greenAsset);
        currentTransition.setInterpolator(Interpolator.EASE_BOTH);
        currentTransition.setToX(targetX);
        currentTransition.setToY(targetY);
        currentTransition.setOnFinished(e -> {
            if (onComplete != null) onComplete.run();
        });
        currentTransition.play();
    }

    private Button createRedButton() {
        Button button = new Button();
        button.setPrefSize(55, 55);
        button.setStyle("-fx-background-color: #a00000; -fx-border-color: #000000; -fx-border-width: 2px; -fx-cursor: hand;");
        return button;
    }

    private Button createPauseButton() {
        Button btn = new Button("II");
        btn.setPrefSize(48, 48);
        btn.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #e6e6e6; -fx-border-color: #000000; -fx-border-width: 2px; -fx-cursor: hand;");
        return btn;
    }

    private Button createGearButton() {
        Button gearBtn = new Button("\u2699");
        gearBtn.setPrefSize(48, 48);
        gearBtn.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 50%; -fx-background-radius: 50%; -fx-background-color: #f9f9f9; -fx-cursor: hand;");
        return gearBtn;
    }
}
