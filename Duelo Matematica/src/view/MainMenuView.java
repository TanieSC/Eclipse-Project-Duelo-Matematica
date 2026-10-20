package view;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * MAIN MENU VIEW — The "Window Shell" and Animation Director.
 * 
 * WHAT DOES THIS CLASS DO?
 * This is the ONLY class in the app that owns the actual physical OS window 
 * (the Stage). It acts like a picture frame. 
 * 
 * Inside the frame, it uses a special container called a "StackPane" (which 
 * works like a stack of papers). It places the Home Menu card on the stack. 
 * When the user navigates to a different screen (like Login or the Game), 
 * this class puts the new screen on TOP of the old one, plays a cool 
 * "popup" animation, and then throws away the old screen underneath.
 */
public class MainMenuView {

    // How long the animation takes (250 milliseconds). 
    // It's fast enough to feel snappy, but slow enough to see the fade.
    private static final Duration ANIM_DURATION = Duration.millis(250);

    // The actual OS window provided by the App class.
    private final Stage stage;
    
    // The "Stack of papers" container. This holds whatever screen is currently showing.
    private final StackPane root;
    
    // The default home screen layout (Title + Login/Register/Exit buttons).
    private final VBox menuContent;

    // The 3 buttons on the home screen.
    private final Button loginButton;
    private final Button registerButton;
    private final Button exitButton;
    
    // We keep track of the current animation. If a user clicks really fast, 
    // we can stop the old animation and start the new one smoothly.
    private Transition activeTransition;

