package controller;

import view.ResumeView;

/**
 * RESUME CONTROLLER — the "brain" behind the resume card.
 *
 * Three actions, three wire-ups:
 *   RESUME GAME         → nav.resumeGame()        (jump back in)
 *   CHOOSE NEW CATEGORY → nav.goToTopicSelection() (leave saved game alone)
 *   X                    → nav.goToMenu()          (leave saved game alone)
 *
 * The controller deliberately does NOT clear Session.savedGame() on the
 * "new category" or "X" paths. The paused game stays around so the
 * player can change their mind — it only gets overwritten when the
 * player actually starts a new game and clicks X mid-round.
 */
public class ResumeController {

    public ResumeController(ResumeView view, Navigator nav) {
        view.getResumeButton()      .setOnAction(e -> nav.resumeGame());
        view.getNewCategoryButton() .setOnAction(e -> nav.goToTopicSelection());
        view.getBackButton()        .setOnAction(e -> nav.goToMenu());
    }
}