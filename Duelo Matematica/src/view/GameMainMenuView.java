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
 * It draws the "MAIN MENU" title, the Start/Credits/Back buttons, and 
 * the Gear (settings) button in the bottom-left corner. 
 * 
 * It is now an INLINE view, meaning it returns a Node (getRoot()) so the 
 * MainMenuController can swap it into the main window. It does not know 
 * what the buttons do—that is the Controller's job.
 */
public class GameMainMenuView {

    private final StackPane root;
    private final Label headerLabel;
    private final Button btnStart;
    private final Button btnCredits;
    private final Button btnBack;
    private final Button gearButton;

    public GameMainMenuView(String titleText) {
        // 1. Build the Header
        headerLabel = new Label(titleText);
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setStyle(
            "-fx-font-size: 26px; -fx-font-weight: bold;" +
            "-fx-border-color: #222222; -fx-border-width: 3px;" +
            "-fx-padding: 12px 60px; -fx-background-color: #f4f4f4;" +
            "-fx-alignment: center; -fx-text-alignment: center;"
        );

        // 2. Build the Main Buttons
        btnStart   = createBorderedButton("START");
        btnCredits = createBorderedButton("CREDITS");
        btnBack    = createBorderedButton("BACK");

        VBox buttonBox = new VBox(15, btnStart, btnCredits, btnBack);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setFillWidth(true);

        // 3. Stack the Header and Buttons in the center
        VBox centerContent = new VBox(30, headerLabel, buttonBox);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setFillWidth(false);

        // 4. Build the Gear Button (bottom-left)
        gearButton = createGearButton();

        // 5. Assemble the Root Layout using a StackPane
        root = new StackPane();
        root.setStyle("-fx-background-color: #ffffff;");
        root.getChildren().addAll(centerContent, gearButton);
        
        // Push the gear button to the bottom-left corner with some margin
        StackPane.setAlignment(gearButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(gearButton, new Insets(20));
    }

    // --- UI HELPER METHODS ---
    
    private Button createBorderedButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(55);
        button.setMaxHeight(55);
        button.setStyle(
            "-fx-font-size: 18px; -fx-font-weight: bold;" +
            "-fx-border-color: #000000; -fx-border-width: 3px;" +
            "-fx-padding: 10px 30px; -fx-background-color: #f9f9f9; -fx-cursor: hand;"
        );
        return button;
    }

    private Button createGearButton() {
        Button gearBtn = new Button("\u2699"); // Unicode for gear icon
        gearBtn.setPrefSize(48, 48);
        gearBtn.setStyle(
            "-fx-font-size: 24px; -fx-font-weight: bold;" +
            "-fx-border-color: #000000; -fx-border-width: 2px;" +
            "-fx-border-radius: 50%; -fx-background-radius: 50%;" +
            "-fx-background-color: #f9f9f9; -fx-cursor: hand;"
        );
        return gearBtn;
    }

    // --- GETTERS for the Controller ---
    public Node getRoot()             { return root; }
    public Button getStartButton()    { return btnStart; }
    public Button getCreditsButton()  { return btnCredits; }
    public Button getBackButton()     { return btnBack; }
    public Button getGearButton()     { return gearButton; }
}