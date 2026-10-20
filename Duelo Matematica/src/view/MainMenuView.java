package view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * MAIN MENU VIEW — the "window shell" of the app.
 *
 * This is the ONLY view that owns the real Stage (the OS window).
 * Everything else — login, register, forgot password, settings —
 * is a Node that gets dropped into the center of this window by
 * setContent(). That's how the app feels like one window that "changes
 * pages" instead of many popups.
 *
 * Layer picture:
 *
 *     StackPane (root)
 *     ├─ menuContent     ← VBox with header + 3 buttons (the home page)
 *     ├─ Login card      ← shown after goToLogin()
 *     ├─ Register card   ← shown after goToRegister()
 *     └─ ...
 *
 * Only one child is visible at a time because setContent()/showMenu()
 * replaces the children.
 *
 * The view exposes its buttons so the controller can attach actions.
 * It does NOT decide what clicking a button should do — that's the
 * controller's job.
 */
public class MainMenuView {

    // The OS window — created once, reused forever.
    private final Stage stage;

    // The container that holds whichever card is currently shown.
    private final StackPane root;

    // The "home page" VBox — kept around so we can swap back to it.
    private final VBox menuContent;

    // The three home-page buttons. Exposed via getters so the controller
    // can wire them to navigator methods.
    private final Button loginButton;
    private final Button registerButton;
    private final Button settingsButton;

    /**
     * Builds the window, puts the home menu inside it, and sets the scene.
     * Nothing is shown yet — call show() when you're ready.
     */
    public MainMenuView(Stage stage) {
        this.stage = stage;

        // ---- Header banner ----
        Label headerLabel = new Label("Duelo\nX\nMatematica");
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setStyle(
            "-fx-font-size: 26px; -fx-font-weight: bold;" +
            "-fx-border-color: #222222; -fx-border-width: 3px;" +
            "-fx-padding: 12px 60px; -fx-background-color: #f4f4f4;" +
            "-fx-alignment: center; -fx-text-alignment: center;"
        );

        // ---- Menu buttons ----
        loginButton    = createMenuButton("Login");
        registerButton = createMenuButton("Register");
        settingsButton = createMenuButton("Game Settings");

        VBox buttonBox = new VBox(15, loginButton, registerButton, settingsButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setFillWidth(true);

        // ---- Home "page" ----
        menuContent = new VBox(30, headerLabel, buttonBox);
        menuContent.setAlignment(Pos.CENTER);
        // FillWidth false + USE_PREF_SIZE stop the banner from stretching
        // to fill the whole 800x500 window (which would look ugly).
        menuContent.setFillWidth(false);
        menuContent.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // ---- Root container + scene ----
        root = new StackPane(menuContent);
        root.setStyle("-fx-background-color: #ffffff;");

        stage.setTitle("Duelo X Matematica");
        stage.setScene(new Scene(root, 800, 500));
    }

    /**
     * Helper: builds a menu button with the shared "thick border" style.
     * Each button is allowed to grow to fill the widest sibling, so all
     * three buttons end up the same width.
     */
    private Button createMenuButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setMinHeight(55);
        b.setMaxHeight(55);
        b.setStyle(
            "-fx-font-size: 18px; -fx-font-weight: bold;" +
            "-fx-border-color: #000000; -fx-border-width: 3px;" +
            "-fx-padding: 10px 30px; -fx-background-color: #f9f9f9;" +
            "-fx-cursor: hand;"
        );
        return b;
    }

    /**
     * Swap the current page for any Node (a login card, register card, …).
     * Called by the controller during navigation.
     */
    public void setContent(Node node) {
        root.getChildren().setAll(node);
    }

    /** Bring the home menu back into view. */
    public void showMenu() {
        root.getChildren().setAll(menuContent);
    }

    // -- Accessors the controller needs to hook up events --
    public Button getLoginButton()    { return loginButton; }
    public Button getRegisterButton() { return registerButton; }
    public Button getSettingsButton() { return settingsButton; }
    
    /**
     * The main window's Stage. Exposed so the controller can pass it as
     * the "owner" when opening a popup (the popup will center on it and
     * block its input while open).
     */
    public Stage getStage()           { return stage; }

    /** Show the window. Called once, from App. */
    public void show() {
        stage.show();
    }
}