    /**
     * CONSTRUCTOR — Builds the main window and sets up the home menu.
     * 
     * @param stage The empty window provided by the App class.
     */
    public MainMenuView(Stage stage) {
        this.stage = stage;

        // ==========================================
        // 1. BUILDING THE HOME MENU VISUALS
        // ==========================================
        
        // Create the big title banner at the top.
        Label headerLabel = new Label("Duelo\nX\nMatematica");
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setStyle(
            "-fx-font-size: 26px; -fx-font-weight: bold;" +
            "-fx-border-color: #222222; -fx-border-width: 3px;" +
            "-fx-padding: 12px 60px; -fx-background-color: #f4f4f4;" +
            "-fx-alignment: center; -fx-text-alignment: center;"
        );

        // Create the three main navigation buttons using our helper method.
        loginButton    = createMenuButton("LOGIN");
        registerButton = createMenuButton("CREATE ACCOUNT");
        exitButton     = createMenuButton("EXIT");

        // Stack the buttons vertically in a VBox (Vertical Box).
        VBox buttonBox = new VBox(15, loginButton, registerButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setFillWidth(true);

        // Stack the title and the buttons vertically to make the complete "Card".
        menuContent = new VBox(30, headerLabel, buttonBox);
        menuContent.setAlignment(Pos.CENTER);
        menuContent.setFillWidth(false);
        
        // This keeps the menu card at a nice, compact size in the center of the window.
        menuContent.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // ==========================================
        // 2. SETTING UP THE WINDOW
        // ==========================================
        
        // Put the menu card inside our stack of papers.
        root = new StackPane(menuContent);
        root.setStyle("-fx-background-color: #ffffff;");

        // Set the window title and size (800 pixels wide, 500 pixels tall).
        stage.setTitle("Duelo X Matematica");
        stage.setScene(new Scene(root, 800, 500)); 
    }

    /**
     * HELPER — Creates a big, uniform button for the home menu.
     * It uses "setMaxWidth(Double.MAX_VALUE)" so all buttons stretch to 
     * match the widest button perfectly.
     */
    private Button createMenuButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE); // Stretch to fill horizontal space
        b.setMinHeight(55);              // Make it tall
        b.setMaxHeight(55);
        b.setStyle(
            "-fx-font-size: 18px; -fx-font-weight: bold;" +
            "-fx-border-color: #000000; -fx-border-width: 3px;" +
            "-fx-padding: 10px 30px; -fx-background-color: #f9f9f9;" +
            "-fx-cursor: hand;"
        );
        return b;
    }

    // ==========================================
    // NAVIGATION & ANIMATION
    // ==========================================

    /**
     * SET CONTENT — Swaps the current screen for a new one.
     * For example, when the user clicks "Login", the MainMenuController 
     * builds the Login screen and passes it here.
     */
    public void setContent(Node node) {
        playPopupTransition(node);
    }

    /**
     * SHOW MENU — Goes back to the home screen.
     */
    public void showMenu() {
        playPopupTransition(menuContent);
    }

    /**
     * THE MAGIC TRICK — The Popup Animation.
     * 
     * HOW IT WORKS IN PLAIN ENGLISH:
     * 1. Place the new screen on top of the old screen.
     * 2. Make the new screen completely invisible (Opacity 0) and tiny (Scale 0.9).
     * 3. Fade the old screen out (to invisible).
     * 4. Fade the new screen in and grow it to full size.
     * 5. Once the animation is done, delete the old screen to save memory.
     */
    private void playPopupTransition(Node newNode) {
        
        // 1. CLEANUP: If an animation is already running (user clicked too fast), 
        // stop it and clean up the half-finished mess before starting a new one.
        if (activeTransition != null) {
            activeTransition.stop();
            activeTransition = null;
            
            // Remove any extra screens that got stuck in the stack.
            if (root.getChildren().size() > 1) {
                root.getChildren().remove(1, root.getChildren().size());
            }
            
            // Reset the current screen back to full visibility and normal size.
            if (!root.getChildren().isEmpty()) {
                Node current = root.getChildren().get(0);
                current.setOpacity(1);
                current.setScaleX(1);
                current.setScaleY(1);
            }
        }

        // If the screen we are trying to show is ALREADY the one showing, do nothing.
        if (!root.getChildren().isEmpty() && root.getChildren().get(0) == newNode) {
            return;
        }

        // 2. Grab the old screen (if there is one) and place the new screen on top.
        Node oldNode = root.getChildren().isEmpty() ? null : root.getChildren().get(0);
        root.getChildren().add(newNode);

        // 3. Prepare the new screen: Make it invisible and slightly shrunken.
        newNode.setOpacity(0);
        newNode.setScaleX(0.9);
        newNode.setScaleY(0.9);

        // 4. Create the animations.
        // Fade the new screen IN (from 0 to 1).
        FadeTransition fadeIn = new FadeTransition(ANIM_DURATION, newNode);
        fadeIn.setToValue(1.0);

        // Grow the new screen back to full size (from 0.9 to 1.0).
        ScaleTransition scaleIn = new ScaleTransition(ANIM_DURATION, newNode);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        ParallelTransition transition;
        
        if (oldNode != null) {
            // Fade the OLD screen OUT (from 1 to 0).
            FadeTransition fadeOut = new FadeTransition(ANIM_DURATION, oldNode);
            fadeOut.setToValue(0.0);
            
            // Run all three animations at the exact same time.
            transition = new ParallelTransition(fadeIn, scaleIn, fadeOut);
        } else {
            // If there was no old screen (first time loading), just fade the new one in.
            transition = new ParallelTransition(fadeIn, scaleIn);
        }

        activeTransition = transition;
        
        // 5. WHEN THE ANIMATION FINISHES: Clean up the old screen.
        transition.setOnFinished(e -> {
            if (oldNode != null) {
                root.getChildren().remove(oldNode); // Delete the old screen from memory
                oldNode.setOpacity(1);              // Reset its properties in case we use it again
                oldNode.setScaleX(1);
                oldNode.setScaleY(1);
            }
            activeTransition = null;
        });

        // Start the show!
        transition.play();
    }

    // ==========================================
    // GETTERS
    // ==========================================

    /** Lets the controller attach a click action to the Login button. */
    public Button getLoginButton()    { return loginButton; }
    
    /** Lets the controller attach a click action to the Register button. */
    public Button getRegisterButton() { return registerButton; }

    /** Lets the controller attach a click action to the Exit button. */
    public Button getExitButton()     { return exitButton; }
    
    /** Returns the main window. */
    public Stage  getStage()          { return stage; }
    
    /** Makes the window visible on the screen. */
    public void   show()              { stage.show(); }
}