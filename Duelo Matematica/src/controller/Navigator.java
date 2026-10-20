package controller;

public interface Navigator {
    void goToMenu();
    void goToLogin();
    void goToRegister();
    void goToForgotPassword();
    
    // The new menu flow
    void goToGameMainMenu();   
    void goToGameStartMenu();  
    
    // The actual game flow
    void goToTopicSelection();
    void goToResume();
    void goToGame(String category);
    void resumeGame();
}