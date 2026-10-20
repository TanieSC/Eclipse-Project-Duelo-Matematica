package controller;

import view.GameSettingsView;

/**
 * GAME SETTINGS CONTROLLER — the "brain" for the settings POPUP.
 *
 * Very small by design: the only thing this popup does right now is
 * close itself. As real settings (volume sliders, difficulty dropdowns,
 * theme radio buttons) are added to the view, their handlers get wired
 * in here — the view never talks to the model directly.
 *
 * Note the constructor signature: no Navigator. The popup doesn't swap
 * pages; it lives and dies on its own. When it closes, the main window
 * underneath simply becomes interactive again — no navigation needed.
 */
public class GameSettingsController {

    private final GameSettingsView view;

    public GameSettingsController(GameSettingsView view) {
        this.view = view;

        // "X" → close the popup. Nothing else to do yet.
        view.getBackButton().setOnAction(e -> view.close());
    }
}