package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * LOGIN VIEW — The visual layout for the login screen.
 *
 * WHAT DOES THIS CLASS DO?
 * Think of this class as a "dumb" artist. Its ONLY job is to draw the 
 * visual elements of the login card (the text boxes, buttons, and labels) 
 * and arrange them neatly. 
 * 
 * It does NOT know what happens when you click "CONFIRM", and it does NOT 
 * know how to check if a password is correct. It simply builds the UI and 
 * waits for the LoginController to come along and "wire up" the actions.
 * 
 * Once built, it hands the whole card to the main window using getRoot().
 */
public class LoginView {

    // --- THE BUILDING BLOCKS ---
    // We save these as class variables so the Controller can reach in 
    // and grab them later.

    // The main container holding everything (like a vertical stack of blocks).
    private final VBox root;

    // The input boxes where the user types their email and password.
    private final TextField emailField;
    private final PasswordField passwordField;

    // The clickable elements (Confirm, Close, and Forgot Password).
    private final Button confirmButton;   
    private final Button backButton;      
    private final Hyperlink forgotPasswordLink; 

    // A hidden text label that we can use to show red error messages.
    private final Label messageLabel;

    /**
     * CONSTRUCTOR — Runs once when the Login screen is created.
     * 
     * This is where we actually build and arrange all the visual pieces.
     */
    public LoginView() {
        
        // ==========================================
        // 1. THE HEADER (Title + Close Button)
        // ==========================================
        Label titleLabel = ViewUtils.createPopupHeaderLabel("LOGIN");//new Label("LOGIN");
        //titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        backButton = ViewUtils.createCloseButton("X");  //new Button("X");
        //backButton.setStyle(
        //    "-fx-background-color: transparent; -fx-font-weight: bold;" +
        //    "-fx-font-size: 14px; -fx-cursor: hand;"
        //);

        // A BorderPane lets us put the title in the center and the X on the right.
        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. THE FORM (Email and Password fields)
        // ==========================================
        Label emailLabel = new Label("EMAIL");
        emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        
        emailField = new TextField("demo@duelo.com");
        emailField.setPrefWidth(140);
        emailField.setStyle(
            "-fx-background-color: #666666; -fx-text-inner-color: white;" +
            "-fx-background-radius: 0;"
        );

        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        
        // PasswordField hides the characters as the user types (shows dots instead).
        passwordField = new PasswordField();
        passwordField.setPrefWidth(140);
        passwordField.setStyle(
            "-fx-background-color: #666666; -fx-text-inner-color: white;" +
            "-fx-background-radius: 0;"
        );

        // A GridPane works like an invisible spreadsheet. 
        // We put the labels in the left column and the text boxes in the right column.
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15); // Horizontal gap between columns
        formGrid.setVgap(15); // Vertical gap between rows
        formGrid.add(emailLabel, 0, 0);    formGrid.add(emailField, 1, 0);
        formGrid.add(passwordLabel, 0, 1); formGrid.add(passwordField, 1, 1);

        // ==========================================
        // 3. LINKS & FEEDBACK
        // ==========================================
        forgotPasswordLink = new Hyperlink("FORGOT PASSWORD?");
        forgotPasswordLink.setStyle(
            "-fx-text-fill: black; -fx-underline: false; -fx-font-size: 11px;"
        );

        // This label is empty at first, but the Controller will fill it 
        // with red text if the user makes a mistake.
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        // ==========================================
        // 4. THE CONFIRM BUTTON
        // ==========================================
        confirmButton = new Button("CONFIRM");
        confirmButton.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        confirmButton.setPrefSize(130, 35);
        confirmButton.setStyle(
            "-fx-background-color: #999999; -fx-text-fill: black;" +
            "-fx-background-radius: 0; -fx-cursor: hand;"
        );

        // ==========================================
        // 5. ASSEMBLING THE CARD
        // ==========================================
        // We stack everything vertically in our main 'root' VBox. 
        // The number '10' is the vertical spacing between each element.
        root = new VBox(10, headerBox, formGrid, forgotPasswordLink, messageLabel, confirmButton);
        
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
    // GETTERS — The "Control Panel" for the LoginController
    // ============================================================
    // These methods let the LoginController grab the pieces it needs 
    // to attach click events and read what the user typed.

    /** Returns the whole card so the main window can display it. */
    public Node getRoot() { return root; }

    /** Lets the controller read the email the user typed. */
    public TextField getEmailField()        { return emailField; }
    
    /** Lets the controller read the password the user typed. */
    public PasswordField getPasswordField() { return passwordField; }
    
    /** Lets the controller attach a click action to the CONFIRM button. */
    public Button getConfirmButton()        { return confirmButton; }
    
    /** Lets the controller attach a click action to the X button. */
    public Button getBackButton()           { return backButton; }
    
    /** Lets the controller attach a click action to the Forgot Password link. */
    public Hyperlink getForgotPasswordLink(){ return forgotPasswordLink; }

    /** Allows the controller to show red error text on the screen. */
    public void showMessage(String msg) {
        messageLabel.setText(msg);
    }
}