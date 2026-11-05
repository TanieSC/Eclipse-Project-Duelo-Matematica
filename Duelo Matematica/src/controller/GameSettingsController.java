package controller;

import model.AudioManager;
import view.GameSettingsView;

public class GameSettingsController {

    private final GameSettingsView view;
    private final Navigator nav;

    public GameSettingsController(GameSettingsView view, Navigator nav) {
        this.view = view;
        this.nav = nav;

        // When "X" is clicked, go back to the Main Menu
        //view.getBackButton().setOnAction(e -> nav.closePopup());
        view.getBackButton().setOnAction(e -> {
           // AudioManager.playSFX("sfx_wax_break.wav");
            nav.closePopup();
        });
        
        // --- NEW: Wire the sliders to the AudioManager ---
        // Values are 0-100 in the UI, but 0.0-1.0 in JavaFX
        view.getMusicSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            AudioManager.setBgmVolume(newVal.doubleValue() / 100.0);
        });
        
        view.getSfxSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            AudioManager.setSfxVolume(newVal.doubleValue() / 100.0);
        });
    }
}