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
     * CONSTRUCTOR — build the window's contents, but don't show them yet.
     *
     * Called ONCE from App.start(). By the time this finishes:
     *   - the Stage has a Scene (the screen),
     *   - the home menu (header + 3 buttons) is sitting inside that Scene,
     *   - the window is still invisible.
     *
     * Showing the window is a separate step (view.show()), so that the
     * caller can wire up all the button actions *before* the user gets a
     * chance to click anything. Otherwise the buttons would do nothing
     * for the split second between being visible and being wired up.
     *
     * The 'stage' parameter is the empty window JavaFX hands to App.start.
     * We fill it in and store a reference so later methods (like
     * getStage()) can pass it around — for example, to a popup that wants
     * to know who its owner is.
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
     * HELPER — build one menu button with the shared "thick border" look.
     *
     * WHY A HELPER? The three home buttons should look identical (same
     * size, same border, same font). Instead of repeating ~8 lines of
     * styling three times, this method builds one and gets called three
     * times with different labels. Change the style once here and all
     * three buttons update together — no risk of them drifting apart.
     *
     * WHY setMaxWidth(Double.MAX_VALUE)? It lets each button stretch to
     * the width of the widest sibling in the VBox. That's why "Login",
     * "Register", and "Game Settings" end up as three identical rectangles
     * instead of three differently-sized ones.
     *
     * WHO CALLS IT: only this class, from the constructor. It's 'private'
     * because outsiders have no business creating menu buttons — the view
     * decides what the home page looks like.
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
     * PAGE SWAP — replace whatever's currently displayed with a new card.
     *
     * THE CORE OF INLINE NAVIGATION. When the user clicks Login,
     * Register, or Forgot Password, the controller builds that card's
     * view and calls setContent(view.getRoot()). This method takes that
     * root Node (could be a VBox, HBox, GridPane — anything that extends
     * Node) and drops it into the StackPane as its only child. The old
     * content is thrown away; the new content is shown. The window itself
     * never changes size or position — only its contents.
     *
     * WHY 'Node' AND NOT 'VBox'? The caller shouldn't have to know what
     * the card is built out of. It just says "here's a thing to show."
     * Later, if a card's layout class changes, this method needs no
     * edit — it accepts any Node.
     *
     * WHO CALLS IT: MainMenuController — every goToLogin / goToRegister /
     * goToForgotPassword builds a card and calls this.
     */
    public void setContent(Node node) {
        root.getChildren().setAll(node);
    }

    /**
     * GO HOME — swap back to the menu card.
     *
     * Called whenever a sub-page (login, register, forgot-password) wants
     * to return the user to the main menu — for example, after a
     * successful login, after the user clicks "X" to cancel, or after
     * registration succeeds. It just puts the pre-built menuContent VBox
     * back into the StackPane.
     *
     * WHY KEEP menuContent AS A FIELD? Because this method needs to
     * re-insert it. If we threw it away after showing the first card, we'd
     * have to rebuild the whole menu every time the user comes back. By
     * keeping one instance around, switching back is instant and any
     * state (like a "Logged in as X" label on the login button) survives.
     *
     * WHO CALLS IT: MainMenuController.goToMenu() — which is itself
     * called from LoginController (after successful login), from every
     * "X" cancel button, etc.
     */
    public void showMenu() {
        root.getChildren().setAll(menuContent);
    }

    // -- Accessors the controller needs to hook up events --

    /**
     * The Login button from the home menu.
     *
     * Returned so MainMenuController can attach a click handler
     * (.setOnAction(...)) that calls goToLogin(). The view doesn't know
     * or care what the button will do — it just hands it over.
     *
     * Also read by MainMenuController.goToMenu() so it can change the
     * button's text to "Logged in: <username>" after a successful login.
     */
    public Button getLoginButton()    { return loginButton; }

    /**
     * The Register button from the home menu.
     * Same pattern as getLoginButton — the controller wires it to
     * goToRegister().
     */
    public Button getRegisterButton() { return registerButton; }

    /**
     * The Game Settings button from the home menu.
     * Wired by MainMenuController to open the settings popup
     * (a modal Stage, not an inline card swap).
     */
    public Button getSettingsButton() { return settingsButton; }

    /**
     * The main window's Stage — the OS-level window object.
     *
     * WHY EXPOSE THIS? Popups need an "owner" Stage so they can:
     *   - appear centered on the main window,
     *   - stay on top of it,
     *   - block its input while open (modality),
     *   - not clutter the OS taskbar with a separate icon.
     *
     * The GameSettingsView constructor takes a Stage as 'owner' — the
     * controller gets that Stage from here. Without this getter, the
     * controller would have no way to tell the popup who its parent is.
     *
     * WHO CALLS IT: MainMenuController.openGameSettings() — and nothing
     * else, currently. That's fine; one clear caller is better than a
     * vague "somebody might want it."
     */
    public Stage getStage()           { return stage; }

    /**
     * SHOW THE WINDOW — make the OS window actually appear on screen.
     *
     * Called exactly once, from App.start(), AFTER the controller has
     * already been constructed (which wires all the button actions).
     * Doing it in that order means every button is already clickable and
     * responsive the instant the window becomes visible.
     *
     * If you ever need a "splash screen then menu" startup sequence, you
     * would delay this call and show something else first — but for now,
     * one show() is enough.
     *
     * Note: 'show()' on a Stage makes it appear AND starts the JavaFX
     * event loop for that window. Callers must not call show() twice on
     * the same Stage unless the intention is truly to re-show after a
     * hide().
     */
    public void show() {
        stage.show();
    }
}