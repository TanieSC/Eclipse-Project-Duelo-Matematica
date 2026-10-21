package controller;

import model.AuthService;
import view.NewPassView;

public class NewPassController {
    private final NewPassView view;
    private final AuthService authService;
    private final String email; // Store the email so we can update the right user
    private final Navigator nav;

    public NewPassController(NewPassView view, AuthService authService, String email, Navigator nav) {
        this.view = view;
        this.authService = authService;
        this.email = email;
        this.nav = nav;

        view.getConfirmButton().setOnAction(e -> handleReset());
        view.getBackButton().setOnAction(e -> nav.goToLogin()); 
    }

    private void handleReset() {
        String pass = view.getNewPassField().getText();
        String confirm = view.getConfirmPassField().getText();

        if (pass.isEmpty() || confirm.isEmpty()) {
            view.showMessage("Please fill in all fields.");
            return;
        }
        if (!pass.equals(confirm)) {
            view.showMessage("Passwords do not match.");
            return;
        }

        // Update the password in the database!
        boolean success = authService.updatePassword(email, pass);
        
        if (success) {
            // Success! Send them to Login to try their new password.
            nav.goToLogin(); 
        } else {
            view.showMessage("Error updating password. Try again.");
        }
    }
}