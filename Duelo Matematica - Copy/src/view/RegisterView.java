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

    // CHANGED: Root is now a StackPane so the button can float
    private final StackPane root;                    
    private final TextField emailField;         
    private final TextField usernameField;      
    private final PasswordField passwordField;  
    private final Button confirmButton;         
    private final Button backButton;            
    private final Label messageLabel;           

    public RegisterView() {
        
        // ==========================================
        // 1. THE HEADER (Title Only)
        // ==========================================
        Label titleLabel = ViewUtils.createPopupHeaderLabel("CREATE ACCOUNT");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. THE FORM (Email, Username, Password)
        // ==========================================
        Label emailLabel = ViewUtils.createBodyTextLabel("EMAIL");
        emailField = new TextField();
        StackPane emailContainer = ViewUtils.createSpriteTextField(emailField);
        emailContainer.setPrefWidth(200); 
        
        Label usernameLabel =  ViewUtils.createBodyTextLabel("USERNAME");
        usernameField = new TextField();
        StackPane usernameContainer = ViewUtils.createSpriteTextField(usernameField);
        usernameContainer.setPrefWidth(200); 

        Label passwordLabel = ViewUtils.createBodyTextLabel("PASSWORD");
        passwordField = new PasswordField();
        StackPane passwordContainer = ViewUtils.createSpritePasswordField(passwordField);
        passwordContainer.setPrefWidth(200); 

        GridPane formGrid = ViewUtils.createStandardFormGrid(15, 15);
        formGrid.add(emailLabel, 0, 0);     formGrid.add(emailContainer, 1, 0);
        formGrid.add(usernameLabel, 0, 1);  formGrid.add(usernameContainer, 1, 1);
        formGrid.add(passwordLabel, 0, 2);  formGrid.add(passwordContainer, 1, 2);

        ColumnConstraints col1 = new ColumnConstraints(); 
        ColumnConstraints col2 = new ColumnConstraints(); 
        col2.setHgrow(Priority.ALWAYS);                   
        formGrid.getColumnConstraints().addAll(col1, col2);
        
        // ==========================================
        // 3. FEEDBACK & ACTION
        // ==========================================
        messageLabel = ViewUtils.createBodyTextLabel("");
       // messageLabel.setFont(ViewUtils.getCustomFont(8));
        messageLabel.setStyle("-fx-text-fill: red;");

        confirmButton = ViewUtils.createSpriteButton("/buttons/btn.png", "CONFIRM", null);
        confirmButton.setTranslateY(-20); 

        // ==========================================
        // 4. ASSEMBLE THE CONTENT
        // ==========================================
        // Group everything into a VBox
        VBox contentBox = new VBox(15, headerBox, formGrid, messageLabel, confirmButton);
        
        // Add top padding to make room for the floating exit button
        contentBox.setPadding(new Insets(25, 20, 20, 20));
        contentBox.setAlignment(Pos.TOP_CENTER);
        
        // ==========================================
        // 5. THE EXIT BUTTON (Same as ConcedeView)
        // ==========================================
        backButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_exit.png", 40, null);

        // Stack the content and button together
        root = new StackPane(contentBox, backButton);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Align and position the button exactly like ConcedeView
        StackPane.setAlignment(backButton, Pos.TOP_RIGHT);
        StackPane.setMargin(backButton, new Insets(-10, -4, 0, 0)); 
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
