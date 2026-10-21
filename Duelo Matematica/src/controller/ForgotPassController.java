package controller;

import model.AuthService;
import view.ForgotPassView;

/**
 * FORGOT PASSWORD CONTROLLER — the "brain" behind the reset card.
 *
 * WHAT DOES A CONTROLLER DO?
 * In this app, we split the code into three parts (MVC):
 *   1. View   (ForgotPassView)   → Draws the screen and the buttons.
 *   2. Model  (AuthService)      → Knows the rules and the data.
 *   3. Controller (This file)    → The middleman. It listens for button 
 *                                  clicks from the View and decides what 
 *                                  the Model should do.
 *
 * Right now, this controller only checks if the email field is empty. 
 * When the "Create New Password" flow is built in the future, the TODO 
 * branch below will route the user there. Keeping this logic here (and 
 * out of the View) means we can upgrade the reset feature later without 
 * having to touch the visual layout at all.
 */
public class ForgotPassController {

    // 1. The screen we are controlling.
    private final ForgotPassView view;

    // 2. The "rulebook" for accounts (we will use this later to check if the email exists).
    private final AuthService authService;

    // 3. The "remote control" that lets us swap to a different screen (like the menu).
    private final Navigator nav;

    /**
     * CONSTRUCTOR — sets everything up and attaches actions to the buttons.
     *
     * This runs exactly once when the Forgot Password screen is first created.
     * Here, we tell the buttons what to do when the user clicks them.
     */
    public ForgotPassController(ForgotPassView view, AuthService authService, Navigator nav) {
        this.view = view;
        this.authService = authService;
        this.nav = nav;

        // When the "RESET" (or Confirm) button is clicked, run the handleReset() method below.
        view.getConfirmButton().setOnAction(e -> handleReset());
        
        // When the "X" (back) button is clicked, tell the Navigator to go back to the main menu.
        view.getBackButton().setOnAction(e -> nav.goToMenu());
    }

    /**
     * HANDLE RESET — the logic that runs when the user clicks "RESET".
     *
     * The steps in plain English:
     *   1. Grab the text the user typed into the email box.
     *   2. If they didn't type anything, show an error message on the screen and stop.
     *   3. If they did type something, pretend to send an email and show a success message.
     */
    private void handleReset() {
        // Grab the email and use .trim() to remove any accidental spaces at the start or end.
        String email = view.getEmailField().getText().trim();

        // VALIDATION: Did they actually type anything?
        if (email.isEmpty()) {
            view.showMessage("Please enter your email.");
            return; // Stop here. Don't do the rest of the method.
        }
        
        // VERIFY: Does this email exist in the temporary database?
        if (!authService.emailExists(email)) {
            view.showMessage("Email not found in our system.");
            return;
        }

        // TODO: In the future, this is where we will check the AuthService database
        // to see if the email exists, and then open the "New Password" screen.
        
        // Success! Pass the verified email to the New Password screen
        nav.goToNewPass(email);

        // For now, we just show a generic success message. 
        // (We use a generic message so hackers can't guess which emails are registered).
        //view.showMessage("If this email exists, a reset link was sent.");
    }
    
    
}