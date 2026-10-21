package controller;

import view.GameSettingsView;

public class GameSettingsController {

    private final GameSettingsView view;
    private final Navigator nav;

    public GameSettingsController(GameSettingsView view, Navigator nav) {
        this.view = view;
        this.nav = nav;

        // When "X" is clicked, go back to the Main Menu
        view.getBackButton().setOnAction(e -> nav.closePopup());
    }
}