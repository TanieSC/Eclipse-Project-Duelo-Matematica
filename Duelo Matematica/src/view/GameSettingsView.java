package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * GAME SETTINGS VIEW — a MODAL POPUP version of the settings card.
 *
 * Unlike LoginView / RegisterView / ForgotPassView (which return a Node
 * that MainMenuView swaps into its StackPane), this view creates its own
 * Stage. Opening it puts a separate OS window on top of the main menu;
 * the Modal modifier means the user cannot click anything in the main
 * window until this popup is closed.
 *
 * WHY THE POPUP PATTERN HERE?
 *   Settings are a "side trip" from the menu — you open them, tweak
 *   something, close them, and you're right back where you were. The
 *   main menu stays visible underneath, unchanged. That's different from
 *   Login/Register, where the menu is conceptually "replaced" while you
 *   authenticate.
 *
 * The view still only builds UI and exposes its controls via getters.
 * It does not decide what the "X" button does — the controller wires
 * that to close(). This keeps the view a dumb, reusable shell.
 */
public class GameSettingsView {

    // The OS window owned by this popup. Created once in the constructor.
    private final Stage stage;

    // The "X" button in the top-right. Exposed so the controller can
    // decide what closing means (here: close this popup).
    private final Button backButton;

    /**
     * Builds the popup window but does NOT display it yet.
     * Call show() when you're ready.
     *
     * @param owner the main window — the popup will be centered on it and
     *              will grey out the owner's input while open.
     */
    public GameSettingsView(Stage owner) {
        // ---- Window setup ----
        stage = new Stage();
        // "APPLICATION_MODAL" means: while this window is open, clicks on
        // any other window of this app are ignored. This is what makes it
        // feel like a real dialog.
        stage.initModality(Modality.APPLICATION_MODAL);
        // Tie this popup to the main window — it will always appear on top
        // of the owner and disappear from the taskbar entry of the owner.
        stage.initOwner(owner);
        stage.setTitle("Game Settings");
        stage.setResizable(false);

        // ---- Header ----
        Label titleLabel = new Label("GAME SETTINGS");
        titleLabel.setStyle("-fx-font-size: 16px;");

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ---- Body (placeholder for now) ----
        Label placeholder = new Label("Settings coming soon…");
        placeholder.setStyle("-fx-font-size: 13px;");

        // ---- Assemble the card ----
        VBox root = new VBox(20, headerBox, placeholder);
        root.setPadding(new Insets(15, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle(
            "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
            "-fx-border-width: 1px;"
        );

        // ---- Scene ----
        stage.setScene(new Scene(root, 300, 200));
    }

    // -- Accessors for the controller --

    /** The "X" button — the controller decides what closing means. */
    public Button getBackButton() { return backButton; }

    /**
     * Show the popup and BLOCK this method's caller until the popup closes.
     *
     * Uses showAndWait() (not show()) so the controller's event handler
     * doesn't run past this line until the user dismisses the popup. If
     * you used show() instead, the code after this call would run
     * immediately — usually not what you want for a modal dialog.
     *
     * Despite "blocking", the JavaFX event loop keeps processing events
     * (including the popup's own buttons) — that's how showAndWait can
     * pause the caller without freezing the UI.
     */
    public void show() {
        stage.showAndWait();
    }

    /** Close the popup. Called by the controller in response to "X". */
    public void close() {
        stage.close();
    }
}