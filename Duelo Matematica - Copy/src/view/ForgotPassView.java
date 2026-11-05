package view;

import javafx.geometry.HPos; // Added for Halignment
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ForgotPassView {

    private final StackPane root;                 
    private final TextField emailField;      
    private final Button confirmButton;      
    private final Button backButton;         
    private final Label messageLabel;        

    public ForgotPassView() {
        
        // ==========================================
        // 1. THE HEADER (Title Only)
        // ==========================================
        Label titleLabel = ViewUtils.createPopupHeaderLabel("FORGOT PASSWORD");
        
        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. INPUT FORM
        // ==========================================
        Label emailLabel = ViewUtils.createBodyTextLabel("EMAIL");
        
        emailField = new TextField();
        emailField.setPrefWidth(180);
        emailField.setFont(ViewUtils.getCustomFont(10));
        
        // Wrapped in the custom sprite text field
        StackPane emailContainer = ViewUtils.createSpriteTextField(emailField);
        emailContainer.setPrefWidth(200); 

        GridPane formGrid = ViewUtils.createStandardFormGrid(15, 15);
        formGrid.add(emailLabel, 0, 0);
        formGrid.add(emailContainer, 1, 0);

        // ==========================================
        // 3. FEEDBACK & ACTION
        // ==========================================
        messageLabel = ViewUtils.createBodyTextLabel("");
       // messageLabel.setFont(ViewUtils.getCustomFont(8));
        messageLabel.setStyle("-fx-text-fill: red;");

        confirmButton = ViewUtils.createSpriteButton("/buttons/btn.png", "RESET", null);
        confirmButton.setTranslateY(-10); 

        // ==========================================
        // 4. ASSEMBLE THE CONTENT
        // ==========================================
        // Group everything into a VBox
        VBox contentBox = new VBox(10, headerBox, formGrid, messageLabel, confirmButton);
        contentBox.setAlignment(Pos.TOP_CENTER);
        
        // Add top padding to make room for the floating exit button
        contentBox.setPadding(new Insets(25, 20, 20, 20)); 

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

    public Node getRoot()               { return root; }
    public TextField getEmailField()    { return emailField; }
    public Button getConfirmButton()    { return confirmButton; }
    public Button getBackButton()       { return backButton; }
    public void showMessage(String msg) { messageLabel.setText(msg); }
}