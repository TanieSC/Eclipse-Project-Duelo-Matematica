package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * REGISTER VIEW — the "create account" card.
 *
 * Same shape as LoginView but with three input rows (email, username,
 * password) and no forgot-password link. It exposes its fields and
 * buttons so RegisterController can validate input and call the
 * AuthService. It doesn't decide anything itself.
 */
public class RegisterView {

    private final VBox root;                    // the card
    private final TextField emailField;         // EMAIL input
    private final TextField usernameField;      // USERNAME input
    private final PasswordField passwordField;  // PASSWORD input
    private final Button confirmButton;         // "CONFIRM"
    private final Button backButton;            // "X" close
    private final Label messageLabel;           // inline error label

    public RegisterView() {
        // ---- Header ----
        Label titleLabel = new Label("CREATE ACCOUNT");
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ---- Inputs ----
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

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.add(emailLabel, 0, 0);     formGrid.add(emailField, 1, 0);
        formGrid.add(usernameLabel, 0, 1);  formGrid.add(usernameField, 1, 1);
        formGrid.add(passwordLabel, 0, 2);  formGrid.add(passwordField, 1, 2);

        // ---- Error label + confirm ----
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        confirmButton = new Button("CONFIRM");
        confirmButton.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        confirmButton.setPrefSize(130, 35);
        confirmButton.setStyle(
            "-fx-background-color: #999999; -fx-text-fill: black;" +
            "-fx-background-radius: 0; -fx-cursor: hand;"
        );

        // ---- Assemble ----
        root = new VBox(15, headerBox, formGrid, messageLabel, confirmButton);
        root.setPadding(new Insets(15, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle(
            "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
            "-fx-border-width: 1px;"
        );
    }

    // -- Accessors for the controller --

    public Node getRoot()                   { return root; }
    public TextField getEmailField()        { return emailField; }
    public TextField getUsernameField()     { return usernameField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getConfirmButton()        { return confirmButton; }
    public Button getBackButton()           { return backButton; }
    public void showMessage(String msg)     { messageLabel.setText(msg); }
}
