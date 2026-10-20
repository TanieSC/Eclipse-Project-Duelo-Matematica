package controller;

import model.AuthService;
import view.RegisterView;

/**
 * REGISTER CONTROLLER — the "brain" behind the create-account card.
 *
 * Responsibilities:
 *   - CONFIRM pressed → validate fields, ask authService.register().
 *                       On success: go back to the menu (no auto-login).
 *                       On failure: show an error message.
 *   - X pressed       → cancel and go back to the menu.
 *
 * It does NOT log the new user in — that's a deliberate UX choice so the
 * player sees the login flow once. If you wanted auto-login, you'd call
 * authService.login(...) here and Session.setCurrentUser(...).
 */
public class RegisterController {

    private final RegisterView view;        // the card
    private final AuthService authService;  // shared account manager
    private final Navigator nav;            // remote control for page swaps

    public RegisterController(RegisterView view, AuthService authService, Navigator nav) {
        this.view = view;
        this.authService = authService;
        this.nav = nav;

        view.getConfirmButton().setOnAction(e -> handleRegister());
        view.getBackButton()   .setOnAction(e -> nav.goToMenu());
    }

    private void handleRegister() {
        String email    = view.getEmailField().getText().trim();
        String username = view.getUsernameField().getText().trim();
        String password = view.getPasswordField().getText();

        // 1) Reject empty fields.
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            view.showMessage("Please fill in all fields.");
            return;
        }

        // 2) Ask the model to create the account. It refuses duplicates.
        if (!authService.register(email, username, password)) {
            view.showMessage("Email already registered.");
            return;
        }

        // 3) Success — return to the menu.
        nav.goToMenu();
    }
}