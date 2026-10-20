package controller;

import model.AuthService;
import model.GameService;
import model.MathQuestions;
import model.Session;
import view.*;

/**
 * MAIN MENU CONTROLLER — the "Traffic Cop" of the app.
 * 
 * WHAT DOES THIS CLASS DO?
 * Think of this class as the central hub for navigation. It implements the 
 * `Navigator` interface, which means it is the ONLY class that actually knows 
 * how to build every screen and swap them out in the main window.
 * 
 * When you are on the main menu and click "Login", this class handles it.
 * When you finish a game and click "X", this class handles it. It is the 
 * central switchboard.
 */
public class MainMenuController implements Navigator {

    // The actual physical window shell where everything is drawn.
    private final MainMenuView view;
    
    // The "Rulebook" that knows all registered accounts.
    private final AuthService authService;

    /**
     * CONSTRUCTOR — runs once when the app first starts.
     * 
     * This is where we wire up the buttons on the main menu.
     */
    public MainMenuController(MainMenuView view, AuthService authService) {
        this.view = view;
        this.authService = authService;

        // --- WIRING THE LOGIN BUTTON ---
        // The Login button is special. It acts differently depending on the situation.
        view.getLoginButton().setOnAction(e -> {
            if (!Session.isLoggedIn()) {
                // Scenario 1: Nobody is logged in. Show the Login screen.
                goToLogin();
            } else if (Session.hasSavedGame()) {
                // Scenario 2: A user is logged in, and they quit a game halfway through.
                // Show the "Resume or New Category?" screen.
                goToResume();
            } else {
                // Scenario 3: A user is logged in, but has no paused game.
                // Skip the login and go straight to picking a category.
                goToTopicSelection();
            }
        });

        // --- WIRING THE REGISTER BUTTON ---
        // Simple: just open the Registration screen.
        view.getRegisterButton().setOnAction(e -> goToRegister());
    }

    // ============================================================
    // NAVIGATION METHODS (The "Remote Control" implementation)
    // ============================================================

    /**
     * GO TO MENU — Takes the user back to the home screen.
     * 
     * It also updates the Login button text. If a user is logged in, 
     * it will change the button to say "Logged in: [Username]".
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
     * GO TO LOGIN — Builds the Login screen and its brain.
     * 
     * 1. Create the visual LoginView.
     * 2. Create the LoginController and pass it the view and the 'authService'.
     * 3. Drop the view into the main window.
     */
    @Override
    public void goToLogin() {
        LoginView v = new LoginView();
        new LoginController(v, authService, this);
        view.setContent(v.getRoot());
    }

    /**
     * GO TO REGISTER — Builds the Registration screen and its brain.
     */
    @Override
    public void goToRegister() {
        RegisterView v = new RegisterView();
        new RegisterController(v, authService, this);
        view.setContent(v.getRoot());
    }

    /**
     * GO TO FORGOT PASSWORD — Builds the Password Reset screen and its brain.
     */
    @Override
    public void goToForgotPassword() {
        ForgotPassView v = new ForgotPassView();
        new ForgotPassController(v, authService, this);
        view.setContent(v.getRoot());
    }

    /**
     * GO TO TOPIC SELECTION — Builds the Category Picker screen and its brain.
     */
    @Override
    public void goToTopicSelection() {
        TopicView v = new TopicView();
        new TopicController(v, this);
        view.setContent(v.getRoot());
    }

    /**
     * GO TO RESUME — Builds the "Resume Game?" prompt.
     * 
     * Safety Check: If there is no saved game for some reason, 
     * it redirects safely to the category picker instead of crashing.
     */
    @Override
    public void goToResume() {
        if (!Session.hasSavedGame()) {
            goToTopicSelection();
            return;
        }
        
        // We pass the saved game to the view so it can display the stats 
        // (e.g., "Question 5/10, Score: 3").
        ResumeView v = new ResumeView(Session.getSavedGame());
        new ResumeController(v, this);
        view.setContent(v.getRoot());
    }

    /**
     * GO TO GAME — Starts a brand new game with the chosen category.
     * 
     * 1. Create a new GameService (loads all questions, filters by category).
     * 2. Create the GameView.
     * 3. Create the GameController. We pass 'false' for the 'resume' parameter,
     *    which tells the controller to shuffle the deck and start at Question 1.
     */
    @Override
    public void goToGame(String category) {
        GameService service = new GameService(MathQuestions.loadAll(), category);
        GameView v = new GameView();
        new GameController(v, service, this, false);
        view.setContent(v.getRoot());
    }

    /**
     * RESUME GAME — Continues a paused game.
     * 
     * Instead of making a new GameService, we pull the saved one out of memory.
     * We pass 'true' for the 'resume' parameter, which tells the controller 
     * NOT to shuffle the deck, but to just draw the exact question the player 
     * left off on.
     */
    @Override
    public void resumeGame() {
        GameService service = Session.getSavedGame();
        if (service == null) {
            goToTopicSelection(); // Safety fallback
            return;
        }
        GameView v = new GameView();
        new GameController(v, service, this, true);
        view.setContent(v.getRoot());
    }
}