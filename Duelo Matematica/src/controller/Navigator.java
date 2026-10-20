package controller;

/**
 * NAVIGATOR — the "Remote Control" interface.
 * 
 * WHAT IS AN INTERFACE?
 * Think of an interface like a TV remote control. It doesn't contain the actual 
 * TV screen or the wiring inside the TV. It just has buttons with labels 
 * (Power, Volume Up, Channel Down). It says "These are the things you can do," 
 * but it leaves the actual work to whoever is holding the remote.
 * 
 * WHY DO WE NEED THIS?
 * Our other controllers (like LoginController and GameController) need to 
 * change the screen. But we don't want them to know how to build every 
 * single screen (that would make them huge and messy). 
 * 
 * Instead, we give them this "Remote Control" (Navigator). They just call 
 * a button like `nav.goToMenu()`. The actual work of building the menu 
 * and swapping the screen is handled by the MainMenuController, which 
 * implements this interface.
 * 
 * RULES TO REMEMBER:
 * - There is no Play button on the menu. The ONLY way to get to the game 
 *   is to successfully log in, which triggers the topic selection.
 * - If a controller asks to resume a game but there is no saved game, 
 *   the MainMenuController will safely redirect them to the topic picker 
 *   so the app doesn't crash.
 */
public interface Navigator {

    /**
     * GO TO MENU — The "Home" button.
     * Takes the user back to the main menu screen.
     */
    void goToMenu();

    /**
     * GO TO LOGIN — The "Sign In" button.
     * Shows the screen where the user can type their email and password.
     */
    void goToLogin();

    /**
     * GO TO REGISTER — The "Create Account" button.
     * Shows the screen where a new user can sign up.
     */
    void goToRegister();

    /**
     * GO TO FORGOT PASSWORD — The "Reset Password" button.
     * Shows the screen where a user can request a password reset.
     */
    void goToForgotPassword();

    /**
     * GO TO TOPIC SELECTION — The "Choose Category" button.
     * Shows the screen where the player picks Calculus, Statistics, etc.
     * This is called after a successful login (if they have no saved game).
     */
    void goToTopicSelection();
    
    /**
     * GO TO RESUME — The "Continue Playing?" button.
     * Shows a screen asking the player if they want to resume their 
     * paused game or start a new one.
     */
    void goToResume();
    
    /**
     * GO TO GAME — Starts a brand new game.
     * 
     * @param category The math subject the user picked (e.g., "Calculus", 
     *                 "Statistics", "Symbolic Logic"). This tells the game 
     *                 which questions to load.
     */
    void goToGame(String category);
    
    /**
     * RESUME GAME — Jumps straight back into a paused game.
     * Skips the main menu and goes directly to the exact question 
     * the player was on before they quit.
     */
    void resumeGame();
}