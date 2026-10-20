package controller;

import model.GameService;
import model.Question;
import model.Session;
import view.GameView;

/**
 * GAME CONTROLLER — the "brain" of the quiz card.
 *
 * Two new behaviors compared to the previous version:
 *
 *   1. CONSTRUCTOR FLAG:
 *        resume = false → fresh game (shuffle, start at Q1)
 *        resume = true  → paused game (render the CURRENT question
 *                          without reshuffling)
 *
 *   2. X BUTTON:
 *        - If the round is still in progress → save the GameService
 *          into Session.savedGame, then go to the menu. Player can
 *          resume later.
 *        - If the round is finished → clear savedGame (nothing worth
 *          resuming), then go to the menu.
 *
 * Everything else (answer pick → score, NEXT → advance, PLAY AGAIN →
 * restart) is unchanged. Play Again always starts fresh and leaves
 * savedGame alone until the player exits — that way X-mid-round on a
 * restarted game saves the new progress.
 */
public class GameController {

    // The card we drive. Built by MainMenuController and passed in.
    private final GameView view;

    // The "referee" holding question pool, shuffle order, and score.
    // For a fresh game this is brand new; for a resume it's the SAME
    // instance that was saved in Session.savedGame last time.
    private final GameService service;

    // The remote control for page swaps (menu, topic picker, …).
    private final Navigator nav;

    /**
     * CONSTRUCTOR — wire the view's callbacks and kick off the round.
     *
     * WHAT HAPPENS IN ORDER:
     *   1. Store the three collaborators so the methods below can use them.
     *   2. Attach four callbacks to the view. Each callback is a method
     *      reference — when the view fires it, it calls back into THIS
     *      controller. The view stays a pure display surface; all the
     *      "what should happen" logic lives here.
     *   3. Start the round in one of two ways:
     *        - resume=true  → redraw the question the player was on.
     *          No reshuffle, no score reset — the GameService already
     *          knows where it left off.
     *        - resume=false → shuffle and jump to question 1.
     *
     * WHO CALLS THIS:
     *   MainMenuController.goToGame(category)   → new GameService, resume=false
     *   MainMenuController.resumeGame()         → Session.getSavedGame(), resume=true
     *
     * NO ASYNC LOAD: unlike the WebView version, everything here is
     * synchronous. showQuestion() returns as soon as the JavaFX nodes
     * exist, so the first question is on screen immediately.
     */
    public GameController(GameView view, GameService service, Navigator nav, boolean resume) {
        this.view = view;
        this.service = service;
        this.nav = nav;

        // Wire the four user actions to private methods below.
        // 'this::handleXxx' is a method reference — same as writing
        // 'e -> handleXxx()' for a button click, but shorter.
        view.setOnAnswerPicked(this::handleAnswer);
        view.setOnNext(this::handleNext);
        view.setOnPlayAgain(this::handlePlayAgain);
        view.setOnBack(this::handleBack);   // ← saves on exit

        if (resume) {
            // Paused game — don't reshuffle; just redraw the current Q.
            renderCurrentQuestion();
        } else {
            // Fresh game — start at question 1.
            startGame();
        }
    }

    /**
     * START A FRESH ROUND — shuffle and show question 1.
     *
     * Two things happen:
     *   1. service.startNewGame() re-shuffles the pool, picks 10
     *      questions, resets currentIndex=0, score=0, and shuffles
     *      question 1's four options.
     *   2. renderCurrentQuestion() pushes that state into the view.
     *
     * WHEN IT RUNS:
     *   - Once from the constructor when resume=false.
     *   - Again every time the player clicks PLAY AGAIN.
     *
     * The category filter inside the GameService is UNTOUCHED, so Play
     * Again replays the same category — the player would have to leave
     * the game and pick a different category to switch topics.
     */
    private void startGame() {
        service.startNewGame();
        renderCurrentQuestion();
    }

    /**
     * RENDER THE CURRENT QUESTION — copy state from the model into the view.
     *
     * THE TRANSLATION LAYER. This is the only place where model types
     * (Question, String[], int) are unpacked into plain values that the
     * view can consume. The view never touches the Question object
     * directly — it just gets the fields it needs, one by one:
     *
     *   q.getText()                    raw prompt (may contain $...$)
     *   service.getCurrentOptions()    4 shuffled answer strings
     *   service.getCorrectOptionIndex() which of those 4 is right
     *   service.getCategory()          e.g. "Calculus" for the header
     *   q.getDifficulty()              "Easy" … for the colored badge
     *   service.getCurrentIndex() + 1  1-based number for "Q 3/10"
     *   service.getTotal()             10
     *   service.getScore()             running score
     *
     * CALLED BY:
     *   - startGame()          (fresh game, first question)
     *   - constructor (resume) (paused game, redraw current question)
     *   - handleNext()         (after the player clicks NEXT)
     *
     * The view's showQuestion(...) does the LaTeX rendering and rebuilds
     * the four answer buttons. By the time it returns, the card is fully
     * updated for the new question.
     */
    private void renderCurrentQuestion() {
        Question q = service.getCurrentQuestion();
        view.showQuestion(
            q.getText(),
            service.getCurrentOptions(),
            service.getCorrectOptionIndex(),
            service.getCategory(),
            q.getDifficulty(),
            service.getCurrentIndex() + 1,
            service.getTotal(),
            service.getScore()
        );
    }

