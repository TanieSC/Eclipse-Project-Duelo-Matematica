package controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import view.GameMainMenuView;
import view.GameSettingsView;

public class GameMainMenuController {

    private final GameMainMenuView view;
    private final Navigator nav;

    public GameMainMenuController(GameMainMenuView view, Navigator nav) {
        this.view = view;
        this.nav = nav;

        // START -> Go to the Game Start Menu (Select Mode)
        view.getStartButton().setOnAction(e -> nav.goToGameStartMenu());

        // BACK -> Return to the App Main Menu (Login Screen)
        view.getBackButton().setOnAction(e -> nav.goToMenu());

        // CREDITS -> Popup
        view.getCreditsButton().setOnAction(e -> showCreditsPopup());

        // GEAR -> Popup
        view.getGearButton().setOnAction(e -> openSettings());
    }

    private void showCreditsPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Credits");
        alert.setHeaderText("Duelo X Matematica");
        alert.setContentText("Created by:\nYour Name Here");
        alert.showAndWait();
    }

    private void openSettings() {
        Stage owner = (Stage) view.getRoot().getScene().getWindow();
        GameSettingsView settingsView = new GameSettingsView(owner);
        new GameSettingsController(settingsView);
        settingsView.show();
    }
}