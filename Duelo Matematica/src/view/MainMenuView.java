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

public class MainMenuView {

    private static final Duration ANIM_DURATION = Duration.millis(250);

    private final Stage stage;
    private final StackPane root;
    private final VBox menuContent;
    private final Region overlay;
    
    private final Button loginButton;
    private final Button registerButton;
    private final Button exitButton;
    
    private Transition activeTransition;

    public MainMenuView(Stage stage) {
        this.stage = stage;

        Label headerLabel = ViewUtils.createMainTitleLabel("Duelo\nX\nMatematica");  //new Label("Duelo\nX\nMatematica");
        

        loginButton = ViewUtils.createSpriteButton("/buttons/btn.png", "LOGIN", null);
        
        registerButton = ViewUtils.createSpriteButton("/buttons/btn.png", "CREATE ACCOUNT", null);
        
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
        stage.setScene(new Scene(root, 800, 500)); 
    }

    private Button createMenuButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setMinHeight(55);
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
    // FULL SCREEN SWAP (With Popup Animation)
    // ==========================================
    // --- FULL SCREEN & OVERLAY SWAPS ---

    /**
     * FULL SCREEN SWAP — Hides the previous screen completely and shows the new one
     * with the same popup effect (fade + scale) as the overlay views.
     * 
     * This method handles TWO possible starting states:
     *   1. Coming from the menu (no overlay active)
     *   2. Coming from an overlay like Login (Login is on top of the menu)
     */
    public void setContent(Node newNode) {
        if (activeTransition != null) { 
            activeTransition.stop(); 
            activeTransition = null; 
        }

        // If this node is already the only one showing, skip
        if (root.getChildren().size() == 1 && root.getChildren().get(0) == newNode) {
            return;
        }

        // Prepare the new node for the popup animation
        newNode.setOpacity(0);
        newNode.setScaleX(0.9);
        newNode.setScaleY(0.9);

        // Add the new node ON TOP of whatever is currently showing
        // (e.g., on top of the Login card + overlay + menu)
        root.getChildren().add(newNode);

        // Fade + scale in the new node
        FadeTransition fadeIn = new FadeTransition(ANIM_DURATION, newNode);
        fadeIn.setToValue(1.0);
        ScaleTransition scaleIn = new ScaleTransition(ANIM_DURATION, newNode);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        // If there's an active overlay (like Login), fade it out simultaneously
        ParallelTransition transition;
        if (overlay.isVisible()) {
            FadeTransition fadeOutOverlay = new FadeTransition(ANIM_DURATION, overlay);
            fadeOutOverlay.setToValue(0.0);
            transition = new ParallelTransition(fadeIn, scaleIn, fadeOutOverlay);
        } else {
            transition = new ParallelTransition(fadeIn, scaleIn);
        }

        activeTransition = transition;
        
        // When done, clean up — only the new node should remain
        activeTransition.setOnFinished(e -> {
            root.getChildren().setAll(newNode);
            overlay.setVisible(false);
            
            // Reset properties in case this node is shown again later
            newNode.setOpacity(1);
            newNode.setScaleX(1);
            newNode.setScaleY(1);
            
            activeTransition = null;
        });
        
        activeTransition.play();
    }

    public void setOverlayContent(Node newNode) {
        if (activeTransition != null) { activeTransition.stop(); activeTransition = null; }

        if (root.getChildren().size() >= 3) {
            Node oldNode = root.getChildren().get(root.getChildren().size() - 1);
            if (oldNode == newNode) return;
            root.getChildren().add(newNode);

            newNode.setOpacity(0); newNode.setScaleX(0.9); newNode.setScaleY(0.9);
            FadeTransition fadeIn = new FadeTransition(ANIM_DURATION, newNode);
            fadeIn.setToValue(1.0);
            ScaleTransition scaleIn = new ScaleTransition(ANIM_DURATION, newNode);
            scaleIn.setToX(1.0); scaleIn.setToY(1.0);

            FadeTransition fadeOut = new FadeTransition(ANIM_DURATION, oldNode);
            fadeOut.setToValue(0.0);
            ScaleTransition scaleOut = new ScaleTransition(ANIM_DURATION, oldNode);
            scaleOut.setToX(0.9); scaleOut.setToY(0.9);

            activeTransition = new ParallelTransition(fadeIn, scaleIn, fadeOut, scaleOut);
            activeTransition.setOnFinished(e -> { root.getChildren().remove(oldNode); activeTransition = null; });
            activeTransition.play();
            return;
        }

        overlay.setVisible(true);
        overlay.setOpacity(0);
        newNode.setOpacity(0); newNode.setScaleX(0.9); newNode.setScaleY(0.9);
        root.getChildren().setAll(menuContent, overlay, newNode);

        FadeTransition fadeInNode = new FadeTransition(ANIM_DURATION, newNode);
        fadeInNode.setToValue(1.0);
        ScaleTransition scaleInNode = new ScaleTransition(ANIM_DURATION, newNode);
        scaleInNode.setToX(1.0); scaleInNode.setToY(1.0);
        FadeTransition fadeInOverlay = new FadeTransition(ANIM_DURATION, overlay);
        fadeInOverlay.setToValue(1.0);

        activeTransition = new ParallelTransition(fadeInNode, scaleInNode, fadeInOverlay);
        activeTransition.play();
    }

