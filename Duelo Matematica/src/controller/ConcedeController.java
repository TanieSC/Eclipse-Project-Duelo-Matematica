package controller;

import view.ConcedeView;

public class ConcedeController {

    public ConcedeController(ConcedeView view, Navigator nav) {
        // Clicking "NO" or the Red X just closes the popup and returns to the game
        view.getNoButton().setOnAction(e -> nav.closePopup());
        view.getCloseButton().setOnAction(e -> nav.closePopup());

        // Clicking "YES" closes the popup and triggers the surrender logic
        view.getYesButton().setOnAction(e -> {
            nav.closePopup();
            nav.goToGameStartMenu(); // Or wherever you want to send them after conceding
        }); 
    }
}
