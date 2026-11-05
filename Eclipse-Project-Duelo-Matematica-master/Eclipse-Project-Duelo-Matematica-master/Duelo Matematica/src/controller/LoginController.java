package controller;

import model.AuthService;
import model.Session;
import model.User;
import view.LoginView;

/**
 * LOGIN CONTROLLER — the "brain" behind the login card.
 *
 * WHAT DOES THIS CLASS DO?
 * It sits between the visual Login screen and the app's memory. It listens 
 * for button clicks and decides what should happen next.
 * 
 * The rules are:
 *   - CONFIRM clicked        → Check if the email and password are correct.
 *                              If yes, remember the user and go to the game.
 *                              If no, show a red error message.
 *   - "X" clicked            → Cancel and go back to the main menu.
 *   - "FORGOT PASSWORD?" hit → Open the password reset screen.
 *
 * A VERY IMPORTANT RULE:
 * This screen is the ONLY way to reach the game. There is no "Play" button 
 * on the main menu. You *must* log in successfully to play.
 */
public class LoginController {

    // 1. The visual screen (the text boxes and buttons).
    private final LoginView view;           
    
    // 2. The "Rulebook" that knows all the registered accounts.
    private final AuthService authService;  
    
    // 3. The "Remote Control" used to switch to different screens.
    private final Navigator nav;            

    /**
     * CONSTRUCTOR — runs once when the Login screen is first created.
     * 
     * This is where we wire up the buttons. We say: "Hey view, when 
     * the user clicks this button, run this specific method."
     */
    public LoginController(LoginView view, AuthService authService, Navigator nav) {
        this.view = view;
        this.authService = authService;
        this.nav = nav;

        // --- WIRING THE BUTTONS ---
        // When CONFIRM is clicked, run our handleLogin() method.
        view.getConfirmButton()     .setOnAction(_ -> handleLogin());
        
        view.getBackButton().setOnAction(_ -> nav.closePopup());
        
        // When "FORGOT PASSWORD?" is clicked, tell the Navigator to open the reset screen.
        view.getForgotPasswordLink().setOnAction(e -> nav.goToForgotPassword());
    }

    /**
     * HANDLE LOGIN — the step-by-step logic that runs when CONFIRM is clicked.
     */
    private void handleLogin() {
        // Grab whatever the user typed into the boxes.
        // (We use .trim() on the email to remove accidental spaces at the beginning/end).
        String email    = view.getEmailField().getText().trim();
        String password = view.getPasswordField().getText();

        // STEP 1: Did they fill out both boxes?
        if (email.isEmpty() || password.isEmpty()) {
            // If either box is empty, show a red error and STOP right here.
            view.showMessage("Please fill in all fields.");
            return; 
        }

        // STEP 2: Ask the Rulebook (AuthService) if the email and password match.
        // It will return a 'User' object if they match, or 'null' if they don't.
        User user = authService.login(email, password);
        
        if (user == null) {
            // If the user is null, it means the email doesn't exist or the password is wrong.
            view.showMessage("Invalid email or password.");
            return; // STOP right here.
        }

        // STEP 3: Success! The password was correct.
        // We save this user into the app's "Memory" (Session) so other screens 
        // know who is playing.
        Session.setCurrentUser(user);
        
        nav.goToGameMainMenu();
    }
}