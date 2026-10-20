package controller;

import model.AuthService;
import model.Session;
import view.*;

/**
 * MAIN MENU CONTROLLER — the "traffic cop" for the app.
 *
 * Its jobs:
 *   1. Wire the three home-menu buttons to the right navigation method.
 *   2. Implement Navigator so sub-controllers can ask to leave their page.
 *   3. Build each card + its own controller on demand, then ask the view
 *      to display the new card.
 *
 * It's the only class that touches the MainMenuView directly. Everything
 * else talks to it through the Navigator interface.
 */
public class MainMenuController implements Navigator {

    private final MainMenuView view;         // the window shell (owner of the Stage)
    private final AuthService authService;   // shared account manager

    /**
     * Hooks up the three home buttons. Called once from App on startup.
     */
    public MainMenuController(MainMenuView view, AuthService authService) {
        this.view = view;
        this.authService = authService;

        view.getLoginButton()   .setOnAction(e -> goToLogin());
        view.getRegisterButton().setOnAction(e -> goToRegister());
        view.getSettingsButton().setOnAction(e -> openGameSettings());   // ← popup, not card swap
    }
    
    // ============================================================
    // Card-swap navigation (Navigator implementation)
    // ============================================================

    /**
     * Back to the home menu.
     * Also refreshes the login button text so, after a successful login,
     * the menu shows "Logged in: <username>".
     */
    @Override
    public void goToMenu() {
        view.showMenu();
        if (Session.isLoggedIn()) {
            view.getLoginButton().setText(
                "Logged in: " + Session.getCurrentUser().getUsername()
            );
        }
    }

    /**
     * Build the login card + its controller, then swap it into the window.
     * The 'this' argument is the Navigator — the login controller uses it
     * to return to the menu or jump to forgot-password.
     */
    @Override
    public void goToLogin() {
        LoginView v = new LoginView();
        new LoginController(v, authService, this);
        view.setContent(v.getRoot());
    }

    /** Same pattern as goToLogin, for the register card. */
    @Override
    public void goToRegister() {
        RegisterView v = new RegisterView();
        new RegisterController(v, authService, this);
        view.setContent(v.getRoot());
    }

    /** Same pattern, for the forgot-password card. */
    @Override
    public void goToForgotPassword() {
        ForgotPassView v = new ForgotPassView();
        new ForgotPassController(v, authService, this);
        view.setContent(v.getRoot());
    }

    // ============================================================
    // Popup navigation (not part of Navigator)
    // ============================================================

    /**
     * Open the Game Settings popup.
     *
     * Sequence in plain English:
     *   1. Build the popup view, telling it who its owner (main window) is.
     *   2. Wire the "X" button by constructing the controller.
     *   3. show() → shows the popup modally and BLOCKS here until the user
     *      closes it. When they do, execution continues past this line
     *      (with nothing else to do — the popup has already closed itself).
     *
     * Because this runs from the settings button's click handler, the
     * main window stays visible underneath; the modal modifier simply
     * makes it unclickable until the popup goes away. That's the standard
     * JavaFX pattern for dialogs.
     */
    private void openGameSettings() {
        GameSettingsView v = new GameSettingsView(view.getStage());
        new GameSettingsController(v);
        v.show();          // blocks here until popup closes
    }
}