package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * FORGOT PASSWORD VIEW — the "reset my password" card.
 *
 * Reached by clicking the "FORGOT PASSWORD?" link on the login card.
 * It's a minimal card with a single email field and a RESET button.
 * ForgotPassController decides what happens when RESET is pressed.
 */
public class ForgotPassView {

    private final VBox root;                 // the card
    private final TextField emailField;      // EMAIL input
    private final Button confirmButton;      // "RESET"
    private final Button backButton;         // "X" close
    private final Label messageLabel;        // inline status/error text

    public ForgotPassView() {
        // ---- Header ----
        Label titleLabel = new Label("FORGOT PASSWORD");
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ---- Email row ----
        Label emailLabel = new Label("EMAIL");
        emailLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        emailField = new TextField();
        emailField.setPrefWidth(140);
        emailField.setStyle(
            "-fx-background-color: #666666; -fx-text-inner-color: white;" +
            "-fx-background-radius: 0;"
        );

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.add(emailLabel, 0, 0);
        formGrid.add(emailField, 1, 0);

        // ---- Status label + reset button ----
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        confirmButton = new Button("RESET");
        confirmButton.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
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

    public Node getRoot()               { return root; }
    public TextField getEmailField()    { return emailField; }
    public Button getConfirmButton()    { return confirmButton; }
    public Button getBackButton()       { return backButton; }
    public void showMessage(String msg) { messageLabel.setText(msg); }
}
