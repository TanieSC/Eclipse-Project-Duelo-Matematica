package controller;

import view.LeaderBoardView;

/**
 * LEADERBOARD CONTROLLER — The brain behind the Leaderboard screen.
 * 
 * WHAT DOES THIS CLASS DO?
 * It listens for the "BACK" button and the "Gear" (Settings) button.
 */
public class LeaderBoardController {

    private final LeaderBoardView view;
    private final Navigator nav;

    public LeaderBoardController(LeaderBoardView view, Navigator nav) {
        this.view = view;
        this.nav = nav;

        // Clicking "BACK" closes the popup (revealing the Game Start Menu underneath)
        view.getBackButton().setOnAction(e -> nav.closePopup());
        
    }
}