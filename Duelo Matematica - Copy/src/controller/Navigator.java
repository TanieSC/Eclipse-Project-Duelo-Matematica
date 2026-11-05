package controller;

public interface Navigator {
    void goToMenu();
    void goToLogin();
    void goToRegister();
    void goToForgotPassword();
    void goToNewPass(String email); 
    
    void goToGameMainMenu();
    void goToGameStartMenu();
    void goToUserProfile(); 
    void goTo1v1Game();
    void goToSettings(); 
    void closePopup(); 
    void goToCredits();
    void goToLeaderboard();
    void goToStoryLevels();
    void goToStoryScreen(int level);
    void goToStoryBattle(int level);
    void goToConcede(); 
}