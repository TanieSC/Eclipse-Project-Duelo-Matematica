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

/**
 * GAME START MENU VIEW — The visual layout for the "Select Mode" screen.
 * 
 * WHAT DOES THIS CLASS DO?
 * It draws the "SELECT MODE" title, the Story/1v1/Profile/Leaderboard/Back 
 * buttons, and the Gear (settings) button in the bottom-left corner.
 * 
 * It is an INLINE view. It does not open a new window. The MainMenuController 
 * will grab its root Node and place it inside the main app window.
 */
public class GameStartMenuView {

    private final StackPane root;
    private final Button btnStory;
    private final Button btn1vs1;
    private final Button btnProfile;
    private final Button btnLeaderboard;
    private final Button btnBack;
    private final Button gearButton;

    public GameStartMenuView(String titleText) {
        // 1. Build Header
        Label headerLabel = new Label(titleText);
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setStyle(
            "-fx-font-size: 26px; -fx-font-weight: bold;" +
            "-fx-border-color: #222222; -fx-border-width: 3px;" +
            "-fx-padding: 12px 60px; -fx-background-color: #f4f4f4;" +
            "-fx-alignment: center; -fx-text-alignment: center;"
        );

        // 2. Build Main Buttons
        btnStory       = createBorderedButton("STORY");
        btn1vs1        = createBorderedButton("1VS1");
        btnProfile     = createBorderedButton("PROFILE");
        btnLeaderboard = createBorderedButton("LEADERBOARD");
        btnBack        = createBorderedButton("BACK");

        // 3. Side-by-side row for Profile & Leaderboard
        HBox profileLeaderboardRow = new HBox(15, btnProfile, btnLeaderboard);
        profileLeaderboardRow.setAlignment(Pos.CENTER);
        profileLeaderboardRow.setFillHeight(true);
        
        // Make the two side-by-side buttons share the space equally
        HBox.setHgrow(btnProfile, Priority.ALWAYS);
        HBox.setHgrow(btnLeaderboard, Priority.ALWAYS);

        // 4. Group all buttons vertically
        VBox buttonBox = new VBox(15, btnStory, btn1vs1, profileLeaderboardRow, btnBack);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setFillWidth(true);

        // 5. Stack the header and buttons in the center
        VBox centerContent = new VBox(30, headerLabel, buttonBox);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setFillWidth(false);

        // 6. Build the Gear Button (bottom-left)
        gearButton = createGearButton();

        // 7. Assemble Root Layout
        root = new StackPane();
        root.setStyle("-fx-background-color: #ffffff;");
        root.getChildren().addAll(centerContent, gearButton);
        
        // Pin the gear icon to the bottom-left
        StackPane.setAlignment(gearButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(gearButton, new Insets(20));
    }

    // --- UI HELPERS ---
    
    private Button createBorderedButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(55);
        button.setMaxHeight(55);
        button.setStyle(
            "-fx-font-size: 18px; -fx-font-weight: bold;" +
            "-fx-border-color: #000000; -fx-border-width: 3px;" +
            "-fx-padding: 10px 30px; -fx-background-color: #f9f9f9; -fx-cursor: hand;"
        );
        return button;
    }

    private Button createGearButton() {
        Button gearBtn = new Button("\u2699");
        gearBtn.setPrefSize(48, 48);
        gearBtn.setStyle(
            "-fx-font-size: 24px; -fx-font-weight: bold;" +
            "-fx-border-color: #000000; -fx-border-width: 2px;" +
            "-fx-border-radius: 50%; -fx-background-radius: 50%;" +
            "-fx-background-color: #f9f9f9; -fx-cursor: hand;"
        );
        return gearBtn;
    }

    // --- GETTERS for the Controller ---
    public Node getRoot()               { return root; }
    public Button getStoryButton()      { return btnStory; }
    public Button get1vs1Button()       { return btn1vs1; }
    public Button getProfileButton()    { return btnProfile; }
    public Button getLeaderboardButton(){ return btnLeaderboard; }
    public Button getBackButton()       { return btnBack; }
    public Button getGearButton()       { return gearButton; }
}