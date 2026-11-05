package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameStartMenuView {

    private final StackPane root;
    private final Button btnStory;
    private final Button btn1vs1;
    private final Button btnProfile;
    private final Button btnLeaderboard;
    private final Button btnBack;
    private final Button gearButton;

    public GameStartMenuView(String titleText) {
        Label headerLabel = ViewUtils.createPopupHeaderLabel(titleText);

        btnStory       = ViewUtils.createSpriteButton("/buttons/btn.png", "STORY", null);
        btn1vs1        = ViewUtils.createSpriteButton("/buttons/btn.png", "1VS1", null);
        btnProfile     = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_profile.png", 45, null);
        btnLeaderboard = ViewUtils.createSpriteButton("/buttons/btn.png", "LEADERBOARD", null);
        btnBack        = ViewUtils.createSpriteButton("/buttons/btn.png", "BACK", null);

        HBox profileLeaderboardRow = new HBox(15, btnProfile, btnLeaderboard);
        profileLeaderboardRow.setAlignment(Pos.CENTER);
        profileLeaderboardRow.setFillHeight(true);
        
        HBox.setHgrow(btnProfile, Priority.ALWAYS);
        HBox.setHgrow(btnLeaderboard, Priority.ALWAYS);

        VBox buttonBox = new VBox(15, btnStory, btn1vs1, profileLeaderboardRow, btnBack);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setFillWidth(true);

        VBox centerContent = new VBox(30, headerLabel, buttonBox);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setFillWidth(false);

        gearButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_settings.png", 48, null);

        root = new StackPane();
        // CRUCIAL: Transparent background
        root.setStyle("-fx-background-color: #ffffff;"); 
        root.getChildren().addAll(centerContent, gearButton);
        
        StackPane.setAlignment(gearButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(gearButton, new Insets(20));
    }

    public Node getRoot()               { return root; }
    public Button getStoryButton()      { return btnStory; }
    public Button get1vs1Button()       { return btn1vs1; }
    public Button getProfileButton()    { return btnProfile; }
    public Button getLeaderboardButton(){ return btnLeaderboard; }
    public Button getBackButton()       { return btnBack; }
    public Button getGearButton()       { return gearButton; }
}