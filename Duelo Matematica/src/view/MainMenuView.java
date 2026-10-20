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

    private final Button loginButton;
    private final Button registerButton;

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

        loginButton    = createMenuButton("Login");
        registerButton = createMenuButton("Register");

        VBox buttonBox = new VBox(15, loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setFillWidth(true);

        menuContent = new VBox(30, headerLabel, buttonBox);
        menuContent.setAlignment(Pos.CENTER);
        menuContent.setFillWidth(false);
        menuContent.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

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

    public void setContent(Node node) {
        playPopupTransition(node);
    }

    public void showMenu() {
        playPopupTransition(menuContent);
    }

    private void playPopupTransition(Node newNode) {
        if (activeTransition != null) {
            activeTransition.stop();
            activeTransition = null;
            if (root.getChildren().size() > 1) {
                root.getChildren().remove(1, root.getChildren().size());
            }
            if (!root.getChildren().isEmpty()) {
                Node current = root.getChildren().get(0);
                current.setOpacity(1);
                current.setScaleX(1);
                current.setScaleY(1);
            }
        }

        if (!root.getChildren().isEmpty() && root.getChildren().get(0) == newNode) {
            return;
        }

        Node oldNode = root.getChildren().isEmpty() ? null : root.getChildren().get(0);
        root.getChildren().add(newNode);

        newNode.setOpacity(0);
        newNode.setScaleX(0.9);
        newNode.setScaleY(0.9);

        FadeTransition fadeIn = new FadeTransition(ANIM_DURATION, newNode);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleIn = new ScaleTransition(ANIM_DURATION, newNode);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        ParallelTransition transition;
        
        if (oldNode != null) {
            FadeTransition fadeOut = new FadeTransition(ANIM_DURATION, oldNode);
            fadeOut.setToValue(0.0);
            transition = new ParallelTransition(fadeIn, scaleIn, fadeOut);
        } else {
            transition = new ParallelTransition(fadeIn, scaleIn);
        }

        activeTransition = transition;
        
        transition.setOnFinished(e -> {
            if (oldNode != null) {
                root.getChildren().remove(oldNode);
                oldNode.setOpacity(1); 
                oldNode.setScaleX(1);
                oldNode.setScaleY(1);
            }
            activeTransition = null;
        });

        transition.play();
    }

    public Button getLoginButton()    { return loginButton; }
    public Button getRegisterButton() { return registerButton; }
    public Stage  getStage()          { return stage; }
    public void   show()              { stage.show(); }
}