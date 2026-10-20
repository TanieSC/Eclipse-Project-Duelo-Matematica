package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.GameService;

/**
 * RESUME VIEW — The visual layout for the "Resume Game?" screen.
 *
 * WHAT DOES THIS CLASS DO?
 * When a player quits a game halfway through, the app saves their progress. 
 * The next time they log in, they see this screen. 
 * 
 * This class's ONLY job is to draw the visual prompt. It shows the user a 
 * summary of their saved game (e.g., "Calculus | Question 5/10 | Score: 3") 
 * and presents them with two choices:
 *   1. Resume the game exactly where they left off.
 *   2. Abandon the save and pick a new category.
 * 
 * It does NOT decide what happens when the buttons are clicked. It just 
 * builds the UI and waits for the ResumeController to wire up the actions.
 */
public class ResumeView {

    // --- THE BUILDING BLOCKS ---

    // The main container holding all the visual elements.
    private final VBox root;
    
    // The button to jump back into the saved game.
    private final Button resumeButton;
    
    // The button to abandon the save and go to the category picker.
    private final Button newCategoryButton;
    
    // The "X" button in the top right to cancel.
    private final Button backButton;

    /**
     * CONSTRUCTOR — Builds the card using the saved game's data.
     * 
     * @param savedGame The paused game data passed in from the Controller.
     *                  We use this to show the summary text so the user 
     *                  knows exactly what they are resuming.
     */
    public ResumeView(GameService savedGame) {
        
        // ==========================================
        // 1. THE HEADER (Title + Close Button)
        // ==========================================
        Label titleLabel = new Label("RESUME GAME?");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );

        // BorderPane puts the title in the center and the X on the right edge.
        BorderPane header = new BorderPane();
        header.setCenter(titleLabel);
        header.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. THE SUMMARY TEXT
        // ==========================================
        // We pull the data from the savedGame to build a sentence like: 
        // "Calculus | Question 5/10 | Score: 3"
        // Note: getCurrentIndex() starts at 0, so we add 1 to show the human-readable number.
        String summary = savedGame.getCategory()
                       + "  |  Question " + (savedGame.getCurrentIndex() + 1)
                       + "/" + savedGame.getTotal()
                       + "  |  Score: " + savedGame.getScore();
                       
        Label summaryLabel = new Label(summary);
        summaryLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");
        summaryLabel.setWrapText(true); // If the text is too long, wrap to the next line.

        // ==========================================
        // 3. THE ACTION BUTTONS
        // ==========================================
        // We use our custom 'bigButton' helper method below to create 
        // large, easy-to-click buttons.
        resumeButton      = bigButton("RESUME GAME",      "Continue where you left off");
        newCategoryButton = bigButton("CHOOSE NEW CATEGORY", "Start a fresh round");

        // Stack the two buttons vertically with a bit of space between them.
        VBox buttons = new VBox(12, resumeButton, newCategoryButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setFillWidth(true);

        // ==========================================
        // 4. ASSEMBLING THE CARD
        // ==========================================
        // Stack everything vertically inside the main 'root' VBox.
        root = new VBox(20, header, summaryLabel, buttons);
        
        root.setPadding(new Insets(20, 30, 25, 30));
        root.setAlignment(Pos.TOP_CENTER);
        
        // This keeps the card at a nice, compact size instead of stretching 
        // to fill the entire window.
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        
        // Style it to look like a floating card.
        root.setStyle(
            "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
            "-fx-border-width: 1px;"
        );
    }

    /**
     * HELPER — Creates a wide button with a title and a smaller subtitle.
     * 
     * This is just to keep the code clean. Instead of writing the same 
     * 8 lines of styling for both buttons, we wrote it once here.
     */
    private Button bigButton(String title, String subtitle) {
        // The "\n" character breaks the text into two lines.
        Button b = new Button(title + "\n" + subtitle);
        b.setMaxWidth(Double.MAX_VALUE); // Stretch to fill the width
        b.setMinHeight(60);              // Make it nice and tall
        b.setStyle(
            "-fx-font-size: 14px; -fx-font-weight: bold;" +
            "-fx-text-alignment: center;" +
            "-fx-border-color: #000; -fx-border-width: 2px;" +
            "-fx-background-color: #f9f9f9; -fx-cursor: hand;"
        );
        return b;
    }

    // ============================================================
    // GETTERS — The "Control Panel" for the ResumeController
    // ============================================================

    /** Returns the whole card so the main window can display it. */
    public Node   getRoot()              { return root; }
    
    /** Lets the controller attach a click action to the "RESUME GAME" button. */
    public Button getResumeButton()      { return resumeButton; }
    
    /** Lets the controller attach a click action to the "CHOOSE NEW CATEGORY" button. */
    public Button getNewCategoryButton() { return newCategoryButton; }
    
    /** Lets the controller attach a click action to the "X" button. */
    public Button getBackButton()        { return backButton; }
}