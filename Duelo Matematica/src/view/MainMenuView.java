package view;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * MAIN MENU VIEW — The "Window Shell" and Animation Director.
 * 
 * WHAT DOES THIS CLASS DO?
 * This is the ONLY class in the app that owns the actual OS window (the Stage).
 * It acts like a picture frame that holds all the different screens.
 * 
 * It handles TWO different types of screen changes:
 * 1. FULL SCREEN SWAPS: Uses a smooth "Popup" effect (Fade + Scale up).
 * 2. POPUP OVERLAYS: Uses a "Wooden Dropdown/Roll-up" effect (using a 12-frame image).
 */
public class MainMenuView {

    private final Stage stage;          // The actual OS window.
    private final StackPane root;       // The container holding all our screens.
    private final VBox menuContent;      // The default home screen (Title + Login/Register/Exit).    
    private final Region overlay;

    // The three main buttons on the home screen.
    private final Button loginButton;
    private final Button registerButton;
    private final Button exitButton;
    
    // Keeps track of any running animation so we can stop it if the user clicks really fast.
    private Transition activeTransition;

    /**
     * CONSTRUCTOR — Builds the main window and sets up the home menu.
     */
    public MainMenuView(Stage stage) {
        this.stage = stage;
        String titleText = "Duelo X Matematica";

        // Use the sprite header directly (returns a StackPane)
        StackPane headerLabel = ViewUtils.createSpriteHeader("/panels/ui_scroll.png", titleText);

        loginButton = ViewUtils.createSpriteButton("/buttons/btn.png", "LOGIN", null);
        
        registerButton = ViewUtils.createSpriteButton("/buttons/btn.png", 300, 60, null);
        registerButton.setText("CREATE ACCOUNT");
        
        exitButton = ViewUtils.createSpriteButton("/buttons/btn.png","EXIT", null);

        VBox buttonBox = new VBox(15, loginButton, registerButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setFillWidth(true);

        menuContent = new VBox(30, headerLabel, buttonBox);
        menuContent.setAlignment(Pos.CENTER);
        menuContent.setFillWidth(false);
        menuContent.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        overlay.setVisible(false);

        root = new StackPane(menuContent);
        root.setStyle("-fx-background-color: #ffffff;");

        stage.setTitle("Duelo X Matematica");
        stage.setScene(new Scene(root, 900, 700)); 
    }

    // =========================================================
    // 1. FULL SCREEN SWAP — Uses POPUP Transition (Fade + Scale)
    // =========================================================
    /**
     * Changes the whole screen to a new full-screen view (e.g., Game Menu).
     * Plays a smooth fade-in and scale-up "popup" effect.
     */
    public void setContent(Node newNode) {
        // Stop any animation that is currently running.
        if (activeTransition != null) activeTransition.stop();

        // If a popup is currently open, roll it up first before showing the new screen.
        if (root.getChildren().size() >= 3) {
            closePopup(() -> setContent(newNode));
            return;
        }

        // If this screen is already showing, do nothing.
        if (root.getChildren().size() == 1 && root.getChildren().get(0) == newNode) {
            return;
        }

        // Clear the screen completely (removes the main menu) and add the new screen.
        root.getChildren().setAll(newNode);

        // Prepare the new screen for the animation: invisible and slightly shrunk.
        newNode.setOpacity(0);
        newNode.setScaleX(0.9);
        newNode.setScaleY(0.9);

        // Animate opacity from 0 to 1 (Fade In)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), newNode);
        fadeIn.setToValue(1.0);
        
        // Animate scale from 90% to 100% (Pop out)
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(250), newNode);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        // Play both animations at the exact same time.
        activeTransition = new ParallelTransition(fadeIn, scaleIn);
        activeTransition.play();
    }

