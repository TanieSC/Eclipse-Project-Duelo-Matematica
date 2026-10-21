import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoryLevelsView extends Application {

    private Rectangle greenAsset;
    private TranslateTransition currentTransition;
    private Stage primaryStage;
    private Scene mapScene;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        Pane gamePane = new Pane();

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
            int levelNumber = i + 1;

            Button redBtn = createRedButton();
            redBtn.setLayoutX(targetX);
            redBtn.setLayoutY(targetY);

            // Move green asset, then switch to Story Screen
            redBtn.setOnAction(e -> moveGreenAssetTo(targetX, targetY - 65, () -> loadStoryScreen(levelNumber)));

            gamePane.getChildren().add(redBtn);
        }

        // Overlay Buttons
        Button pauseButton = createPauseButton();
        Button gearButton = createGearButton();

        StackPane root = new StackPane();
        root.getChildren().addAll(gamePane, pauseButton, gearButton);

        StackPane.setAlignment(pauseButton, Pos.TOP_LEFT);
        StackPane.setMargin(pauseButton, new Insets(20));

        StackPane.setAlignment(gearButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(gearButton, new Insets(20));

        mapScene = new Scene(root, 800, 500);
        stage.setTitle("Duelo Matematica");
        stage.setScene(mapScene);
        stage.show();
    }

    /**
     * Glides asset to target coordinates, then runs onComplete action.
     */
    private void moveGreenAssetTo(double targetX, double targetY, Runnable onComplete) {
        if (Math.abs(greenAsset.getTranslateX() - targetX) < 1.0 && 
            Math.abs(greenAsset.getTranslateY() - targetY) < 1.0) {
            if (onComplete != null) onComplete.run();
            return;
        }

        if (currentTransition != null) {
            currentTransition.stop();
        }

        currentTransition = new TranslateTransition(Duration.millis(500), greenAsset);
        currentTransition.setInterpolator(Interpolator.EASE_BOTH);
        currentTransition.setToX(targetX);
        currentTransition.setToY(targetY);
        currentTransition.setOnFinished(e -> {
            if (onComplete != null) onComplete.run();
        });
        currentTransition.play();
    }

    /**
     * Reusable trigger to switch from Map to Story Screen.
     */
    private void loadStoryScreen(int level) {
        StoryScreen storyView = new StoryScreen(
            level,
            () -> primaryStage.setScene(mapScene), // On Skip/Finish -> Return to map
            () -> showPauseDialog()                // On Pause click
        );
        primaryStage.setScene(new Scene(storyView, 800, 500));
    }

    private void showPauseDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(primaryStage);
        alert.setTitle("Pause");
        alert.setHeaderText(null);
        alert.setContentText("Game Paused");
        alert.show();
    }

    private Button createRedButton() {
        Button button = new Button();
        button.setPrefSize(55, 55);
        button.setStyle(
            "-fx-background-color: #a00000; " +
            "-fx-border-color: #000000; " +
            "-fx-border-width: 2px; " +
            "-fx-cursor: hand;"
        );
        return button;
    }

    private Button createPauseButton() {
        Button btn = new Button("II");
        btn.setPrefSize(48, 48);
        btn.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #e6e6e6; " +
            "-fx-border-color: #000000; " +
            "-fx-border-width: 2px; " +
            "-fx-cursor: hand;"
        );
        return btn;
    }

    private Button createGearButton() {
        Button gearBtn = new Button("\u2699");
        gearBtn.setPrefSize(48, 48);
        gearBtn.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-border-color: #000000; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 50%; " +
            "-fx-background-radius: 50%; " +
            "-fx-background-color: #f9f9f9; " +
            "-fx-cursor: hand;"
        );
        return gearBtn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

/**
 * Reusable, self-contained Story View with line-by-line reveal & auto-scrolling.
 */
class StoryScreen extends BorderPane {

    private static final Map<Integer, List<String>> STORY_DATABASE = new HashMap<>();

    static {
        STORY_DATABASE.put(1, Arrays.asList(
            "you are jobless...",
            "....",
            "...",
            "...",
            "you have 0 coins left in your pocket.",
            "a mysterious math warrior appears!",
            "prepare for your first battle!"
        ));
        STORY_DATABASE.put(2, Arrays.asList(
            "level 2 begins...",
            "you enter the local math tavern.",
            "the challenges are getting tougher.",
            "solve fast or go home broke!"
        ));
        STORY_DATABASE.put(3, Arrays.asList(
            "the ultimate math boss awaits...",
            "the final showdown of arithmetic mastery!",
            "victory or defeat!"
        ));
    }

    private final VBox storyContentBox;
    private final ScrollPane scrollPane;
    private Timeline storyTimeline;

    public StoryScreen(int selectedLevel, Runnable onComplete, Runnable onPause) {
        this.setStyle("-fx-background-color: #000000;");

        // --- TOP BAR ---
        Button pauseButton = new Button("II");
        pauseButton.setPrefSize(48, 48);
        pauseButton.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #ffffff; " +
            "-fx-text-fill: #000000; " +
            "-fx-cursor: hand;"
        );
        pauseButton.setOnAction(e -> {
            if (storyTimeline != null) storyTimeline.pause();
            if (onPause != null) onPause.run();
        });

        Button skipButton = new Button("SKIP >");
        skipButton.setStyle(
            "-fx-font-size: 22px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-cursor: hand;"
        );
        skipButton.setOnAction(e -> {
            if (storyTimeline != null) storyTimeline.stop();
            if (onComplete != null) onComplete.run();
        });

        BorderPane topBar = new BorderPane();
        topBar.setLeft(pauseButton);
        topBar.setRight(skipButton);
        topBar.setPadding(new Insets(20));

        // --- STORY CONTAINER ---
        storyContentBox = new VBox(20);
        storyContentBox.setAlignment(Pos.CENTER);
        storyContentBox.setPadding(new Insets(40, 20, 100, 20));

        scrollPane = new ScrollPane(storyContentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #000000; -fx-background-color: #000000; -fx-viewport-background: #000000;");

        this.setTop(topBar);
        this.setCenter(scrollPane);

        // --- START AUTOMATIC LINE-BY-LINE STORY REVEAL ---
        List<String> storyLines = STORY_DATABASE.getOrDefault(selectedLevel, Arrays.asList("Story not found."));
        playStorySequence(storyLines, onComplete);
    }

    /**
     * Appends story lines sequentially and smoothly scrolls down.
     */
    private void playStorySequence(List<String> lines, Runnable onComplete) {
        storyTimeline = new Timeline();

        for (int i = 0; i < lines.size(); i++) {
            final String lineText = lines.get(i);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds((i + 1) * 1.4), e -> {
                Label label = new Label(lineText);
                label.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
                label.setWrapText(true);
                storyContentBox.getChildren().add(label);

                // Auto-scroll to bottom with soft animation
                Timeline scrollAnimation = new Timeline(
                    new KeyFrame(Duration.millis(400), new KeyValue(scrollPane.vvalueProperty(), 1.0, Interpolator.EASE_BOTH))
                );
                scrollAnimation.play();
            });
            storyTimeline.getKeyFrames().add(keyFrame);
        }

        // Action when story reaches end
        storyTimeline.setOnFinished(e -> {
            if (onComplete != null) {
                // Optional delay before proceeding automatically
                Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), evt -> onComplete.run()));
                delay.play();
            }
        });

        storyTimeline.play();
    }
}
