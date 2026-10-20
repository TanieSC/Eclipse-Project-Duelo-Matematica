package controller;

import model.AuthService;
import model.GameService;
import model.MathQuestions;
import model.Session;
import view.*;

/**
 * MAIN MENU CONTROLLER — the "traffic cop" for the app.
 *
 * TWO KINDS OF NAVIGATION LIVE HERE:
 *
 *   1. CARD SWAPS (via the Navigator interface):
 *        menu, login, register, forgot-pass, topic-picker, resume, game
 *
 *   2. MODAL POPUPS (private helper):
 *        game settings
 *
 * The menu's first button has three possible behaviors depending on
 * state:
 *
 *   Not logged in        → open the login card
 *   Logged in + paused   → open the Resume card
 *   Logged in + fresh    → open the category picker
 *
 * That's a small but important UX improvement: the menu button no longer
 * re-opens a login prompt for someone who's already logged in.
 *
 * WHY THIS CLASS IMPLEMENTS NAVIGATOR:
 *   Every other controller (LoginController, TopicController,
 *   ResumeController, GameController) needs a way to say "take the user
 *   to page X". They don't know MainMenuView exists; they only know the
 *   Navigator interface. This class is the one place that translates
 *   "navigate to X" into "build view X, wire its controller, drop it
 *   into the window". That's the single point where all page-building
 *   happens.
 */
public class MainMenuController implements Navigator {

    // The window shell. Owns the StackPane that holds whichever card
    // is currently shown. Every navigation lands here as setContent(...).
    private final MainMenuView view;

    // The shared account manager. One instance for the whole app, so
    // every registration is visible to every later login attempt.
    // Also forwarded to LoginController and RegisterController.
    private final AuthService authService;

    /**
     * CONSTRUCTOR — wire up the three home-menu buttons.
     *
     * Called exactly once from App.start(), right after the view is
     * built and right before view.show() puts the window on screen.
     * By the time App.show() runs, every button already has its action
     * attached — no "dead" moment where a button is visible but inert.
     *
     * THE THREE WIRE-UPS:
     *
     *   Login button  → state-dependent. The menu button doubles as
     *                   "Login" (fresh session), "Logged in: X"
     *                   (returning player with paused game), or the
     *                   topic picker. See the lambda below.
     *
     *   Register      → always opens the registration card.
     *
     *   Game Settings → opens the modal settings popup (not a card
     *                   swap — see openGameSettings()).
     *
     * NO PLAY BUTTON EXISTS ON THE MENU. This is deliberate: the only
     * way into the game is via a successful login (which routes through
     * LoginController → goToTopicSelection or goToResume).
     */
    public MainMenuController(MainMenuView view, AuthService authService) {
        this.view = view;
        this.authService = authService;

        // First button: state-dependent (login / resume / topic).
        // The three branches in plain English:
        //   - Nobody logged in yet? Show the login card.
        //   - Logged in AND has an unfinished game? Offer to resume.
        //   - Logged in and starting fresh? Show the category picker.
        view.getLoginButton().setOnAction(e -> {
            if (!Session.isLoggedIn()) {
                goToLogin();
            } else if (Session.hasSavedGame()) {
                goToResume();
            } else {
                goToTopicSelection();
            }
        });

        view.getRegisterButton().setOnAction(e -> goToRegister());
        view.getSettingsButton().setOnAction(e -> openGameSettings());
    }

    // ============================================================
    // Card swaps (Navigator implementation)
    // ============================================================

    /**
     * GO TO MENU — return to the home card.
     *
     * Also refreshes the Login button's label so it reflects the
     * current session state:
     *
     *   Logged in  → "Logged in: <username>"
     *   Logged out → "Login"
     *
     * WHY REFRESH HERE? Because the menu card itself never changes
     * after it's built (it's cached as menuContent inside MainMenuView).
     * The only way to keep the label accurate is to update it whenever
     * the menu is shown. This method is the single funnel for that.
     *
     * WHO CALLS THIS:
     *   - LoginController's X button (cancel login)
     *   - RegisterController's X and success paths
     *   - ForgotPassController's X button
     *   - TopicController's X (cancel category choice)
     *   - ResumeController's X (cancel resume)
     *   - GameController.handleBack() (exit game)
     *   - Every other "get me out of here" path
     */
    @Override
    public void goToMenu() {
        view.showMenu();
        if (Session.isLoggedIn()) {
            view.getLoginButton().setText(
                "Logged in: " + Session.getCurrentUser().getUsername()
            );
        } else {
            view.getLoginButton().setText("Login");
        }
    }

    /**
     * GO TO LOGIN — show the login card.
     *
     * Standard three-step pattern used by every card swap:
     *   1. Build the view (pure UI, no actions attached yet).
     *   2. Build its controller, passing 'this' as the Navigator so the
     *      card can request further navigation (e.g. "forgot password").
     *   3. Drop the card's root Node into the window.
     *
     * Called by: the state-dependent Login button on the menu, when no
     * user is logged in.
     */
    @Override
    public void goToLogin() {
        LoginView v = new LoginView();
        new LoginController(v, authService, this);
        view.setContent(v.getRoot());
    }

    /**
     * GO TO REGISTER — show the create-account card.
     *
     * Same three-step pattern. RegisterController handles the form and
     * returns to the menu on success or cancel.
     *
     * Called by: the Register button on the menu.
     */
    @Override
    public void goToRegister() {
        RegisterView v = new RegisterView();
        new RegisterController(v, authService, this);
        view.setContent(v.getRoot());
    }

