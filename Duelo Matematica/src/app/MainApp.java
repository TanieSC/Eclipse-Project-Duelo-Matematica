package app;

import controller.MainMenuController;
import javafx.application.Application;
import javafx.stage.Stage;
import model.AuthService;
import view.MainMenuView;
import view.ViewUtils;

/**
 * APP — the "ignition switch" of the whole program.
 *
 * This is the class the JVM launches first. Its only job is to build
 * the three pieces of the MVC pattern once and wire them together:
 *
 *      1. MODEL       →  AuthService  (the shared "account manager")
 *      2. VIEW        →  MainMenuView (the window shell)
 *      3. CONTROLLER  →  MainMenuController (the traffic cop for the menu)
 *
 * After that handshake, App steps out of the way. Every later action
 * (clicking Login, submitting a form, returning to the menu…) is handled
 * inside the controller / view / model triangle — App is not involved.
 *
 * WHY EXTEND Application?
 *   JavaFX needs a class that inherits from javafx.application.Application
 *   so the launcher (started by launch()) can call its start(...) method
 *   on the JavaFX Application Thread. That's the only thread allowed to
 *   touch UI controls.
 *
 * WHY DOES main() JUST CALL launch()?
 *   In JavaFX, the JVM needs to spin up the JavaFX toolkit (window system,
 *   rendering thread, etc.) before any Stage/Scene exists. launch() does
 *   that setup, then calls start(...) once the toolkit is ready. If you
 *   skipped launch() and tried to build a Scene in main(), you'd get
 *   "Toolkit not initialized".
 */
public class MainApp extends Application {

    /**
     * JavaFX calls this method ONCE, right after the toolkit is ready.
     * The 'stage' parameter is the empty primary window the toolkit has
     * already created for us — we're responsible for filling it with a
     * Scene (that's what MainMenuView's constructor does) and then
     * showing it.
     *
     * The startup sequence in plain English:
     *
     *   1. Create the shared AuthService — a single instance passed to
     *      every controller that needs it. If each controller created its
     *      own AuthService, none of them would see each other's registrations.
     *
     *   2. Create the MainMenuView — this builds the Stage's Scene and
     *      stores the three home-menu buttons.
     *
     *   3. Create the MainMenuController — this is what actually wires
     *      the three buttons to actions. Constructing the controller is
     *      what "activates" the view; before this line, the buttons do
     *      nothing when clicked.
     *
     *   4. Call view.show() — makes the OS window appear.
     *
     * NOTE: We never call System.exit() or Platform.exit() here — closing
     * the window via the OS close button is handled by JavaFX's default
     * shutdown behavior.
     */
    @Override
    public void start(Stage stage) {
    	
        //Load the custom font FIRST
        ViewUtils.loadCustomFont();

        // MODEL: one shared instance of the account manager.
        // Nothing else in App touches this — it's handed to MainMenuController
        // so it can hand the same instance on to Login/Register controllers.
        AuthService authService = new AuthService();

        // VIEW: build the window shell. This internally creates the Scene,
        // adds the home menu card, and stores the three buttons.
        MainMenuView view = new MainMenuView(stage);

        // CONTROLLER: attach actions to the buttons inside 'view'.
        // After this line, clicking "Login" opens the login card, etc.
        // The controller also implements Navigator, so it acts as the
        // "remote control" the other controllers use to swap pages.
        new MainMenuController(view, authService);

        // Show the OS window. From here on, everything is event-driven —
        // start(...) returns as soon as the window is displayed and
        // JavaFX's event loop takes over.
        view.show();
    }

    /**
     * Standard Java entry point. The JVM calls this first when you run
     * "java app.App".
     *
     * The single call to launch(args) does three things:
     *   1. Starts the JavaFX toolkit (window system, event thread).
     *   2. Constructs an instance of this App class (via reflection).
     *   3. Calls that instance's start(Stage) method on the JavaFX
     *      Application Thread, passing in a fresh primary Stage.
     *
     * Because the toolkit is built this way, main() must NOT do any
     * UI work itself — only launch(args) is safe to call here.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
