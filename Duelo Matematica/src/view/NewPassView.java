package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class NewPassView {
    private final VBox root;
    private final PasswordField newPassField;
    private final PasswordField confirmPassField;
    private final Button confirmButton;
    private final Button backButton;
    private final Label messageLabel;

    public NewPassView(String email) {
        Label titleLabel = ViewUtils.createPopupHeaderLabel("RESET PASSWORD");
        backButton = ViewUtils.createCloseButton("X");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        Label emailDisplay = new Label("Resetting: " + email);
        emailDisplay.setFont(ViewUtils.getCustomFont(12));
        emailDisplay.setStyle("-fx-text-fill: #555555;");

        Label passLabel = new Label("NEW PASSWORD");
        passLabel.setFont(ViewUtils.getCustomFont(13));
        newPassField = new PasswordField();

        StackPane newPassContainer  = ViewUtils.createpriteTextField(newPassField);
        
        Label confirmLabel = new Label("CONFIRM");
        confirmLabel.setFont(ViewUtils.getCustomFont(13));
        confirmPassField = new PasswordField();

        StackPane confirmPassContainer  = ViewUtils.createpriteTextField(confirmPassField);
        
        GridPane formGrid = ViewUtils.createStandardFormGrid(15, 15);
        formGrid.add(passLabel, 0, 0);    formGrid.add(newPassContainer, 1, 0);
        formGrid.add(confirmLabel, 0, 1); formGrid.add(confirmPassContainer, 1, 1);

        messageLabel = new Label();
        messageLabel.setFont(ViewUtils.getCustomFont(12));
        messageLabel.setStyle("-fx-text-fill: red;");

        confirmButton = ViewUtils.createSpriteButton("/buttons/btn.png", "SAVE", null);
        confirmButton.setTranslateY(-20); 

        root = new VBox(15, headerBox, emailDisplay, formGrid, messageLabel, confirmButton);
        root.setPadding(new Insets(15, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
    }

    public Node getRoot() { return root; }
    public PasswordField getNewPassField() { return newPassField; }
    public PasswordField getConfirmPassField() { return confirmPassField; }
    public Button getConfirmButton() { return confirmButton; }
    public Button getBackButton() { return backButton; }
    public void showMessage(String msg) { messageLabel.setText(msg); }
}