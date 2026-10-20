package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * REGISTER VIEW — The visual layout for the create-account screen.
 *
 * WHAT DOES THIS CLASS DO?
 * Think of this class as a "dumb" artist. Its ONLY job is to draw the 
 * visual elements of the registration card (the text boxes, buttons, 
 * and labels) and arrange them neatly. 
 * 
 * It does NOT know what happens when you click "CONFIRM", and it does NOT 
 * know how to save a new user. It simply builds the UI and waits for the 
 * RegisterController to come along and "wire up" the actions.
 * 
 * This screen looks very similar to the Login screen, but it has an extra 
 * box for a Username and no "Forgot Password" link.
 */
public class RegisterView {

    // --- THE BUILDING BLOCKS ---
    // We save these as class variables so the Controller can reach in 
    // and grab them later.

    // The main container holding everything.
    private final VBox root;                    
    // The text box where the user types their email.
    private final TextField emailField;         
    // The text box where the user types their display name.
    private final TextField usernameField;      
    // The text box where the user types their password (hidden as dots).
    private final PasswordField passwordField;  
    // The "CONFIRM" button to submit the form.
    private final Button confirmButton;         
    // The "X" button in the top right corner to cancel.
    private final Button backButton;            
    // A hidden text label that we can use to show red error messages.
    private final Label messageLabel;           

    /**
     * CONSTRUCTOR — Runs once when the Registration screen is created.
     * 
     * This is where we actually build and arrange all the visual pieces.
     */
    public RegisterView() {
        
        // ==========================================
        // 1. THE HEADER (Title + Close Button)
        // ==========================================
        Label titleLabel = new Label("CREATE ACCOUNT");
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );

        // A BorderPane lets us put the title in the center and the X on the right.
        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. THE FORM (Email, Username, Password)
        // ==========================================
        Label emailLabel = new Label("EMAIL");
        emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        emailField = new TextField();
        emailField.setPrefWidth(140);
        emailField.setStyle(
            "-fx-background-color: #666666; -fx-text-inner-color: white;" +
            "-fx-background-radius: 0;"
        );

        Label usernameLabel = new Label("USERNAME");
        usernameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        usernameField = new TextField();
        usernameField.setPrefWidth(140);
        usernameField.setStyle(
            "-fx-background-color: #666666; -fx-text-inner-color: white;" +
            "-fx-background-radius: 0;"
        );

        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        passwordField = new PasswordField();
        passwordField.setPrefWidth(140);
        passwordField.setStyle(
            "-fx-background-color: #666666; -fx-text-inner-color: white;" +
            "-fx-background-radius: 0;"
        );

        // A GridPane works like an invisible spreadsheet. 
        // We put the labels in the left column (0) and the text boxes in the right column (1).
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15); // Horizontal gap between columns
        formGrid.setVgap(15); // Vertical gap between rows
        formGrid.add(emailLabel, 0, 0);     formGrid.add(emailField, 1, 0);
        formGrid.add(usernameLabel, 0, 1);  formGrid.add(usernameField, 1, 1);
        formGrid.add(passwordLabel, 0, 2);  formGrid.add(passwordField, 1, 2);

        // ==========================================
        // 3. FEEDBACK & ACTION (Error text + CONFIRM Button)
        // ==========================================
        // This label is empty at first, but the Controller will fill it 
        // with red text if the user makes a mistake.
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        confirmButton = new Button("CONFIRM");
        confirmButton.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
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
        
        // Keep the card at a nice, small size instead of stretching 
        // to fill the entire giant window.
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        
        // Give it a grey background and a border to look like a "card".
        root.setStyle(
            "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
            "-fx-border-width: 1px;"
        );
    }

    // ============================================================
    // GETTERS — The "Control Panel" for the RegisterController
    // ============================================================
    // These methods let the RegisterController grab the pieces it needs 
    // to attach click events and read what the user typed.

    /** Returns the whole card so the main window can display it. */
    public Node getRoot()                   { return root; }
    
    /** Lets the controller read the email the user typed. */
    public TextField getEmailField()        { return emailField; }
    
    /** Lets the controller read the username the user typed. */
    public TextField getUsernameField()     { return usernameField; }
    
    /** Lets the controller read the password the user typed. */
    public PasswordField getPasswordField() { return passwordField; }
    
    /** Lets the controller attach a click action to the CONFIRM button. */
    public Button getConfirmButton()        { return confirmButton; }
    
    /** Lets the controller attach a click action to the X button. */
    public Button getBackButton()           { return backButton; }
    
    /** Allows the controller to show red error text on the screen. */
    public void showMessage(String msg)     { messageLabel.setText(msg); }
}
