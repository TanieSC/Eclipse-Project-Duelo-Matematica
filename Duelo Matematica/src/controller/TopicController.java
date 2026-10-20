package controller;

import view.TopicView;

/**
 * TOPIC CONTROLLER — the "brain" behind the category selection screen.
 *
 * WHAT DOES THIS CLASS DO?
 * This screen simply asks the player: "Which type of math do you want to practice?"
 * There are no passwords to check or scores to calculate here. 
 * 
 * This controller's only job is to listen for button clicks and tell the 
 * "Remote Control" (Navigator) which category the player chose. 
 * 
 * THE MAGIC STRINGS:
 * We pass specific words to the Navigator (like "Calculus" or "Statistics").
 * The GameService on the next screen will use these exact words to filter 
 * the massive list of 50 questions down to just the ones the player wants.
 * 
 *   "Calculus"        → gets all Calculus 1, 2, and 3 questions
 *   "Statistics"      → gets Statistics questions only
 *   "Symbolic Logic"  → gets Logic questions only
 *   "All"             → gets a random mix of everything
 */
public class TopicController {

    // 1. The visual screen with the four large category buttons.
    private final TopicView view;
    
    // 2. The "Remote Control" used to switch screens and start the game.
    private final Navigator nav;

    /**
     * CONSTRUCTOR — runs once when the category screen is created.
     * 
     * This is where we connect the buttons to their actions. We don't do any 
     * heavy lifting here; we just say "When this button is clicked, tell the 
     * Navigator to do this."
     * 
     * @param view The visual Topic screen.
     * @param nav  The remote control for navigation.
     */
    public TopicController(TopicView view, Navigator nav) {
        this.view = view;
        this.nav = nav;

        // --- WIRING THE BUTTONS ---
        
        // If the player clicks "CALCULUS", tell the Navigator to start a game with "Calculus".
        view.getCalculusButton()  .setOnAction(e -> nav.goToGame("Calculus"));
        
        // If the player clicks "STATISTICS", tell the Navigator to start a game with "Statistics".
        view.getStatisticsButton().setOnAction(e -> nav.goToGame("Statistics"));
        
        // If the player clicks "SYMBOLIC LOGIC", tell the Navigator to start a game with "Symbolic Logic".
        view.getLogicButton()     .setOnAction(e -> nav.goToGame("Symbolic Logic"));
        
        // If the player clicks "ALL TOPICS", tell the Navigator to start a game with "All" (no filter).
        view.getAllButton()       .setOnAction(e -> nav.goToGame("All"));
        
        // If the player clicks the "X" (Back button), tell the Navigator to cancel and return to the main menu.
        view.getBackButton()      .setOnAction(e -> nav.goToMenu());
    }
}
