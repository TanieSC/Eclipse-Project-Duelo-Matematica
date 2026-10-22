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
        // The CSS ensures text starts typing from the right side
        emailField.setStyle(
            "-fx-background-color: #666666; -fx-text-inner-color: white;" +
            "-fx-background-radius: 0;"
        );

        GridPane formGrid = new GridPane();
        formGrid.setHgap(80);
        formGrid.setVgap(25);
        formGrid.setAlignment(Pos.CENTER); // Centers the grid in the card
        formGrid.add(emailLabel, 0, 0);
        formGrid.add(emailField, 1, 0);
        

        // 3. FEEDBACK & ACTION
        messageLabel = ViewUtils.createBodyTextLabel(""); //new Label();
        messageLabel.setFont(ViewUtils.getCustomFont(8));
        messageLabel.setStyle("-fx-text-fill: red;");

        confirmButton = ViewUtils.createSpriteButton("/buttons/btn.png", "RESET", null); //new Button("RESET");
       // confirmButton.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
      //  confirmButton.setPrefSize(130, 35);
      //  confirmButton.setStyle(
      //     "-fx-background-color: #999999; -fx-text-fill: black;" +
      //      "-fx-background-radius: 0; -fx-cursor: hand;"
      //  );
        
        // 4. ASSEMBLE CARD
        root = new VBox(10, headerBox, formGrid, messageLabel, confirmButton);
        root.setPadding(new Insets(15, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle(
            "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
            "-fx-border-width: 1px;"
        );
    }

    public Node getRoot()               { return root; }
    public TextField getEmailField()    { return emailField; }
    public Button getConfirmButton()    { return confirmButton; }
    public Button getBackButton()       { return backButton; }
    public void showMessage(String msg) { messageLabel.setText(msg); }
}