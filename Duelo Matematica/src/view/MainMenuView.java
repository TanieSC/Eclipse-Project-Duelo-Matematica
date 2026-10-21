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

        Label headerLabel = new Label("Duelo\nX\nMatematica");
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setStyle(
            "-fx-font-size: 26px; -fx-font-weight: bold;" +
            "-fx-border-color: #222222; -fx-border-width: 3px;" +
            "-fx-padding: 12px 60px; -fx-background-color: #f4f4f4;" +
            "-fx-alignment: center; -fx-text-alignment: center;"
        );

        loginButton    = createMenuButton("LOGIN");
        registerButton = createMenuButton("CREATE ACCOUNT");
        exitButton     = createMenuButton("EXIT");

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
    public void setContent(Node newNode) {
        if (activeTransition != null) {
            activeTransition.stop();
            activeTransition = null;
        }

        // If the screen is already showing, do nothing
        if (!root.getChildren().isEmpty() && root.getChildren().get(root.getChildren().size() - 1) == newNode) {
            return;
        }

        Node oldNode = root.getChildren().isEmpty() ? null : root.getChildren().get(root.getChildren().size() - 1);

        // Put new node on top
        root.getChildren().add(newNode);
        overlay.setVisible(false);

        // Prepare new node for popup (transparent and slightly smaller)
        newNode.setOpacity(0);
        newNode.setScaleX(0.9);
        newNode.setScaleY(0.9);

        FadeTransition fadeIn = new FadeTransition(ANIM_DURATION, newNode);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleIn = new ScaleTransition(ANIM_DURATION, newNode);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        ParallelTransition transition;
        
        // If we have an old screen to hide (but not the menu, we keep the menu hidden)
        if (oldNode != null && oldNode != menuContent) {
            FadeTransition fadeOut = new FadeTransition(ANIM_DURATION, oldNode);
            fadeOut.setToValue(0.0);
            ScaleTransition scaleOut = new ScaleTransition(ANIM_DURATION, oldNode);
            scaleOut.setToX(0.9);
            scaleOut.setToY(0.9);
            transition = new ParallelTransition(fadeIn, scaleIn, fadeOut, scaleOut);
        } else {
            transition = new ParallelTransition(fadeIn, scaleIn);
        }

        activeTransition = transition;
        activeTransition.setOnFinished(e -> {
            if (oldNode != null && oldNode != menuContent) {
                root.getChildren().remove(oldNode);
                // Reset old node properties for future use
                oldNode.setOpacity(1);
                oldNode.setScaleX(1);
                oldNode.setScaleY(1);
            }
            activeTransition = null;
        });
        activeTransition.play();
    }

    // ==========================================
    // OVERLAY SWAP (With Popup Animation & No Flicker)
    // ==========================================
    public void setOverlayContent(Node newNode) {
        if (activeTransition != null) {
            activeTransition.stop();
            activeTransition = null;
        }

        // SCENARIO 1: Already inside an overlay (e.g., Login -> Forgot Password)
        if (root.getChildren().size() >= 3) {
            Node oldNode = root.getChildren().get(root.getChildren().size() - 1);
            
            if (oldNode == newNode) return;

            root.getChildren().add(newNode);

            newNode.setOpacity(0);
            newNode.setScaleX(0.9);
            newNode.setScaleY(0.9);

            FadeTransition fadeIn = new FadeTransition(ANIM_DURATION, newNode);
            fadeIn.setToValue(1.0);
            ScaleTransition scaleIn = new ScaleTransition(ANIM_DURATION, newNode);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);

            FadeTransition fadeOut = new FadeTransition(ANIM_DURATION, oldNode);
            fadeOut.setToValue(0.0);
            ScaleTransition scaleOut = new ScaleTransition(ANIM_DURATION, oldNode);
            scaleOut.setToX(0.9);
            scaleOut.setToY(0.9);

            activeTransition = new ParallelTransition(fadeIn, scaleIn, fadeOut, scaleOut);
            activeTransition.setOnFinished(e -> {
                root.getChildren().remove(oldNode);
                oldNode.setOpacity(1);
                oldNode.setScaleX(1);
                oldNode.setScaleY(1);
                activeTransition = null;
            });
            activeTransition.play();
            return;
        }

        // SCENARIO 2: Starting fresh from the menu (Menu -> Login)
        overlay.setVisible(true);
        overlay.setOpacity(0);
        
        newNode.setOpacity(0);
        newNode.setScaleX(0.9);
        newNode.setScaleY(0.9);

        root.getChildren().setAll(menuContent, overlay, newNode);

        FadeTransition fadeInNode = new FadeTransition(ANIM_DURATION, newNode);
        fadeInNode.setToValue(1.0);
        ScaleTransition scaleInNode = new ScaleTransition(ANIM_DURATION, newNode);
        scaleInNode.setToX(1.0);
        scaleInNode.setToY(1.0);

        FadeTransition fadeInOverlay = new FadeTransition(ANIM_DURATION, overlay);
        fadeInOverlay.setToValue(1.0);

        activeTransition = new ParallelTransition(fadeInNode, scaleInNode, fadeInOverlay);
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
    public Button getLoginButton()    { return loginButton; }
    public Button getRegisterButton() { return registerButton; }
    public Button getExitButton()     { return exitButton; }
    public Stage  getStage()          { return stage; }
    public void   show()              { stage.show(); }
}