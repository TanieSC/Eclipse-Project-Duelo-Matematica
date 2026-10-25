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

    private final VBox root;                 
    private final TextField emailField;      
    private final Button confirmButton;      
    private final Button backButton;         
    private final Label messageLabel;        

    public ForgotPassView() {
        
        // 1. HEADER
        Label titleLabel = ViewUtils.createPopupHeaderLabel("FORGOT PASSWORD");
        backButton = ViewUtils.createCloseButton("X");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // 2. INPUT FORM
        Label emailLabel = ViewUtils.createBodyTextLabel("EMAIL");//w Label("EMAIL");
       // emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        
        emailField = new TextField();
        emailField.setPrefWidth(180);
        emailField.setFont(ViewUtils.getCustomFont(10));

        StackPane emailContainer = ViewUtils.createpriteTextField(emailField);
        
        GridPane formGrid = ViewUtils.createStandardFormGrid(15, 15);
        formGrid.add(emailLabel, 0, 0);
        formGrid.add(emailContainer, 1, 0);
        

        // 3. FEEDBACK & ACTION
        messageLabel = ViewUtils.createBodyTextLabel(""); //new Label();
        messageLabel.setFont(ViewUtils.getCustomFont(8));
        messageLabel.setStyle("-fx-text-fill: red;");

        confirmButton = ViewUtils.createSpriteButton("/buttons/btn.png", "RESET", null); //new Button("RESET");
        confirmButton.setTranslateY(-8); 
        
        // 4. ASSEMBLE CARD
        root = new VBox(10, headerBox, formGrid, messageLabel, confirmButton);
        root.setPadding(new Insets(15, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle(
            "-fx-border-color: transparent;" +
            "-fx-border-width: 1px;"
        );
    }

    public Node getRoot()               { return root; }
    public TextField getEmailField()    { return emailField; }
    public Button getConfirmButton()    { return confirmButton; }
    public Button getBackButton()       { return backButton; }
    public void showMessage(String msg) { messageLabel.setText(msg); }
}