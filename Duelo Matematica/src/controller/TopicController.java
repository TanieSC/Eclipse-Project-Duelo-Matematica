package controller;

import view.TopicView;

/**
 * TOPIC CONTROLLER — the "brain" behind the category card.
 *
 * Wires each category button to nav.goToGame("…"). The strings match
 * what GameService's constructor filters on:
 *
 *   "Calculus"        → all Calculus 1/2/3 topics
 *   "Statistics"      → Statistics only
 *   "Symbolic Logic"  → Symbolic Logic only
 *   "All"             → every question
 *
 * X cancels back to the menu.
 */
public class TopicController {

    private final TopicView view;
    private final Navigator nav;

    public TopicController(TopicView view, Navigator nav) {
        this.view = view;
        this.nav = nav;

        view.getCalculusButton()  .setOnAction(e -> nav.goToGame("Calculus"));
        view.getStatisticsButton().setOnAction(e -> nav.goToGame("Statistics"));
        view.getLogicButton()     .setOnAction(e -> nav.goToGame("Symbolic Logic"));
        view.getAllButton()       .setOnAction(e -> nav.goToGame("All"));
        view.getBackButton()      .setOnAction(e -> nav.goToMenu());
    }
}
