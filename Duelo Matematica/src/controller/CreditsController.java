package controller;

import view.CreditsView;

public class CreditsController {
    private final CreditsView view;
    private final Navigator nav;

    public CreditsController(CreditsView view, Navigator nav) {
        this.view = view;
        this.nav = nav;

        // Clicking "X" closes the popup
        view.getBackButton().setOnAction(e -> nav.closePopup());
    }
}
