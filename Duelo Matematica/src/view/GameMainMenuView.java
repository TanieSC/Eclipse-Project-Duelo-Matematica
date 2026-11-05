package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameMainMenuView {

    private final StackPane root;
    private final Button btnStart;
    private final Button btnCredits;
    private final Button btnBack;
    private final Button gearButton;

    public GameMainMenuView(String titleText) {
        // 1. Custom Wooden Header
        Label headerLabel = ViewUtils.createPopupHeaderLabel(titleText);
        ViewUtils.applyDropShadow(headerLabel);

        // 2. Custom Wooden Buttons
        btnStart   = ViewUtils.createSpriteButton("/buttons/btn.png", "START", null);
        btnCredits = ViewUtils.createSpriteButton("/buttons/btn.png", "CREDITS", null);
        btnBack    = ViewUtils.createSpriteButton("/buttons/btn.png", "LOGOUT", null);
        
        ViewUtils.applyDropShadow(btnStart);
        ViewUtils.applyDropShadow(btnCredits);
        ViewUtils.applyDropShadow(btnBack);

        VBox buttonBox = new VBox(15, btnStart, btnCredits, btnBack);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setFillWidth(true);

        VBox centerContent = new VBox(30, headerLabel, buttonBox);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setFillWidth(false);

        gearButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_settings.png", 50, null);
        ViewUtils.applyDropShadow(gearButton);

        root = new StackPane();
        // CRUCIAL: Transparent background so the wooden popup shows!
        root.setStyle("-fx-background-color: transparent;"); 
        //ViewUtils.applyBackgroundImage(root, "/backgrounds/bg_main_menu.png");
        root.getChildren().addAll(centerContent, gearButton);
        
        StackPane.setAlignment(gearButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(gearButton, new Insets(20));
    }

    public Node getRoot()             { return root; }
    public Button getStartButton()    { return btnStart; }
    public Button getCreditsButton()  { return btnCredits; }
    public Button getBackButton()     { return btnBack; }
    public Button getGearButton()     { return gearButton; }
}