package controller;

import network.GameConnection;

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
    void goTo1v1TimeSelection();
    void goTo1v1Matchmaking(int durationSeconds);
    void goTo1v1Battle(GameConnection connection, int durationSeconds);
}