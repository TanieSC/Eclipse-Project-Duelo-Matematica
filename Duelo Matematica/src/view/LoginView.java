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
	 private final StackPane root;

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
        // 1. THE CONTENT (Title, Form, Buttons)
        // ==========================================
        Label titleLabel = ViewUtils.createPopupHeaderLabel("LOGIN");

        // We keep a BorderPane for the title, but we remove the button from it
        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        Label emailLabel = ViewUtils.createBodyTextLabel("EMAIL");
        emailField = new TextField("demo@duelo.com");
        emailField.setPrefWidth(180);
        //emailField.setFont(ViewUtils.getCustomFont(10));
        StackPane emailContainer = ViewUtils.createSpriteTextField(emailField);
        emailContainer.setPrefWidth(200); 

        Label passwordLabel = ViewUtils.createBodyTextLabel("PASSWORD");
        passwordField = new PasswordField();
       // passwordField.setFont(ViewUtils.getCustomFont(10));
        StackPane passContainer  = ViewUtils.createSpritePasswordField(passwordField);

        GridPane formGrid = ViewUtils.createStandardFormGrid(15, 15);
        formGrid.add(emailLabel, 0, 0);    formGrid.add(emailContainer, 1, 0);
        formGrid.add(passwordLabel, 0, 1); formGrid.add(passContainer, 1, 1);

        forgotPasswordLink = new Hyperlink("FORGOT PASSWORD?");
        forgotPasswordLink.setFont(ViewUtils.getCustomFont(10));
        forgotPasswordLink.setStyle("-fx-text-fill: #55321E; -fx-underline: false;");

        messageLabel = ViewUtils.createBodyTextLabel("");
        //messageLabel.setFont(ViewUtils.getCustomFont(8));
        messageLabel.setStyle("-fx-text-fill: red;");

        confirmButton = ViewUtils.createSpriteButton("/buttons/btn.png", "CONFIRM", null);
        confirmButton.setTranslateY(-15); 

        // Group all the content into a VBox
        VBox contentBox = new VBox(10, headerBox, formGrid, forgotPasswordLink, messageLabel, confirmButton);
        contentBox.setAlignment(Pos.TOP_CENTER);
        
        // Add padding to push the content away from the edges
        contentBox.setPadding(new Insets(25, 20, 20, 20)); 

        // ==========================================
        // 2. THE EXIT BUTTON
        // ==========================================
        backButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_exit.png", 40, null);

        // ==========================================
        // 3. ASSEMBLE EVERYTHING
        // ==========================================
        // Stack the content and the exit button together
        root = new StackPane(contentBox, backButton);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Align the button to the top right corner
        StackPane.setAlignment(backButton, Pos.TOP_RIGHT);
        
        // Push the button OUTWARD to overlap the wooden border exactly like ConcedeView
        StackPane.setMargin(backButton, new Insets(-10, -4, 0, 0)); 
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