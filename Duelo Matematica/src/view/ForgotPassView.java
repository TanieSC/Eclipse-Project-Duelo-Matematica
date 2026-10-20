package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * FORGOT PASSWORD VIEW — the visual layout for the password reset screen.
 * 
 * WHAT DOES THIS CLASS DO?
 * This is a "dumb" screen. Its ONLY job is to draw the visual elements 
 * (text boxes, buttons, labels) and place them neatly on the screen. 
 * 
 * It does NOT know what happens when you click the "RESET" button, and it 
 * does NOT know how to check if an email exists. It simply builds the UI 
 * and waits for the ForgotPassController to come along and wire up the actions.
 */
public class ForgotPassView {

    // --- THE BUILDING BLOCKS ---
    // We save these as class variables so the Controller can access them later.

    // The main container that holds everything (like a vertical stack).
    private final VBox root;                 
    
    // The text box where the user types their email.
    private final TextField emailField;      
    
    // The "RESET" button.
    private final Button confirmButton;      
    
    // The "X" button in the top right corner to cancel.
    private final Button backButton;         
    
    // A hidden text label that we can use to show red error messages.
    private final Label messageLabel;        

    /**
     * CONSTRUCTOR — runs once when the screen is created.
     * 
     * This is where we actually build the visual layout.
     */
    public ForgotPassView() {
        
        // ==========================================
        // 1. THE HEADER (Title + Close Button)
        // ==========================================
        Label titleLabel = new Label("FORGOT PASSWORD");
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );

        // We use a BorderPane to put the title in the center and the "X" on the right.
        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. THE INPUT FORM (Email)
        // ==========================================
        Label emailLabel = new Label("EMAIL");
        emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        
        emailField = new TextField();
        emailField.setPrefWidth(140);
        emailField.setStyle(
            "-fx-background-color: #666666; -fx-text-inner-color: white;" +
            "-fx-background-radius: 0;"
        );

        // A GridPane is like a mini-spreadsheet. We put the label in column 0 
        // and the text box in column 1.
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.add(emailLabel, 0, 0);
        formGrid.add(emailField, 1, 0);

        // ==========================================
        // 3. THE FEEDBACK & ACTION (Error text + RESET Button)
        // ==========================================
        messageLabel = new Label(); // Starts empty
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        confirmButton = new Button("RESET");
        confirmButton.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        confirmButton.setPrefSize(130, 35);
        confirmButton.setStyle(
            "-fx-background-color: #999999; -fx-text-fill: black;" +
            "-fx-background-radius: 0; -fx-cursor: hand;"
        );

        // ==========================================
        // 4. ASSEMBLING THE CARD
        // ==========================================
        // We stack everything vertically in our main 'root' VBox.
        // The number '15' is the vertical spacing between each element.
        root = new VBox(15, headerBox, formGrid, messageLabel, confirmButton);
        
        root.setPadding(new Insets(15, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);
        
        // This keeps the card at a nice, small size instead of stretching 
        // to fill the entire giant window.
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        
        // The styling gives it a grey background and a border to look like a card.
        root.setStyle(
            "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
            "-fx-border-width: 1px;"
        );
    }

    // ============================================================
    // GETTERS & HELPERS — The "Control Panel" for the Controller
    // ============================================================
    // These methods let the ForgotPassController grab the pieces it needs 
    // to attach click events and read what the user typed.

    /** Returns the whole card so the app can put it on the screen. */
    public Node getRoot()               { return root; }
    
    /** Lets the controller read what the user typed in the email box. */
    public TextField getEmailField()    { return emailField; }
    
    /** Lets the controller attach a click action to the RESET button. */
    public Button getConfirmButton()    { return confirmButton; }
    
    /** Lets the controller attach a click action to the X button. */
    public Button getBackButton()       { return backButton; }
    
    /** Allows the controller to show red error text on the screen. */
    public void showMessage(String msg) { messageLabel.setText(msg); }
}