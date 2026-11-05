package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class NewPassView {
    // CHANGED: Root is now a StackPane so the button can float
    private final StackPane root;
    private final PasswordField newPassField;
    private final PasswordField confirmPassField;
    private final Button confirmButton;
    private final Button backButton;
    private final Label messageLabel;

    public NewPassView(String email) {
        // ==========================================
        // 1. THE HEADER (Title Only)
        // ==========================================
        Label titleLabel = ViewUtils.createPopupHeaderLabel("RESET PASSWORD");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. THE CONTENT
        // ==========================================
        Label emailDisplay = ViewUtils.createBodyTextLabel("Resetting: " + email);

        Label passLabel = ViewUtils.createBodyTextLabel("NEW PASSWORD");
        newPassField = new PasswordField();
        StackPane newPassContainer = ViewUtils.createSpritePasswordField(newPassField);
        
        Label confirmLabel = ViewUtils.createBodyTextLabel("CONFIRM");
        confirmPassField = new PasswordField();
        StackPane confirmPassContainer = ViewUtils.createSpritePasswordField(confirmPassField);
        
        GridPane formGrid = ViewUtils.createStandardFormGrid(20, 20);
        formGrid.add(passLabel, 0, 0);    formGrid.add(newPassContainer, 1, 0);
        formGrid.add(confirmLabel, 0, 1); formGrid.add(confirmPassContainer, 1, 1);

        messageLabel =  ViewUtils.createBodyTextLabel("");//new Label();
       // messageLabel.setFont(ViewUtils.getCustomFont(12));
        messageLabel.setStyle("-fx-text-fill: red;");

        confirmButton = ViewUtils.createSpriteButton("/buttons/btn.png", "SAVE", null);
        confirmButton.setTranslateY(-20); 

        // ==========================================
        // 3. GROUP CONTENT
        // ==========================================
        VBox contentBox = new VBox(15, headerBox, emailDisplay, formGrid, messageLabel, confirmButton);
        contentBox.setPadding(new Insets(25, 20, 20, 20)); 
        contentBox.setAlignment(Pos.TOP_CENTER);

        // ==========================================
        // 4. THE EXIT BUTTON (Same as ConcedeView)
        // ==========================================
        backButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_exit.png", 40, null);

        // ==========================================
        // 5. ASSEMBLE EVERYTHING
        // ==========================================
        root = new StackPane(contentBox, backButton);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Align and position the button exactly like ConcedeView
        StackPane.setAlignment(backButton, Pos.TOP_RIGHT);
        StackPane.setMargin(backButton, new Insets(-10, -4, 0, 0)); 
    }

    public Node getRoot() { return root; }
    public PasswordField getNewPassField() { return newPassField; }
    public PasswordField getConfirmPassField() { return confirmPassField; }
    public Button getConfirmButton() { return confirmButton; }
    public Button getBackButton() { return backButton; }
    public void showMessage(String msg) { messageLabel.setText(msg); }
}