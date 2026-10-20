package controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import view.GameMainMenuView;
import view.GameSettingsView;

/**
 * GAME MAIN MENU CONTROLLER — The brain behind the secondary menu.
 * 
 * WHAT DOES THIS CLASS DO?
 * It wires up the buttons on the GameMainMenuView:
 * - START: navigates to the Category selection screen.
 * - CREDITS: shows a popup with credits.
 * - BACK: navigates back to the App's Main Menu.
 * - GEAR: opens the Game Settings popup.
 */
public class GameMainMenuController {

    private final GameMainMenuView view;
    private final Navigator nav;

    public GameMainMenuController(GameMainMenuView view, Navigator nav) {
        this.view = view;
        this.nav = nav;

        // --- WIRING THE BUTTONS ---
        
        // START -> Go to the topic selection screen
        view.getStartButton().setOnAction(e -> nav.goToTopicSelection());

        // BACK -> Return to the main app menu
        view.getBackButton().setOnAction(e -> nav.goToMenu());

        // CREDITS -> Show a simple popup
        view.getCreditsButton().setOnAction(e -> showCreditsPopup());

        // GEAR -> Open the Settings popup
        view.getGearButton().setOnAction(e -> openSettings());
    }

    private void showCreditsPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Credits");
        alert.setHeaderText("Duelo X Matematica");
        alert.setContentText("Created by:\nYour Name Here\n\nMath Questions Provided by:\nCalculus & Statistics Dept.");
        alert.showAndWait();
    }

    private void openSettings() {
        // Get the main window from the view to act as the owner for the popup
        Stage owner = (Stage) view.getRoot().getScene().getWindow();
        GameSettingsView settingsView = new GameSettingsView(owner);
        new GameSettingsController(settingsView);
        settingsView.show();
    }
}