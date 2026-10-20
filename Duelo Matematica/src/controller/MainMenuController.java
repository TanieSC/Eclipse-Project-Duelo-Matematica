package controller;

import model.AuthService;
import model.GameService;
import model.MathQuestions;
import model.Session;
import view.*;

/**
 * MAIN MENU CONTROLLER — the "Traffic Cop" and "Navigator" of the app.
 * 
 * WHAT DOES THIS CLASS DO?
 * Think of this class as the central switchboard for the whole application. 
 * It implements the `Navigator` interface (the "remote control"), which means 
 * it is the ONLY class that knows how to build every screen and swap them out 
 * in the main window.
 * 
 * When you are on the main menu and click "Login", this class handles it.
 * When a player finishes a game and clicks "X", this class handles it. 
 * It connects the View (the UI) to the Model (the data and rules).
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
     * 
     * @param view The visual window shell.
     * @param authService The shared account manager.
     */
    public MainMenuController(MainMenuView view, AuthService authService) {
        this.view = view;
        this.authService = authService;

        view.getLoginButton().setOnAction(e -> {
            if (!Session.isLoggedIn()) {
                goToLogin();
            } else {
                goToGameMainMenu(); // Skip login if already logged in
            }
        });
        // --- WIRING THE REGISTER BUTTON ---
        // Simple: just open the Registration screen.
        view.getRegisterButton().setOnAction(e -> goToRegister());
        
        // --- WIRING THE EXIT BUTTON ---
        // Shuts the app down completely.
        view.getExitButton().setOnAction(e -> {
            // Safely tell JavaFX to close all its windows and stop its background processes.
            javafx.application.Platform.exit(); 
            // Force the entire Java program to terminate (just to be absolutely sure).
            System.exit(0);                     
        });
    }

    // ============================================================
    // NAVIGATION METHODS (The "Remote Control" implementation)
    // ============================================================
    // --- Navigation Implementations ---

    @Override
    public void goToMenu() {
        view.showMenu();
        if (Session.isLoggedIn()) {
            view.getLoginButton().setText("Logged in: " + Session.getCurrentUser().getUsername());
        } else {
            view.getLoginButton().setText("LOGIN");
        }
    }

    @Override
    public void goToLogin() {
        LoginView v = new LoginView();
        new LoginController(v, authService, this);
        view.setContent(v.getRoot());
    }

    @Override
    public void goToGameMainMenu() {
        model.GameMenuModel model = new model.GameMenuModel("MAIN MENU");
        GameMainMenuView v = new GameMainMenuView(model.getTitle());
        new GameMainMenuController(v, this);
        view.setContent(v.getRoot());
    }

    @Override
    public void goToGameStartMenu() {
        model.GameStartMenuModel model = new model.GameStartMenuModel("SELECT MODE");
        GameStartMenuView v = new GameStartMenuView(model.getTitle());
        new GameStartMenuController(v, this);
        view.setContent(v.getRoot());
    }

    @Override
    public void goToTopicSelection() {
        TopicView v = new TopicView();
        new TopicController(v, this);
        view.setContent(v.getRoot());
    }
    
    /**
     * GO TO REGISTER — Builds the Registration screen and its brain.
     * (Same 3-step pattern as above).
     */
    @Override
    public void goToRegister() {
        RegisterView v = new RegisterView();
        new RegisterController(v, authService, this);
        view.setContent(v.getRoot());
    }

    /**
     * GO TO FORGOT PASSWORD — Builds the Password Reset screen and its brain.
     * (Same 3-step pattern as above).
     */
    @Override
    public void goToForgotPassword() {
        ForgotPassView v = new ForgotPassView();
        new ForgotPassController(v, authService, this);
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
        // If there is no saved game, just go pick a category instead.
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
     * 1. Create a new GameService (loads all 50 questions, then filters them 
     *    down to just the chosen category).
     * 2. Create the GameView.
     * 3. Create the GameController. We pass 'false' for the 'resume' parameter. 
     *    This tells the controller: "This is a fresh start! Shuffle the deck 
     *    and start at Question 1."
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
     * Instead of making a new GameService, we pull the saved one out of memory 
     * (Session). We pass 'true' for the 'resume' parameter, which tells the 
     * controller: "Do NOT shuffle the deck! Just draw the exact question the 
     * player left off on."
     */
    @Override
    public void resumeGame() {
        GameService service = Session.getSavedGame();
        
        // Safety fallback: If somehow the save was deleted, go to category picker.
        if (service == null) {
            goToTopicSelection(); 
            return;
        }
        
        GameView v = new GameView();
        // Passing 'true' means we are resuming!
        new GameController(v, service, this, true);
        view.setContent(v.getRoot());
    }
}