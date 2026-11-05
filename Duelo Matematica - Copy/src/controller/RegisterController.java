package controller;

import model.AuthService;
import view.RegisterView;

/**
 * REGISTER CONTROLLER — the "brain" behind the create-account screen.
 *
 * WHAT DOES THIS CLASS DO?
 * It listens to the buttons on the Registration screen and decides what to do.
 * When the user clicks "CONFIRM", it checks if they filled out the boxes 
 * and asks the database (AuthService) to create the new account.
 * 
 * DESIGN CHOICE: AUTO-LOGIN?
 * We deliberately do NOT log the user in automatically after they register. 
 * We send them back to the main menu so they get used to the login flow. 
 * If we wanted to auto-login, we would just call `authService.login()` 
 * right here and tell the app they are logged in.
 */
public class RegisterController {

    // 1. The visual screen (the text boxes and buttons).
    private final RegisterView view;        
        
    // 2. The "Rulebook" that handles creating and checking accounts.
    private final AuthService authService;  
    
    // 3. The "Remote Control" used to switch to different screens.
    private final Navigator nav;            

    /**
     * CONSTRUCTOR — runs once when the Registration screen is first created.
     * 
     * This is where we wire up the buttons.
     */
    public RegisterController(RegisterView view, AuthService authService, Navigator nav) {
        this.view = view;
        this.authService = authService;
        this.nav = nav;

        // When the "CONFIRM" button is clicked, run our handleRegister() method.
        view.getConfirmButton().setOnAction(e -> handleRegister());
        
        // When the "X" button is clicked, tell the Navigator to cancel and go to the main menu.
        view.getBackButton().setOnAction(e -> nav.closePopup());
    }

    /**
     * HANDLE REGISTER — the step-by-step logic that runs when CONFIRM is clicked.
     */
    private void handleRegister() {
        // Grab whatever the user typed into the boxes.
        // We use .trim() to remove any accidental spaces at the beginning/end.
        String email    = view.getEmailField().getText().trim();
        String username = view.getUsernameField().getText().trim();
        String password = view.getPasswordField().getText();

        // STEP 1: Did they fill out ALL the boxes?
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            // If any box is empty, show a red error message and STOP.
            view.showMessage("Please fill in all fields.");
            return; 
        }

        // STEP 2: Ask the Rulebook (AuthService) to create the account.
        // It returns 'true' if successful, or 'false' if the email is already taken.
        if (!authService.register(email, username, password)) {
            // If it returns false, it means the email is already in the system.
            view.showMessage("Email already registered.");
            return; // STOP right here.
        }

        // STEP 3: Success! The account was created.
        // Send the user back to the main menu so they can log in.
        nav.goToMenu();
    }
}