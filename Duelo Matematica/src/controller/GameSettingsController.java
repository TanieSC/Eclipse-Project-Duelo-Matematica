package controller;

import view.GameSettingsView;

/**
 * GAME SETTINGS CONTROLLER — the "brain" for the settings POPUP.
 *
 * WHAT DOES THIS CLASS DO?
 * In this app, we split code into two main parts:
 *   1. The View (GameSettingsView): Draws the window and the buttons.
 *   2. The Controller (This file): Listens for clicks and decides what to do.
 * 
 * This specific controller is very small right now. Its ONLY job is to 
 * wait for the user to click the "X" button and then close the window.
 * 
 * WHY IS THERE NO NAVIGATOR HERE?
 * Unlike the other screens (like Login or the Main Menu), this is a "popup" window. 
 * It doesn't need to swap to a different page. It just opens on top of the game, 
 * lets you look at it, and then closes so you can get back to playing. When 
 * it closes, the game underneath is already there waiting for you.
 * 
 * In the future, if you add volume sliders or difficulty settings, you will 
 * write the code for those inside this class.
 */
public class GameSettingsController {

    // This holds a reference to the actual visual window so we can tell it to close.
    private final GameSettingsView view;

    /**
     * CONSTRUCTOR — runs as soon as the Settings popup is created.
     * 
     * @param view The visual popup window that was just built.
     */
    public GameSettingsController(GameSettingsView view) {
        this.view = view;

        // --- WIRING THE "X" BUTTON ---
        // We grab the "X" button from the visual view.
        // We tell it: "When someone clicks you, run the view.close() method."
        // The 'e' stands for 'event' (the click), but we don't need to use it here.
        view.getBackButton().setOnAction(e -> view.close());
    }
}