    /**
     * USER PICKED AN ANSWER — score it, then reveal the NEXT button.
     *
     * The view has already visually marked the correct answer green and
     * the wrong pick (if any) red — that's presentation logic it owns.
     * Here we only care about the SCORE:
     *
     *   service.submitAnswer(pickedIndex):
     *     - compares pickedIndex to the correct one
     *     - if they match, score++ inside the service
     *     - returns true/false, which we don't need right now
     *
     * After scoring, we showNextButton() so the player can move on.
     * The NEXT button stays hidden until this method runs — that's what
     * prevents the player from skipping a question without answering.
     *
     * CALLED BY:
     *   The view's onAnswerPicked callback — fired when the player clicks
     *   one of the four answer buttons. The view fires this exactly once
     *   per question (it disables all four after the first click).
     */
    private void handleAnswer(int pickedIndex) {
        service.submitAnswer(pickedIndex);
        view.showNextButton();
    }

    /**
     * USER CLICKED NEXT — advance, then either draw the next question
     * or, if the round is over, show PLAY AGAIN.
     *
     * TWO OUTCOMES:
     *
     *   A. There are more questions:
     *      service.advance() bumps currentIndex and reshuffles the next
     *      question's four options. renderCurrentQuestion() draws it.
     *      The cycle repeats.
     *
     *   B. That was question 10:
     *      service.advance() pushes currentIndex past the end, so
     *      isGameOver() returns true. We:
     *        - clear Session.savedGame (a completed round isn't worth
     *          resuming — if the player leaves now, there's nothing to
     *          come back to),
     *        - show the PLAY AGAIN button so they can start a fresh
     *          round without leaving the game card.
     *
     * CALLED BY:
     *   The view's onNext callback — fired when the player clicks NEXT.
     *   NEXT is only visible after they've answered, so we never get
     *   here mid-question.
     */
    private void handleNext() {
        service.advance();
        if (service.isGameOver()) {
            // Round finished — nothing to resume.
            Session.clearSavedGame();
            view.showPlayAgainButton();
        } else {
            renderCurrentQuestion();
        }
    }

    /**
     * USER CLICKED PLAY AGAIN — restart with a fresh shuffle.
     *
     * The GameService's category filter is already baked in, so
     * startNewGame() reshuffles the SAME category's questions. The
     * player stays on whatever topic they originally picked.
     *
     * This also (implicitly) resets currentIndex and score — no
     * leftover state from the previous round.
     *
     * CALLED BY:
     *   The view's onPlayAgain callback — only visible after the round
     *   is over (isGameOver() true).
     */
    private void handlePlayAgain() {
        // Restart with the same category filter (pool is preserved).
        startGame();
    }

    /**
     * X BUTTON — leave the game, saving progress if the round isn't over.
     *
     * THE PAUSE/RESUME CORE. Two branches, chosen by isGameOver():
     *
     *   STILL PLAYING (mid-round):
     *     Session.setSavedGame(service) — stash the live GameService
     *     in a global slot. Its currentIndex, score, session list, and
     *     category all travel with it. Next time the player logs in
     *     (or clicks the logged-in menu button), the ResumeView will
     *     offer to jump back to this exact state.
     *
     *   ALREADY FINISHED (game over):
     *     Session.clearSavedGame() — nothing worth resuming. Any
     *     previous saved game (from an earlier session) is also
     *     cleared, so the player won't see a stale "Resume" card for
     *     a round they finished last week.
     *
     * Then nav.goToMenu() returns to the home menu. The login button
     * there will show "Logged in: <user>" and, when clicked, route to
     * either the Resume card or the Category picker depending on
     * whether a savedGame exists at that moment.
     *
     * WHY 'service' AND NOT A COPY?
     *   GameService is a plain Java object with no UI state. Handing
     *   the whole instance to Session is safe and cheap: no cloning,
     *   no serialization. Resuming the game literally re-uses this
     *   same object, so there's nothing to reconcile.
     *
     * CALLED BY:
     *   The view's onBack callback — fired when the player clicks X in
     *   the game card's top-right corner.
     */
    private void handleBack() {
        if (!service.isGameOver()) {
            Session.setSavedGame(service);
        } else {
            Session.clearSavedGame();
        }
        nav.goToMenu();
    }
}