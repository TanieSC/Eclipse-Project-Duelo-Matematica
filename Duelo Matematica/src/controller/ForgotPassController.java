package controller;

import model.AuthService;
import view.ForgotPassView;

/**
 * FORGOT PASSWORD CONTROLLER — the "brain" behind the reset card.
 *
 * For now it only validates that the email field isn't empty and shows
 * a placeholder confirmation message. When the NewPass flow is built,
 * the TODO branch below will route the user to it.
 *
 * Keeping this as a controller (rather than stuffing logic into the
 * view) means the real reset logic will slot in here without touching
 * ForgotPassView at all.
 */
public class ForgotPassController {

    private final ForgotPassView view;
    private final AuthService authService;
    private final Navigator nav;

    public ForgotPassController(ForgotPassView view, AuthService authService, Navigator nav) {
        this.view = view;
        this.authService = authService;
        this.nav = nav;

        view.getConfirmButton().setOnAction(e -> handleReset());
        view.getBackButton()   .setOnAction(e -> nav.goToMenu());
    }

    private void handleReset() {
        String email = view.getEmailField().getText().trim();
        if (email.isEmpty()) {
            view.showMessage("Please enter your email.");
            return;
        }
        // TODO: when NewPass is implemented, call nav.goToNewPass(email) here.
        view.showMessage("If this email exists, a reset link was sent.");
    }
}