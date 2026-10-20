package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * TOPIC VIEW — The visual layout for the category selection screen.
 *
 * WHAT DOES THIS CLASS DO?
 * This screen appears right after a player successfully logs in (and has 
 * no saved game to resume). It acts like a menu at a restaurant, asking 
 * the player: "What kind of math do you want to practice today?"
 * 
 * It draws four large buttons (Calculus, Statistics, Symbolic Logic, and 
 * a random mix of All Topics) plus an "X" button to cancel.
 * 
 * Just like the other screens, it is a "dumb" artist. It draws the buttons 
 * but doesn't know what they do. The TopicController will come along and 
 * connect the buttons to their actions.
 */
public class TopicView {

    // --- THE BUILDING BLOCKS ---

    // The main container holding everything (like a vertical stack of blocks).
    private final VBox root;
    
    // The four category buttons.
    private final Button calculusButton;
    private final Button statisticsButton;
    private final Button logicButton;
    private final Button allButton;
    
    // The "X" button in the top right corner to cancel and go back.
    private final Button backButton;

    /**
     * CONSTRUCTOR — Runs once when the Category screen is created.
     * 
     * This is where we build and arrange all the visual pieces.
     */
    public TopicView() {
        
        // ==========================================
        // 1. THE HEADER (Title + Close Button)
        // ==========================================
        Label titleLabel = new Label("CHOOSE A CATEGORY");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );

        // A BorderPane puts the title in the center and the X on the right edge.
        BorderPane header = new BorderPane();
        header.setCenter(titleLabel);
        header.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. THE CATEGORY BUTTONS
        // ==========================================
        // We use our custom 'topicButton' helper method below to create 
        // large buttons with a main title and a smaller description.
        calculusButton   = topicButton("CALCULUS",       "Calculus 1, 2 and 3");
        statisticsButton = topicButton("STATISTICS",     "Probability and inference");
        logicButton      = topicButton("SYMBOLIC LOGIC", "Propositional and predicate");
        allButton        = topicButton("ALL TOPICS",     "Random mix from every subject");

        // Stack the four buttons vertically with a bit of space between them.
        VBox topics = new VBox(12, calculusButton, statisticsButton, logicButton, allButton);
        topics.setAlignment(Pos.CENTER);
        topics.setFillWidth(true); // Make them all stretch to the same width

        // ==========================================
        // 3. ASSEMBLING THE CARD
        // ==========================================
        // Stack the header and the button list vertically inside our main 'root' VBox.
        root = new VBox(20, header, topics);
        
        root.setPadding(new Insets(20, 30, 25, 30));
        root.setAlignment(Pos.TOP_CENTER);
        
        // Keep the card at a nice, compact size.
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        
        // Give it a grey background and a border to look like a floating card.
        root.setStyle(
            "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
            "-fx-border-width: 1px;"
        );
    }

    /**
     * HELPER — Creates a wide button with a title and subtitle.
     * 
     * This keeps the code clean. Instead of writing the same styling code 
     * four times, we wrote it once here. 
     * 
     * The "\n" in the button text forces the subtitle to appear on a new line.
     */
    private Button topicButton(String title, String subtitle) {
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
    // GETTERS — The "Control Panel" for the TopicController
    // ============================================================
    // These methods let the TopicController grab the pieces it needs 
    // to attach click events to the buttons.

    /** Returns the whole card so the main window can display it. */
    public Node   getRoot()             { return root; }
    
    /** Lets the controller attach a click action to the Calculus button. */
    public Button getCalculusButton()   { return calculusButton; }
    
    /** Lets the controller attach a click action to the Statistics button. */
    public Button getStatisticsButton() { return statisticsButton; }
    
    /** Lets the controller attach a click action to the Symbolic Logic button. */
    public Button getLogicButton()      { return logicButton; }
    
    /** Lets the controller attach a click action to the All Topics button. */
    public Button getAllButton()        { return allButton; }
    
    /** Lets the controller attach a click action to the X (cancel) button. */
    public Button getBackButton()       { return backButton; }
}