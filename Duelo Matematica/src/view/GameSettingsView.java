package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * GAME SETTINGS VIEW — The Settings Popup Window.
 * 
 * WHAT DOES THIS CLASS DO?
 * Unlike the Login or Game screens (which swap out inside the main window), 
 * this class creates a completely separate, smaller window that floats on 
 * top of the game. It is a "Modal" window, which means it acts like a 
 * locked door—the player cannot click anything on the main game screen 
 * until they close this settings popup.
 * 
 * It also has a special math trick to perfectly center itself right in 
 * the middle of the main game window.
 */
public class GameSettingsView {

    // The actual physical popup window.
    private final Stage stage;
    
    // The "X" button in the top right corner.
    private final Button backButton;

    /**
     * CONSTRUCTOR — Builds the popup window.
     * 
     * @param owner The main game window. We need this to tie the popup 
     *              to it so it centers properly.
     */
    public GameSettingsView(Stage owner) {
        
        // 1. Create a brand new, separate window (Stage).
        stage = new Stage();
        
        // 2. Make it "Modal". This means the main game window is locked 
        // in the background until this popup is closed. 
        // (APPLICATION_MODAL blocks the whole app).
        stage.initModality(Modality.APPLICATION_MODAL);
        
        // 3. Set the "Owner". This tells the popup: "You belong to the main 
        // window." It ensures the popup always stays on top of the game 
        // and doesn't get lost behind it.
        stage.initOwner(owner);
        
        stage.setTitle("Game Settings");
        stage.setResizable(false); // Stop the user from stretching the window

        // ==========================================
        // BUILDING THE VISUALS
        // ==========================================
        
        Label titleLabel = new Label("GAME SETTINGS");
        titleLabel.setStyle("-fx-font-size: 16px;");

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );

        // Put the title in the center and the "X" on the right.
        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // A temporary text since we haven't added real settings yet.
        Label placeholder = new Label("Settings coming soon…");
        placeholder.setStyle("-fx-font-size: 13px;");

        // Stack everything vertically inside a VBox.
        VBox root = new VBox(20, headerBox, placeholder);
        root.setPadding(new Insets(15, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);
        
        // Keep the window at a nice, small size.
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle(
            "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
            "-fx-border-width: 1px;"
        );

        // Attach the layout to the window and set the size (300x200 pixels).
        stage.setScene(new Scene(root, 300, 200));

        // ============================================================
        // THE CENTERING MATH TRICK
        // ============================================================
        // Why do we use 'setOnShown'? 
        // Because if we try to calculate the center right now, the popup window 
        // hasn't actually been drawn yet, so its width and height are 0. 
        // 'setOnShown' waits until the exact millisecond the window appears on 
        // screen to do the math.
        
        stage.setOnShown(event -> {
            if (owner != null) {
                // Formula for finding the center:
                // 1. Take the main window's starting X position.
                // 2. Add half of the main window's width (to find its middle).
                // 3. Subtract half of the popup's width (so the popup's middle 
                //    aligns perfectly with the main window's middle).
                double centerX = owner.getX() + (owner.getWidth() / 2) - (stage.getWidth() / 2);
                
                // Do the exact same math for the Y (vertical) position.
                double centerY = owner.getY() + (owner.getHeight() / 2) - (stage.getHeight() / 2);
                
                // Move the popup to the calculated center spot!
                stage.setX(centerX);
                stage.setY(centerY);
            }
        });
    }

    // ============================================================
    // PUBLIC METHODS for the Controller
    // ============================================================

    /** Returns the "X" button so the controller can attach a close action. */
    public Button getBackButton() { return backButton; }
    
    /** 
     * SHOW — Opens the popup window.
     * Notice it uses 'showAndWait()' instead of just 'show()'. 
     * This tells the app to pause the code here until the user closes the 
     * popup, ensuring they can't accidentally click things behind it.
     */
    public void show()            { stage.showAndWait(); }
    
    /** CLOSE — Shuts the popup window down. */
    public void close()           { stage.close(); }
}