    /**
     * GO TO FORGOT PASSWORD — show the reset-password card.
     *
     * Same pattern. ForgotPassController currently validates the email
     * and shows a placeholder confirmation; the real reset flow can be
     * added later without changing this method.
     *
     * Called by: the "FORGOT PASSWORD?" link on the login card.
     */
    @Override
    public void goToForgotPassword() {
        ForgotPassView v = new ForgotPassView();
        new ForgotPassController(v, authService, this);
        view.setContent(v.getRoot());
    }

    /**
     * GO TO TOPIC SELECTION — show the category picker.
     *
     * The category card offers CALCULUS / STATISTICS / SYMBOLIC LOGIC /
     * ALL TOPICS. When a button is clicked, TopicController calls back
     * into this controller via goToGame(category), which starts a fresh
     * GameService filtered by that category.
     *
     * Called by:
     *   - LoginController after a successful login with no saved game
     *   - ResumeController's "CHOOSE NEW CATEGORY" button
     *   - The state-dependent menu button, when logged in with no
     *     saved game
     *   - Defensive fallbacks in goToResume() and resumeGame() when
     *     there's no saved game to resume
     */
    @Override
    public void goToTopicSelection() {
        TopicView v = new TopicView();
        new TopicController(v, this);
        view.setContent(v.getRoot());
    }

    /**
     * GO TO RESUME — show the "resume or new category?" card.
     *
     * Shown when the player has a paused game in Session. The card
     * displays a summary of the saved game (category, question N/10,
     * score) and offers two buttons:
     *
     *   RESUME GAME         → nav.resumeGame() (jump back in)
     *   CHOOSE NEW CATEGORY → nav.goToTopicSelection()
     *
     * Defensive fallback: if there's no saved game (shouldn't happen
     * given the caller checks hasSavedGame(), but a stray call must not
     * crash), just show the topic picker so the player isn't stuck.
     *
     * Called by:
     *   - LoginController after a successful login when a saved game
     *     exists
     *   - The state-dependent menu button, when logged in with a
     *     saved game
     */
    @Override
    public void goToResume() {
        if (!Session.hasSavedGame()) {
            goToTopicSelection();
            return;
        }
        ResumeView v = new ResumeView(Session.getSavedGame());
        new ResumeController(v, this);
        view.setContent(v.getRoot());
    }

    /**
     * GO TO GAME (fresh) — start a brand-new round with the chosen category.
     *
     * WHAT IT BUILDS:
     *   1. A fresh GameService filtered by 'category'. Loads ALL 50
     *      questions from MathQuestions, then prunes by category inside
     *      the GameService constructor. Pruning happens once here.
     *   2. A fresh GameView (pure JavaFX, ready immediately).
     *   3. A GameController with resume=false — this tells the
     *      controller "this is a fresh start, shuffle and go".
     *
     * The GameController constructor kicks off question 1 immediately.
     * No async load, no WebView — the card is on screen the moment
     * view.setContent(...) runs.
     *
     * Called by: TopicController, whenever the player clicks a category
     * button on the topic card.
     */
    @Override
    public void goToGame(String category) {
        GameService service = new GameService(MathQuestions.loadAll(), category);
        GameView v = new GameView();
        new GameController(v, service, this, false);
        view.setContent(v.getRoot());
    }

    /**
     * RESUME GAME — continue a paused round.
     *
     * CRUCIAL DIFFERENCE FROM goToGame: this uses the SAME GameService
     * instance that was saved in Session when the player clicked X
     * mid-round. Nothing is re-shuffled. The service's currentIndex,
     * score, session list, and category are all preserved, so the
     * player lands back on exactly the question they were on.
     *
     * GameController receives resume=true → it skips startNewGame()
     * and just calls renderCurrentQuestion() to redraw the saved state.
     *
     * The saved game stays in Session while the player is playing. If
     * they finish the round, GameController clears it; if they exit
     * mid-round again, the same instance is re-saved (trivially — it's
     * literally the same object).
     *
     * Defensive fallback: if the saved game vanished between the
     * ResumeView appearing and the player clicking RESUME (shouldn't
     * happen, but a stray call must not crash), redirect to the topic
     * picker.
     *
     * Called by: ResumeController, when the player clicks RESUME GAME.
     */
    @Override
    public void resumeGame() {
        GameService service = Session.getSavedGame();
        if (service == null) {
            goToTopicSelection();
            return;
        }
        GameView v = new GameView();
        new GameController(v, service, this, true);
        view.setContent(v.getRoot());
    }

    // ============================================================
    // Modal popup (not part of Navigator)
    // ============================================================

    /**
     * OPEN GAME SETTINGS — show the settings popup.
     *
     * Unlike every other destination above, settings is a MODAL POPUP:
     * a separate window that blocks the main window's input until it's
     * closed. That's why it's not on the Navigator interface — no other
     * card opens it, and it doesn't replace the current page (the menu
     * stays underneath, just unclickable).
     *
     * v.show() calls Stage.showAndWait(), which blocks here until the
     * popup closes. Nothing else runs in the meantime; the popup's X
     * button is wired by GameSettingsController to close it.
     *
     * Called by: the Game Settings button on the menu.
     */
    private void openGameSettings() {
        GameSettingsView v = new GameSettingsView(view.getStage());
        new GameSettingsController(v);
        v.show();
    }
}