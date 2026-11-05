package view;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * MAIN MENU VIEW — The "Window Shell" and Animation Director.
 */
public class MainMenuView {

    private final Stage stage;          
    private final StackPane root;       
    private final VBox menuContent;      
    
    private final Button loginButton;
    private final Button registerButton;
    private final Button exitButton;
    
    private Transition activeTransition;

    public MainMenuView(Stage stage) {
        this.stage = stage;

        // ==========================================
        // 1. THE CUSTOM HEADER (DUELO + Swords + MATEMATICA)
        // ==========================================
        Label lblDuelo = new Label("DUELO");
        lblDuelo.setFont(ViewUtils.getCustomFont(40));
        lblDuelo.setTextFill(Color.web("#55321E")); // Very dark, almost black
        lblDuelo.setTranslateY(10); // Push down slightly toward the swords

        ImageView swordsIcon = new ImageView();
        try {
            // Load the image you provided
            var stream = getClass().getResourceAsStream("/_assets/misc/ui_crossed_swords.png");
            if (stream != null) {
                swordsIcon.setImage(new Image(stream));
                swordsIcon.setFitWidth(95); // Adjust this if the swords need to be bigger/smaller
                swordsIcon.setPreserveRatio(true);
            }
        } catch (Exception e) {
            System.err.println("Could not load swords icon.");
        }

        Label lblMatematica = new Label("MATEMATICA");
        lblMatematica.setFont(ViewUtils.getCustomFont(40));
        lblMatematica.setTextFill(Color.web("#55321E"));
        lblMatematica.setTranslateY(-50); // Pull up slightly toward the swords

        // Stack them vertically. Negative spacing (-25) pulls them tightly together
        VBox titleBox = new VBox(-20, lblDuelo, swordsIcon, lblMatematica);
        titleBox.setAlignment(Pos.CENTER);

        // Wrap the text in the wooden scroll background
        NineSlice scrollBg = ViewUtils.createNineSlice("/panels/ui_scroll.png", 1, 20, 20, 8, 14, 3.0, true);
        StackPane headerContainer = new StackPane(scrollBg, titleBox);
        
        // Make the scroll large and wide enough to match your screenshot's proportions
        headerContainer.setPrefSize(700, 280); 
        headerContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // Bind the background to perfectly fill the container
        if (scrollBg != null) {
            scrollBg.prefWidthProperty().bind(headerContainer.widthProperty());
            scrollBg.prefHeightProperty().bind(headerContainer.heightProperty());
            scrollBg.minWidthProperty().bind(headerContainer.widthProperty());
            scrollBg.minHeightProperty().bind(headerContainer.heightProperty());
            scrollBg.maxWidthProperty().bind(headerContainer.widthProperty());
            scrollBg.maxHeightProperty().bind(headerContainer.heightProperty());
        }

        ViewUtils.applyDropShadow(headerContainer);

        // ==========================================
        // 2. THE BUTTONS
        // ==========================================
        loginButton = ViewUtils.createSpriteButton("/buttons/btn.png", "LOGIN", null);
        
        registerButton = ViewUtils.createSpriteButton("/buttons/btn.png", 300, 60, null);
        registerButton.setText("CREATE ACCOUNT");
        
        exitButton = ViewUtils.createSpriteButton("/buttons/btn.png","EXIT", null);
        
        ViewUtils.applyDropShadow(loginButton);
        ViewUtils.applyDropShadow(registerButton);
        ViewUtils.applyDropShadow(exitButton);

        VBox buttonBox = new VBox(15, loginButton, registerButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setFillWidth(true);

        // ==========================================
        // 3. ASSEMBLE THE MENU
        // ==========================================
        menuContent = new VBox(40, headerContainer, buttonBox); // 40px gap between header and buttons
        menuContent.setAlignment(Pos.CENTER);
        menuContent.setFillWidth(false);
        menuContent.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // ==========================================
        // 4. ROOT & BACKGROUND
        // ==========================================
        root = new StackPane(menuContent);
        ViewUtils.applyBackgroundImage(root, "/backgrounds/bg_main_menu.png");

        stage.setTitle("Duelo X Matematica");
        //stage.setScene(new Scene(root, 900, 700)); 
        // Create the scene
        Scene scene = new Scene(root, 900, 700); 
        
        // APPLY CUSTOM CURSOR HERE
        try {
            var cursorStream = getClass().getResourceAsStream("/_assets/misc/ui_cursor.png");
            if (cursorStream != null) {
                Image cursorImage = new Image(cursorStream);
                
                // The (0, 0) are the "hotspot" coordinates. 
                // For a top-left pointing arrow, the hotspot is at the very tip (0,0).
                Cursor customCursor = new ImageCursor(cursorImage, 0, 0);
                scene.setCursor(customCursor);
            } else {
                System.err.println("WARNING: Custom cursor image NOT FOUND at /_assets/misc/cursor.png");
            }
        } catch (Exception e) {
            System.err.println("Failed to load custom cursor: " + e.getMessage());
        }

        stage.setScene(scene);
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

        // NEW: swap the root background so it only lives on the menu screen.
        if (newNode == menuContent) {
            ViewUtils.applyBackgroundImage(root, "/backgrounds/bg_main_menu.png");
        } else {
            //root.setBackground(null);
        	ViewUtils.applyBackgroundImage(root, "");
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