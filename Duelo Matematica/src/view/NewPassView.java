package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class NewPassView {
    private final VBox root;
    private final PasswordField newPassField;
    private final PasswordField confirmPassField;
    private final Button confirmButton;
    private final Button backButton;
    private final Label messageLabel;

    // Pass the email in so we can display it
    public NewPassView(String email) {
        Label titleLabel = new Label("RESET PASSWORD");
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

        backButton = new Button("X");
        backButton.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // Show the user which email they are resetting
        Label emailDisplay = new Label("Resetting: " + email);
        emailDisplay.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555; -fx-font-style: italic;");

        Label passLabel = new Label("NEW PASSWORD");
        passLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        newPassField = new PasswordField();
        newPassField.setPrefWidth(140);
        newPassField.setStyle("-fx-background-color: #666666; -fx-text-inner-color: white; -fx-background-radius: 0;");

        Label confirmLabel = new Label("CONFIRM");
        confirmLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        confirmPassField = new PasswordField();
        confirmPassField.setPrefWidth(140);
        confirmPassField.setStyle("-fx-background-color: #666666; -fx-text-inner-color: white; -fx-background-radius: 0;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.add(passLabel, 0, 0);    formGrid.add(newPassField, 1, 0);
        formGrid.add(confirmLabel, 0, 1); formGrid.add(confirmPassField, 1, 1);

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        confirmButton = new Button("SAVE");
        confirmButton.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        confirmButton.setPrefSize(130, 35);
        confirmButton.setStyle("-fx-background-color: #999999; -fx-text-fill: black; -fx-background-radius: 0; -fx-cursor: hand;");

        root = new VBox(15, headerBox, emailDisplay, formGrid, messageLabel, confirmButton);
        root.setPadding(new Insets(15, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #777777; -fx-border-width: 1px;");
    }

    public Node getRoot() { return root; }
    public PasswordField getNewPassField() { return newPassField; }
    public PasswordField getConfirmPassField() { return confirmPassField; }
    public Button getConfirmButton() { return confirmButton; }
    public Button getBackButton() { return backButton; }
    public void showMessage(String msg) { messageLabel.setText(msg); }
}