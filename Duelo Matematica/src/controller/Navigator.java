package controller;

public interface Navigator {
    void goToMenu();
    void goToLogin();
    void goToRegister();
    void goToForgotPassword();
    
    // UPDATED: Now takes the email to pass to the next screen
    void goToNewPass(String email); 
    
    void goToGameMainMenu();
    void goToGameStartMenu();
    void goToUserProfile(); 
    void goToTopicSelection();
    void goToResume();
    void goToGame(String category);
    void resumeGame();
    void goTo1v1Game();
    void goToSettings(); 
    void closePopup(); 
}