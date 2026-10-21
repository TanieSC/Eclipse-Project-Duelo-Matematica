package controller;

import view.UserProfileView;

/**
 * USER PROFILE CONTROLLER — The brain for the Profile panel.
 * 
 * WHAT DOES THIS CLASS DO?
 * It listens for the "X" button and tells the Navigator to close the 
 * profile and return to the previous screen (the Game Start Menu).
 */
public class UserProfileController {

    private final UserProfileView view;
    private final Navigator nav;

    public UserProfileController(UserProfileView view, Navigator nav) {
        this.view = view;
        this.nav = nav;

        // Use closePopup() — do NOT navigate to a new screen!
        view.getBackButton().setOnAction(e -> nav.closePopup());
    }
}
