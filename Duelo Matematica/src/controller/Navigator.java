package controller;

/**
 * NAVIGATOR — a tiny "remote control" for switching pages.
 *
 * Controllers (Login/Register/etc.) don't know how the window is built
 * or that a StackPane exists. They only know this one interface:
 * "if I want to go somewhere, I call one of these methods."
 *
 * MainMenuController implements this interface — it's the object that
 * actually swaps cards inside MainMenuView.
 *
 * Benefit: a future "MobileNavigator" (or a test fake) can implement
 * this same interface with a totally different implementation, and
 * LoginController/etc. will still work unchanged.
 */
public interface Navigator {

    /** Return to the home menu. */
    void goToMenu();

    /** Show the login card. */
    void goToLogin();

    /** Show the register card. */
    void goToRegister();

    /** Show the forgot-password card. */
    void goToForgotPassword();

}