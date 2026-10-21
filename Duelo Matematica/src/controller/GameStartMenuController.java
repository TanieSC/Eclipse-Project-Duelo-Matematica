package controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Session;
import model.UserProfileModel;
import view.GameSettingsView;
import view.GameStartMenuView;
import view.UserProfileView;

public class GameStartMenuController {

    private final GameStartMenuView view;
    private final Navigator nav;

    public GameStartMenuController(GameStartMenuView view, Navigator nav) {
        this.view = view;
        this.nav = nav;

        // --- WIRING THE BUTTONS ---

        // STORY -> Go to the Topic/Category Selection screen!
        view.getStoryButton().setOnAction(e -> nav.goToTopicSelection());

        // BACK -> Return to the Game Main Menu
        view.getBackButton().setOnAction(e -> nav.goToGameMainMenu());

        // GEAR -> Open Settings Popup
        view.getGearButton().setOnAction(e -> nav.goToSettings());

        // 1VS1, PROFILE, LEADERBOARD -> Placeholder popups for now
        view.get1vs1Button().setOnAction(e -> showDummyPopup("1VS1 MODE"));
        view.getProfileButton().setOnAction(e -> nav.goToUserProfile());
        view.getLeaderboardButton().setOnAction(e -> showDummyPopup("LEADERBOARD"));
    }

    
    private void showDummyPopup(String buttonName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText(null);
        alert.setContentText(buttonName + " clicked! (Coming soon)");
        alert.showAndWait();
    }

}