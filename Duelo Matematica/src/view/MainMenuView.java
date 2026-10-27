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
    // 2. POPUP OVERLAY — Delegated to ViewUtils
    // =========================================================
    public void setOverlayContent(Node newNode) {
        if (activeTransition != null) activeTransition.stop();
        ViewUtils.showOverlayContent(root, newNode);
    }

    // =========================================================
    // 3. CLOSE POPUP — Delegated to ViewUtils
    // =========================================================
    public void closePopup() {
        ViewUtils.closePopup(root, null);
    }
    
    public void closePopup(Runnable onFinished) {
        ViewUtils.closePopup(root, onFinished);
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