    /**
     * NEW: SHOW POPUP — Puts a dark overlay and a popup card ON TOP of the current screen.
     * Perfect for Game Settings on top of GameMenus.
     */
    public void showPopup(Node popupNode) {
        if (activeTransition != null) { activeTransition.stop(); activeTransition = null; }

        overlay.setVisible(true);
        overlay.setOpacity(0);
        popupNode.setOpacity(0); 
        popupNode.setScaleX(0.9); 
        popupNode.setScaleY(0.9);

        // Add overlay and popup ON TOP of whatever is currently showing
        root.getChildren().addAll(overlay, popupNode);

        FadeTransition fadeInNode = new FadeTransition(ANIM_DURATION, popupNode);
        fadeInNode.setToValue(1.0);
        ScaleTransition scaleInNode = new ScaleTransition(ANIM_DURATION, popupNode);
        scaleInNode.setToX(1.0); scaleInNode.setToY(1.0);
        FadeTransition fadeInOverlay = new FadeTransition(ANIM_DURATION, overlay);
        fadeInOverlay.setToValue(1.0);

        activeTransition = new ParallelTransition(fadeInNode, scaleInNode, fadeInOverlay);
        activeTransition.play();
    }

    /**
     * NEW: CLOSE POPUP — Removes the popup and overlay, revealing the screen underneath.
     */
    public void closePopup() {
        if (activeTransition != null) { activeTransition.stop(); activeTransition = null; }
        if (root.getChildren().size() <= 1) return;

        Node topNode = root.getChildren().get(root.getChildren().size() - 1);
        if (topNode == overlay) return; // Nothing to close

        FadeTransition fadeOutNode = new FadeTransition(ANIM_DURATION, topNode);
        fadeOutNode.setToValue(0.0);
        ScaleTransition scaleOutNode = new ScaleTransition(ANIM_DURATION, topNode);
        scaleOutNode.setToX(0.9); scaleOutNode.setToY(0.9);
        FadeTransition fadeOutOverlay = new FadeTransition(ANIM_DURATION, overlay);
        fadeOutOverlay.setToValue(0.0);

        activeTransition = new ParallelTransition(fadeOutNode, scaleOutNode, fadeOutOverlay);
        activeTransition.setOnFinished(e -> {
            // Remove ONLY the popup and overlay
            root.getChildren().removeAll(overlay, topNode);
            overlay.setVisible(false);
            activeTransition = null;
        });
        activeTransition.play();
    }


    // ==========================================
    // SHOW MENU (Returns to base with Animation)
    // ==========================================
    public void showMenu() {
        if (activeTransition != null) {
            activeTransition.stop();
            activeTransition = null;
        }

        if (root.getChildren().size() == 1 && root.getChildren().get(0) == menuContent) {
            return;
        }

        Node topNode = root.getChildren().get(root.getChildren().size() - 1);
        
        FadeTransition fadeOutNode = new FadeTransition(ANIM_DURATION, topNode);
        fadeOutNode.setToValue(0.0);
        ScaleTransition scaleOutNode = new ScaleTransition(ANIM_DURATION, topNode);
        scaleOutNode.setToX(0.9);
        scaleOutNode.setToY(0.9);

        FadeTransition fadeOutOverlay = new FadeTransition(ANIM_DURATION, overlay);
        fadeOutOverlay.setToValue(0.0);

        activeTransition = new ParallelTransition(fadeOutNode, scaleOutNode, fadeOutOverlay);
        
        activeTransition.setOnFinished(e -> {
            root.getChildren().setAll(menuContent);
            overlay.setVisible(false);
            activeTransition = null;
        });
        
        activeTransition.play();
    }

    // ==========================================
    // GETTERS
    // ==========================================
    public Button getLoginButton()    {return loginButton; }
    public Button getRegisterButton() { return registerButton; }
    public Button getExitButton()     { return exitButton; }
    public Stage  getStage()          { return stage; }
    public void   show()              { stage.show(); }
}