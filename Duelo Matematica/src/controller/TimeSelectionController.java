package controller;

import view.TimeSelectionView;

public class TimeSelectionController {

    public TimeSelectionController(TimeSelectionView view, Navigator nav) {
        view.get10MinButton().setOnAction(e -> nav.goTo1v1Matchmaking(10 * 60));
        view.get25MinButton().setOnAction(e -> nav.goTo1v1Matchmaking(25 * 60));
        view.get30MinButton().setOnAction(e -> nav.goTo1v1Matchmaking(30 * 60));

        // X just closes the popup, revealing GameStartMenu underneath
        view.getBackButton().setOnAction(e -> nav.closePopup());
    }
}
