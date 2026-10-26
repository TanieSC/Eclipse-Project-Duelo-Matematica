package view;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * STORY LEVELS VIEW — The map screen for selecting a story level.
 */
public class StoryLevelsView {

    private final StackPane root;
    private final Pane gamePane;
    private final Button pauseButton;
    private final Button gearButton;
    
    private final Rectangle greenBox;
    private TranslateTransition currentTransition;

    // Callback to tell the controller when a level is clicked
    private Consumer<Integer> onLevelSelected;

    // The X/Y targets for each landmark on the map
    private final double[][] landmarkPositions = {
        {185, 345}, // 0: House (Level 1)
        {400, 260}, // 1: Mountain (Level 2)
        {635, 90}   // 2: Castle (Level 3) - Top Right
    };

    public StoryLevelsView() {
        // 1. Load the map
        InputStream stream = getClass().getResourceAsStream("/_assets/misc/ui_map_level.png");
        if (stream == null) {
            System.err.println("CRITICAL ERROR: Map image NOT FOUND at /_assets/misc/ui_map_level.png");
        }
        Image mapImg = new Image(stream);
        ImageView mapView = new ImageView(mapImg);

        double mapWidth = 760;  
        double mapHeight = 480; 

        mapView.setFitWidth(mapWidth);
        mapView.setFitHeight(mapHeight);
        mapView.setPreserveRatio(false); 

        // 2. Setup the Game Pane
        gamePane = new Pane();
        gamePane.setPrefSize(mapWidth, mapHeight);
        gamePane.setMaxSize(mapWidth, mapHeight);

        // 3. SETUP THE GREEN BOX (Player Avatar)
        greenBox = new Rectangle(25, 25);
        greenBox.setFill(Color.web("#00a800"));
        greenBox.setStroke(Color.BLACK);
        greenBox.setStrokeWidth(2);
        
        greenBox.setTranslateX(landmarkPositions[0][0]);
        greenBox.setTranslateY(landmarkPositions[0][1] - 50); 

        // 4. ADD THE INTERACTIVE HOVER ZONES
        Node houseZone = createHoverZone(landmarkPositions[0][0], landmarkPositions[0][1], 40, 1);
        Node mountainZone = createHoverZone(landmarkPositions[1][0], landmarkPositions[1][1], 45, 2);
        Node castleZone = createHoverZone(landmarkPositions[2][0], landmarkPositions[2][1], 45, 3);

        gamePane.getChildren().addAll(greenBox, houseZone, mountainZone, castleZone);

        // 5. Setup UI buttons
        pauseButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_pause.png", 48, null);
        gearButton  = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_settings.png", 48, null);

        // 6. Assemble Everything
        StackPane interactiveMap = new StackPane(mapView, gamePane);
        root = new StackPane(interactiveMap, pauseButton, gearButton);
        
        StackPane.setAlignment(pauseButton, Pos.TOP_LEFT);
        StackPane.setMargin(pauseButton, new Insets(20));
        
        StackPane.setAlignment(gearButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(gearButton, new Insets(20));
    }

    private Node createHoverZone(double x, double y, double radius, int levelNumber) {
        // Invisible clickable hitbox
        Circle hitbox = new Circle(radius, Color.rgb(0, 0, 0, 0.01));
        hitbox.setLayoutX(x);
        hitbox.setLayoutY(y);
        hitbox.setCursor(Cursor.HAND); 

        // Glowing Ring
        Circle glowRing = new Circle(radius);
        glowRing.setFill(Color.TRANSPARENT);
        glowRing.setStroke(Color.web("#FFD700")); // Gold
        glowRing.setStrokeWidth(3);
        glowRing.setMouseTransparent(true); 
        
        DropShadow glow = new DropShadow(BlurType.GAUSSIAN, Color.web("#FFD700"), 15, 0.6, 0, 0);
        glowRing.setEffect(glow);
        glowRing.setOpacity(0);
        
        glowRing.setLayoutX(x);
        glowRing.setLayoutY(y);

        // Pulse Animation
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glowRing.scaleXProperty(), 1.0)),
            new KeyFrame(Duration.ZERO, new KeyValue(glowRing.scaleYProperty(), 1.0)),
            new KeyFrame(Duration.millis(600), new KeyValue(glowRing.scaleXProperty(), 1.15)),
            new KeyFrame(Duration.millis(600), new KeyValue(glowRing.scaleYProperty(), 1.15)),
            new KeyFrame(Duration.millis(1200), new KeyValue(glowRing.scaleXProperty(), 1.0)),
            new KeyFrame(Duration.millis(1200), new KeyValue(glowRing.scaleYProperty(), 1.0))
        );
        pulse.setCycleCount(Timeline.INDEFINITE);

        // Hover Events
        hitbox.setOnMouseEntered(e -> {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), glowRing);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            pulse.play();
        });

        hitbox.setOnMouseExited(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), glowRing);
            fadeOut.setToValue(0.0);
            fadeOut.play();
            pulse.stop();
            glowRing.setScaleX(1.0); 
            glowRing.setScaleY(1.0);
        });

        // Click Event
        hitbox.setOnMouseClicked(e -> {
            moveGreenBox(x, y - 50, () -> {
                if (onLevelSelected != null) {
                    onLevelSelected.accept(levelNumber);
                }
            });
        });

        Pane zonePane = new Pane(hitbox, glowRing);
        zonePane.setPickOnBounds(false);
        return zonePane;
    }

    private void moveGreenBox(double targetX, double targetY, Runnable onComplete) {
        if (currentTransition != null) currentTransition.stop();

        currentTransition = new TranslateTransition(Duration.millis(500), greenBox);
        currentTransition.setInterpolator(Interpolator.EASE_BOTH);
        currentTransition.setToX(targetX);
        currentTransition.setToY(targetY);
        currentTransition.setOnFinished(e -> {
            if (onComplete != null) onComplete.run();
        });
        currentTransition.play();
    }

    public void setOnLevelSelected(Consumer<Integer> handler) {
        this.onLevelSelected = handler;
    }

    public Node getRoot() { return root; }
    public Button getPauseButton() { return pauseButton; }
    public Button getGearButton() { return gearButton; }
}