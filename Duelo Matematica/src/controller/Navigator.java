package controller;

/**
 * NAVIGATOR — the "Remote Control" interface.
 * 
 * We added `goToGameMainMenu()` so that controllers can ask to go to the 
 * new secondary menu after a successful login.
 */
public interface Navigator {
    void goToMenu();
    void goToLogin();
    void goToRegister();
    void goToForgotPassword();
    
    // NEW: The destination for a successful login
    void goToGameMainMenu(); 
    
    void goToTopicSelection();
    void goToResume();
    void goToGame(String category);
    void resumeGame();
}