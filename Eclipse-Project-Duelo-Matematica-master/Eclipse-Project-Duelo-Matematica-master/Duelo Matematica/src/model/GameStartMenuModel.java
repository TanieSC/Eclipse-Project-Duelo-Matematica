package model;

/**
 * GAME START MENU MODEL — The "Data Container" for the Select Mode screen.
 * 
 * WHAT DOES THIS CLASS DO?
 * In the MVC (Model-View-Controller) pattern, the Model is the "brain" that 
 * holds the raw data. It doesn't know about buttons, colors, or screens. 
 * 
 * This specific model is very simple. It holds a single piece of information: 
 * the title text that should be displayed at the top of the screen 
 * (like "SELECT MODE"). 
 * 
 * WHY PUT THE TITLE HERE INSTEAD OF HARDCODING IT IN THE VIEW?
 * If we hardcoded "SELECT MODE" directly inside the View, we would have 
 * to rewrite the View code just to change the title. By keeping it here, 
 * we can pass in "CHOOSE YOUR BATTLE" or any other title without touching 
 * a single line of the visual layout.
 */
public class GameStartMenuModel {

    // The text for the header title.
    // Notice the word 'final'. This means once the title is set in the 
    // constructor, it can NEVER be changed. It is locked in permanently.
    private final String title;

    /**
     * CONSTRUCTOR — The "Form Printer".
     * 
     * This runs when we want to create a new model. We have to provide 
     * the title text right away.
     * 
     * @param title The text to display at the top of the screen.
     */
    public GameStartMenuModel(String title) {
        this.title = title;
    }

    /**
     * GETTER — The "Peek Hole".
     * 
     * This allows the View (the visual screen) to read the title text 
     * so it can display it. Because there is no "setTitle" method, the 
     * view can look at the title, but it cannot change it.
     * 
     * @return The title text.
     */
    public String getTitle() {
        return title;
    }
}