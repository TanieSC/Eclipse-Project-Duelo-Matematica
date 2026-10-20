package model;

/**
 * GAME START MENU MODEL — The data for the "Select Mode" screen.
 * 
 * WHAT DOES THIS CLASS DO?
 * It holds the title of the screen. Keeping the title here (instead of 
 * hardcoding it in the view) makes the view reusable. If you ever want 
 * the title to be "CHOOSE YOUR BATTLE" instead of "SELECT MODE", you 
 * only change it here.
 */
public class GameStartMenuModel {

    private final String title;

    public GameStartMenuModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
