package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * GAME MAIN MENU VIEW — The visual layout for the secondary game menu.
 * 
 * WHAT DOES THIS CLASS DO?
 * Think of this class as a "dumb artist". Its ONLY job is to draw the 
 * visuals for this specific screen: the "MAIN MENU" title, the START, 
 * CREDITS, and BACK buttons, and the Gear (settings) button in the corner.
 * 
 * It is an INLINE view. This means it doesn't create a separate window. 
 * Instead, it builds all the visuals into one big container and hands it 
 * over to the main app window using getRoot(). 
 * 
 * It has NO idea what happens when you click a button. The 
 * GameMainMenuController handles all the actual actions.
 */
public class GameMainMenuView {

    // --- THE BUILDING BLOCKS ---
    // We save these as class variables so the Controller can reach in 
    // and grab them later to attach click events.

    // The main container. We use a StackPane so we can put the main menu 
    // in the center and the gear icon in the corner, layered on top of each other.
    private final StackPane root;
    
    // The big title at the top.
    private final Label headerLabel;
    
    // The three main buttons.
    private final Button btnStart;
    private final Button btnCredits;
    private final Button btnBack;
    
    // The gear icon in the bottom left.
    private final Button gearButton;

    /**
     * CONSTRUCTOR — Runs once when the Game Main Menu is created.
     * 
     * This is where we build and arrange all the visual pieces.
     * 
     * @param titleText The text for the header (e.g., "MAIN MENU"). We pass 
     *                  this in so the view isn't hardcoded to just one title.
     */
    public GameMainMenuView(String titleText) {
        
        // ==========================================
        // 1. THE HEADER
        // ==========================================
        headerLabel = new Label(titleText);
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setStyle(
            "-fx-font-size: 26px; -fx-font-weight: bold;" +
            "-fx-border-color: #222222; -fx-border-width: 3px;" +
            "-fx-padding: 12px 60px; -fx-background-color: #f4f4f4;" +
            "-fx-alignment: center; -fx-text-alignment: center;"
        );

        // ==========================================
        // 2. THE MAIN BUTTONS
        // ==========================================
        // We use our helper method below to create buttons that all look identical.
        btnStart   = createBorderedButton("START");
        btnCredits = createBorderedButton("CREDITS");
        btnBack    = createBorderedButton("BACK");

        // Stack the three buttons vertically in a VBox (Vertical Box).
        VBox buttonBox = new VBox(15, btnStart, btnCredits, btnBack);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setFillWidth(true);

        // ==========================================
        // 3. GROUPING THE CENTER CONTENT
        // ==========================================
        // Stack the header and the buttons vertically to make the main block.
        VBox centerContent = new VBox(30, headerLabel, buttonBox);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setFillWidth(false);

        // ==========================================
        // 4. THE GEAR BUTTON (SETTINGS)
        // ==========================================
        // We use another helper method to make the round gear icon.
        gearButton = createGearButton();

        // ==========================================
        // 5. ASSEMBLING THE ROOT LAYOUT
        // ==========================================
        // We use a StackPane because it lets us layer items on top of each other.
        // We put the center menu down first, then the gear button on top.
        root = new StackPane();
        root.setStyle("-fx-background-color: #ffffff;");
        root.getChildren().addAll(centerContent, gearButton);
        
        // Push the gear button to the bottom-left corner.
        StackPane.setAlignment(gearButton, Pos.BOTTOM_LEFT);
        // Give it a little breathing room from the edges of the screen.
        StackPane.setMargin(gearButton, new Insets(20));
    }

    // ==========================================
    // UI HELPER METHODS
    // ==========================================
    
    /**
     * HELPER — Builds a uniform button.
     * We write this once here so we don't have to copy-paste the styling 
     * code three times for the three main buttons.
     */
    private Button createBorderedButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE); // Stretch to fill available width
        button.setMinHeight(55);              // Make it nice and tall
        button.setMaxHeight(55);
        button.setStyle(
            "-fx-font-size: 18px; -fx-font-weight: bold;" +
            "-fx-border-color: #000000; -fx-border-width: 3px;" +
            "-fx-padding: 10px 30px; -fx-background-color: #f9f9f9; -fx-cursor: hand;"
        );
        return button;
    }

    /**
     * HELPER — Builds the circular Gear button for the settings.
     * We use the special unicode character "\u2699" to display the gear symbol.
     */
    private Button createGearButton() {
        Button gearBtn = new Button("\u2699"); 
        gearBtn.setPrefSize(48, 48); // Make it a perfect 48x48 square
        gearBtn.setStyle(
            "-fx-font-size: 24px; -fx-font-weight: bold;" +
            "-fx-border-color: #000000; -fx-border-width: 2px;" +
            "-fx-border-radius: 50%; -fx-background-radius: 50%;" + // Makes it a circle
            "-fx-background-color: #f9f9f9; -fx-cursor: hand;"
        );
        return gearBtn;
    }

    // ============================================================
    // GETTERS — The "Control Panel" for the Controller
    // ============================================================
    // These methods let the GameMainMenuController grab the pieces it needs 
    // to attach click events.

    /** Returns the whole layout so the main window can display it. */
    public Node getRoot()             { return root; }
    
    /** Lets the controller attach a click action to the START button. */
    public Button getStartButton()    { return btnStart; }
    
    /** Lets the controller attach a click action to the CREDITS button. */
    public Button getCreditsButton()  { return btnCredits; }
    
    /** Lets the controller attach a click action to the BACK button. */
    public Button getBackButton()     { return btnBack; }
    
    /** Lets the controller attach a click action to the GEAR button. */
    public Button getGearButton()     { return gearButton; }
}