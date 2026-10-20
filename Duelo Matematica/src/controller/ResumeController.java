package controller;

import view.ResumeView;

/**
 * RESUME CONTROLLER — the "brain" behind the "Resume Game" screen.
 * 
 * WHAT DOES THIS CLASS DO?
 * When a player quits the game halfway through, the app saves their progress. 
 * The next time they log in, they see this Resume screen.
 * 
 * This controller is very simple. It just connects the three buttons on the 
 * screen to the corresponding actions:
 * 
 *   1. "RESUME GAME"         → Jump right back into the paused game.
 *   2. "CHOOSE NEW CATEGORY" → Go pick a new topic. 
 *   3. "X" (Close)           → Go back to the main menu.
 * 
 * AN IMPORTANT DESIGN CHOICE:
 * Notice that we do NOT delete the saved game if the player clicks "New Category" 
 * or "X". The game stays saved in the background. Why? Because the player might 
 * change their mind and come back to it later! The save file only gets overwritten 
 * if they actually start a brand new game and then quit halfway through *that* new game.
 */
public class ResumeController {

    /**
     * CONSTRUCTOR — runs once when the Resume screen is created.
     * 
     * This is where we wire up the buttons. We tell each button exactly 
     * which "remote control" (Navigator) action to trigger when clicked.
     * 
     * @param view The visual Resume screen with the buttons.
     * @param nav  The remote control used to change screens.
     */
    public ResumeController(ResumeView view, Navigator nav) {
        
        // When the player clicks "RESUME GAME", tell the remote control to 
        // load the saved game and jump right back in.
        view.getResumeButton()      .setOnAction(e -> nav.resumeGame());
        
        // When the player clicks "CHOOSE NEW CATEGORY", tell the remote control 
        // to show the category picker. (We keep the old game saved just in case!)
        view.getNewCategoryButton() .setOnAction(e -> nav.goToTopicSelection());
        
        // When the player clicks the "X", tell the remote control to just 
        // go back to the main menu. (Again, we keep the old game saved!)
        view.getBackButton()        .setOnAction(e -> nav.goToMenu());
    }
}