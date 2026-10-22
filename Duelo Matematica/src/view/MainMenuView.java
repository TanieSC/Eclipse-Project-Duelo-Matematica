package view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
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

    /**
     * FULL SCREEN SWAP (e.g., going to Game Menu)
     */
    public void setContent(Node newNode) {
        overlay.setVisible(false);
        // Just the menu in the background
        ViewUtils.animateDropdown(newNode, root, menuContent); 
    }

    /**
     * OVERLAY SWAP (Popups like Settings/Profile)
     */
    public void setOverlayContent(Node newNode) {
        overlay.setVisible(true);
        overlay.setOpacity(0);
        
        // Fade in the dark overlay simultaneously
        FadeTransition fadeInOverlay = new FadeTransition(Duration.millis(250), overlay);
        fadeInOverlay.setToValue(1.0);
        fadeInOverlay.play();

        // Keep the menu AND the overlay in the background
        ViewUtils.animateDropdown(newNode, root, menuContent, overlay);
    }

    public void showMenu() {
        overlay.setVisible(false);
        root.getChildren().setAll(menuContent);
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