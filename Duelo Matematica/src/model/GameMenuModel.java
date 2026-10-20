package model;

/**
 * GAME MENU MODEL — The Data for the Game Main Menu.
 * 
 * WHAT DOES THIS CLASS DO?
 * It holds any information this specific menu might need. 
 * Currently, it's a placeholder because a menu doesn't have much data, 
 * but it's good practice to have one in case you later want to add 
 * things like "Menu Title" or "List of Credits" dynamically.
 */
public class GameMenuModel {
    
    // The title text displayed at the top of the menu.
    private final String title;

    public GameMenuModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}