package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * LOGIN VIEW — the "login card" you see when you click Login on the menu.
 *
 * It's a pure UI component: it builds a VBox that looks like a small card
 * (title, email field, password field, forgot-password link, confirm
 * button, an error label). It does NOT know what a click on CONFIRM
 * should do — that's LoginController's job.
 *
 * Because it produces a Node (getRoot()), the MainMenuView can drop it
 * into its StackPane. No separate window, no showAndWait.
 */
public class LoginView {

    // The outer card. Everything is packed inside this VBox.
    private final VBox root;

    // Text inputs — the controller reads getText() when CONFIRM is pressed.
    private final TextField emailField;
    private final PasswordField passwordField;

    // Buttons and links — exposed to the controller so it can attach actions.
    private final Button confirmButton;   // "CONFIRM"
    private final Button backButton;      // the "X" in the top-right corner
    private final Hyperlink forgotPasswordLink; // "FORGOT PASSWORD?" link

    // Hidden text label that the controller can set to show validation
    // errors ("Invalid email or password", "Please fill in all fields", …).
    private final Label messageLabel;

    /**
     * Builds the card. Buttons have no actions yet — the controller
     * will wire them after construction.
     */
    public LoginView() {
        // ---- Header: "LOGIN" title + "X" close button ----
        Label titleLabel = new Label("LOGIN");
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ---- EMAIL row ----
        Label emailLabel = new Label("EMAIL");
        emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        emailField = new TextField();
        emailField.setPrefWidth(140);
        emailField.setStyle(
            "-fx-background-color: #666666; -fx-text-inner-color: white;" +
            "-fx-background-radius: 0;"
        );

        // ---- PASSWORD row ----
        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        passwordField = new PasswordField();
        passwordField.setPrefWidth(140);
        passwordField.setStyle(
            "-fx-background-color: #666666; -fx-text-inner-color: white;" +
            "-fx-background-radius: 0;"
        );

        // GridPane puts labels left, fields right, aligned in two columns.
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.add(emailLabel, 0, 0);    formGrid.add(emailField, 1, 0);
        formGrid.add(passwordLabel, 0, 1); formGrid.add(passwordField, 1, 1);

        // ---- Forgot-password link ----
        forgotPasswordLink = new Hyperlink("FORGOT PASSWORD?");
        forgotPasswordLink.setStyle(
            "-fx-text-fill: black; -fx-underline: false; -fx-font-size: 11px;"
        );

        // ---- Error message label (starts empty) ----
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        // ---- Confirm button ----
        confirmButton = new Button("CONFIRM");
        confirmButton.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        confirmButton.setPrefSize(130, 35);
        confirmButton.setStyle(
            "-fx-background-color: #999999; -fx-text-fill: black;" +
            "-fx-background-radius: 0; -fx-cursor: hand;"
        );

        // ---- Assemble the card ----
        root = new VBox(10, headerBox, formGrid, forgotPasswordLink, messageLabel, confirmButton);
        root.setPadding(new Insets(15, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);
        // Keep the card at its natural size — StackPane won't stretch it.
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle(
            "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
            "-fx-border-width: 1px;"
        );
    }

    // -- Accessors for the controller --

    /** The card itself. MainMenuView.setContent() inserts this into the window. */
    public Node getRoot() { return root; }

    public TextField getEmailField()        { return emailField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getConfirmButton()        { return confirmButton; }
    public Button getBackButton()           { return backButton; }
    public Hyperlink getForgotPasswordLink(){ return forgotPasswordLink; }

    /** Called by the controller to display validation errors. */
    public void showMessage(String msg) {
        messageLabel.setText(msg);
    }
}