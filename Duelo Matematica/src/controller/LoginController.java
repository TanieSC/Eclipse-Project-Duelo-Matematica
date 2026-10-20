package controller;

import model.AuthService;
import model.Session;
import model.User;
import view.LoginView;

/**
 * LOGIN CONTROLLER — the "brain" behind the login card.
 *
 * Responsibilities:
 *   - CONFIRM pressed        → validate fields, call authService.login().
 *                              On success: remember the user in Session,
 *                              then navigate back to the menu.
 *                              On failure: show an error message.
 *   - X pressed              → go back to the menu (cancel login).
 *   - "FORGOT PASSWORD?" hit → navigate to the forgot-password card.
 *
 * The view does nothing on its own — every interactive element is wired
 * here.
 */
public class LoginController {

    private final LoginView view;           // the card
    private final AuthService authService;  // shared account manager
    private final Navigator nav;            // remote control for page swaps

    public LoginController(LoginView view, AuthService authService, Navigator nav) {
        this.view = view;
        this.authService = authService;
        this.nav = nav;

        // Wire the three interactive elements.
        view.getConfirmButton()     .setOnAction(e -> handleLogin());
        view.getBackButton()        .setOnAction(e -> nav.goToMenu());
        view.getForgotPasswordLink().setOnAction(e -> nav.goToForgotPassword());
    }

    /**
     * Full login flow. Called when CONFIRM is clicked.
     */
    private void handleLogin() {
        String email    = view.getEmailField().getText().trim();
        String password = view.getPasswordField().getText();

        // 1) Basic validation — reject empty fields.
        if (email.isEmpty() || password.isEmpty()) {
            view.showMessage("Please fill in all fields.");
            return;
        }

        // 2) Ask the model: does this email/password pair match?
        User user = authService.login(email, password);
        if (user == null) {
            view.showMessage("Invalid email or password.");
            return;
        }

        // 3) Success — remember who's logged in, then leave the card.
        Session.setCurrentUser(user);
        nav.goToMenu();
    }
}