    // =========================================================
    // 2. POPUP OVERLAY — Uses DROPDOWN Animation (12-Frame Wood)
    // =========================================================
    /**
     * Shows a smaller popup view (e.g., Settings) on top of the current screen.
     * Plays the 12-frame wooden plank dropdown animation.
     */
    public void setOverlayContent(Node newNode) {
        if (activeTransition != null) activeTransition.stop();

        // --- CHECK IF A POPUP IS ALREADY OPEN ---
        // If so, we roll up the old popup while keeping the dark background visible.
        // This prevents the background from flashing.
        if (root.getChildren().size() >= 3) {
            Node oldPopup = root.getChildren().get(root.getChildren().size() - 1);
            
            if (oldPopup instanceof StackPane && ((StackPane) oldPopup).getClip() != null) {
                StackPane oldContainer = (StackPane) oldPopup;
                Rectangle clip = (Rectangle) oldContainer.getClip();
                
                // Animate the "curtain" mask rolling up.
                Timeline clipTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(clip.heightProperty(), clip.getHeight())),
                    new KeyFrame(Duration.millis(250), new KeyValue(clip.heightProperty(), 0, Interpolator.EASE_IN))
                );

                // Animate the 12-frame wood in reverse.
                Timeline woodTimeline = null;
                Node graphicNode = oldContainer.getChildren().get(0);
                if (graphicNode instanceof ImageView) {
                    ImageView woodBackground = (ImageView) graphicNode;
                    Image woodImg = woodBackground.getImage();
                    double frameWidth = woodImg.getWidth() / 12.0;
                    double frameHeight = woodImg.getHeight();
                    woodTimeline = new Timeline();
                    for (int i = 0; i < 12; i++) {
                        final int frame = 11 - i; // Reverse order
                        woodTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * 20), e -> {
                            woodBackground.setViewport(new Rectangle2D(frame * frameWidth, 0, frameWidth, frameHeight));
                        }));
                    }
                }

                // Play the roll-up animation.
                ParallelTransition rollUp = new ParallelTransition(clipTimeline);
                if (woodTimeline != null) rollUp.getChildren().add(woodTimeline);
                
                rollUp.setOnFinished(e -> {
                    // Remove ONLY the old popup, KEEP the dark overlay.
                    root.getChildren().remove(oldContainer); 
                    // Now drop down the new popup.
                    setOverlayContent(newNode); 
                });
                
                rollUp.play();
                return; // Stop here and wait for the roll-up to finish.
            }
        }

        // --- DROP DOWN THE NEW POPUP ---
        
        // 1. Setup the dark background
        Region darkOverlay;
        if (root.getChildren().size() >= 2) {
            // Reuse the existing dark overlay if it's already there.
            darkOverlay = (Region) root.getChildren().get(1);
        } else {
            // First time opening a popup: Create and fade in the overlay.
            darkOverlay = new Region();
            darkOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            darkOverlay.setOpacity(0);
            FadeTransition fadeOverlay = new FadeTransition(Duration.millis(200), darkOverlay);
            fadeOverlay.setToValue(1.0);
            fadeOverlay.play();
            root.getChildren().add(darkOverlay);
        }

        // 2. Load the 12-frame wooden animation image
        java.io.InputStream stream = getClass().getResourceAsStream("/_assets/panels/ui_popup_scroll.png");
        
        // SAFETY CHECK: If the image is missing, fall back to the Fade/Scale popup.
        if (stream == null) {
            System.err.println("WARNING: ui_popup_scroll.png not found! Falling back to popup.");
            StackPane fallbackContainer = new StackPane(newNode);
            fallbackContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            
            newNode.setOpacity(0);
            newNode.setScaleX(0.9);
            newNode.setScaleY(0.9);
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), newNode);
            fadeIn.setToValue(1.0);
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(250), newNode);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);
            
            new ParallelTransition(fadeIn, scaleIn).play();
            root.getChildren().add(fallbackContainer);
            return;
        }

        // 3. Setup the wooden background image
        Image woodImg = new Image(stream);
        ImageView woodBackground = new ImageView(woodImg);
        Region viewRegion = (Region) newNode;
        
        // Stretch the wood to fit the size of the popup view.
        woodBackground.setPreserveRatio(false);
        woodBackground.fitWidthProperty().bind(viewRegion.widthProperty());
        woodBackground.fitHeightProperty().bind(viewRegion.heightProperty());

        // Wrap the wood and the content together.
        StackPane dropdownContainer = new StackPane(woodBackground, newNode);
        dropdownContainer.setPadding(new Insets(30));
       // dropdownContainer.setStyle("-fx-border-color: #c62828;");
        dropdownContainer.setAlignment(Pos.CENTER); 
        dropdownContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        root.getChildren().add(dropdownContainer);
        
        // Force JavaFX to calculate the layout so we know the exact height.
        root.applyCss();
        root.layout();

        double targetHeight = dropdownContainer.getHeight();
        if (targetHeight <= 0) targetHeight = 300; // Safety fallback.

        // 4. Create the mask (Curtain) that reveals the popup downwards.
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(dropdownContainer.widthProperty());
        clip.setHeight(0); 
        dropdownContainer.setClip(clip);

        // 5. Animate the 12-frame wood unrolling.
        double frameWidth = woodImg.getWidth() / 12.0;
        double frameHeight = woodImg.getHeight();
        
        Timeline woodTimeline = new Timeline();
        for (int i = 0; i < 12; i++) {
            final int frame = i;
            woodTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * 40), e -> {
                woodBackground.setViewport(new Rectangle2D(frame * frameWidth, 0, frameWidth, frameHeight));
            }));
        }

        // 6. Animate the mask dropping down to reveal the view.
        Timeline clipTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(clip.heightProperty(), 0, Interpolator.EASE_IN)),
            new KeyFrame(Duration.millis(480), new KeyValue(clip.heightProperty(), targetHeight, Interpolator.EASE_OUT))
        );

        woodTimeline.play();
        clipTimeline.play();
    }

    // =========================================================
    // 3. CLOSE POPUP — Uses ROLL-UP Animation
    // =========================================================
    public void closePopup() {
        closePopup(null);
    }

    /**
     * Rolls up the current popup and fades out the dark background.
     */
    public void closePopup(Runnable onFinished) {
        if (root.getChildren().size() <= 1) {
            if (onFinished != null) onFinished.run();
            return;
        }

        Node topNode = root.getChildren().get(root.getChildren().size() - 1);
        
        // If it was a fallback popup (no wooden animation), just fade it out.
        if (!(topNode instanceof StackPane) || ((StackPane) topNode).getClip() == null) {
            Node overlayNode = root.getChildren().size() > 2 ? root.getChildren().get(1) : null;
            
            FadeTransition fadeOutNode = new FadeTransition(Duration.millis(200), topNode);
            fadeOutNode.setToValue(0.0);
            
            Transition closeTransition;
            
            if (overlayNode != null) {
                FadeTransition fadeOverlay = new FadeTransition(Duration.millis(200), overlayNode);
                fadeOverlay.setToValue(0.0);
                closeTransition = new ParallelTransition(fadeOutNode, fadeOverlay);
            } else {
                closeTransition = fadeOutNode;
            }
            
            closeTransition.setOnFinished(e -> {
                root.getChildren().remove(1, root.getChildren().size());
                if (onFinished != null) onFinished.run();
            });
            closeTransition.play();
            return;
        }

        // --- WOODEN ROLL-UP ANIMATION ---
        StackPane dropdownContainer = (StackPane) topNode;
        Rectangle clip = (Rectangle) dropdownContainer.getClip();
        Node overlayNode = root.getChildren().size() > 2 ? root.getChildren().get(1) : null;

        // Shrink the mask back to 0 height.
        Timeline clipTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(clip.heightProperty(), clip.getHeight())),
            new KeyFrame(Duration.millis(250), new KeyValue(clip.heightProperty(), 0, Interpolator.EASE_IN))
        );

        // Play the wood animation in reverse.
        Timeline woodTimeline = null;
        Node graphicNode = dropdownContainer.getChildren().get(0);
        
        if (graphicNode instanceof ImageView) {
            ImageView woodBackground = (ImageView) graphicNode;
            Image woodImg = woodBackground.getImage();
            double frameWidth = woodImg.getWidth() / 12.0;
            double frameHeight = woodImg.getHeight();

            woodTimeline = new Timeline();
            for (int i = 0; i < 12; i++) {
                final int frame = 11 - i; 
                woodTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * 20), e -> {
                    woodBackground.setViewport(new Rectangle2D(frame * frameWidth, 0, frameWidth, frameHeight));
                }));
            }
        }

        // Combine the mask animation and wood animation.
        ParallelTransition transition = new ParallelTransition(clipTimeline);
        if (woodTimeline != null) transition.getChildren().add(woodTimeline);
        
        // Fade out the dark background.
        if (overlayNode != null) {
            FadeTransition fadeOverlay = new FadeTransition(Duration.millis(250), overlayNode);
            fadeOverlay.setToValue(0.0);
            transition.getChildren().add(fadeOverlay);
        }

        transition.setOnFinished(e -> {
            // Remove the popup and overlay from the screen.
            root.getChildren().remove(1, root.getChildren().size());
            if (onFinished != null) onFinished.run();
        });

        transition.play();
    }

    /**
     * SHOW MENU — Returns to the main menu and clears the screen.
     */
    public void showMenu() {
        // Reusing setContent automatically clears the screen and plays the transition
        setContent(menuContent);
    }

    // --- GETTERS for the Controller ---
    public Button getLoginButton()    { return loginButton; }
    public Button getRegisterButton() { return registerButton; }
    public Button getExitButton()     { return exitButton; }
    public Stage  getStage()          { return stage; }
    public void   show()              { stage.show(); }
}