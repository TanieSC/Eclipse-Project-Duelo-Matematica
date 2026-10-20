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
            	 goToGameMainMenu();
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

    /**
     * GO TO MENU — Takes the user back to the home screen.
     * 
     * It also updates the Login button text. If a user is logged in, 
     * it will change the button to say "Logged in: [Username]".
     */
    @Override
    public void goToMenu() {
        // 1. Tell the view to show the menu layout.
        view.showMenu();
        
        // 2. Update the button text to reflect who is logged in.
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
     * This is a standard 3-step pattern used for almost every screen:
     * 1. Create the visual layout (View).
     * 2. Create the brain (Controller) and give it the View, the shared 
     *    data (authService), and a remote control (this).
     * 3. Give the View to the main window so it can be displayed.
     */
    @Override
    public void goToLogin() {
        LoginView v = new LoginView();
        new LoginController(v, authService, this);
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
     * GO TO GAME MAIN MENU — Builds the secondary menu after login.
     * 
     * This shows the "START", "CREDITS", and "BACK" buttons.
     */
    @Override
    public void goToGameMainMenu() {
        // 1. Create the model (holds the title)
        model.GameMenuModel model = new model.GameMenuModel("MAIN MENU");
        
        // 2. Create the view with the model's data
        GameMainMenuView v = new GameMainMenuView(model.getTitle());
        
        // 3. Create the controller to wire the buttons
        new GameMainMenuController(v, this);
        
        // 4. Swap the screen!
        view.setContent(v.getRoot());
    }
    
   
    /**
     * GO TO TOPIC SELECTION — Builds the Category Picker screen and its brain.
     * (Same 3-step pattern, but we don't need to pass authService here).
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