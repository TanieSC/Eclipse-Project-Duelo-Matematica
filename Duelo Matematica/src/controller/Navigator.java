package controller;

/**
 * NAVIGATOR — a tiny "remote control" for switching pages.
 *
 * Every destination that replaces the main window's content is listed
 * here
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
 * 
 * There's no other route into the game — no play button on the menu.
 *   goToTopicSelection()  → called by LoginController on success
 *   goToGame(String)      → called by TopicController with the category
 *   
 *   goToResume()  — show the "Resume or New Category?" card
 *   resumeGame()  — jump straight back into the paused round
 *
 * Both are only meaningful when Session.hasSavedGame() is true; callers
 * are expected to check that first. (The implementations handle the
 * "no saved game" case by redirecting to the topic picker, so a stray
 * call can't crash the app.)
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

    /** Show the topic card. */
    void goToTopicSelection();
    
    void goToResume();
    
    /** Show the game card. */
    void goToGame(String category);
    
    void resumeGame();
}