package app;

/**
 * LAUNCHER — the "back door" into the app.
 *
 * WHY THIS FILE EXISTS
 * --------------------
 * Normally you would just run App.main(...) directly and be done. But
 * there's a quirk in the JVM: when the class you run **extends**
 * javafx.application.Application (which App does), the JVM does a safety
 * check *before* running anything, asking "is the JavaFX runtime module
 * available?". If your JavaFX jars are sitting on the classpath (a folder
 * of .jar files) rather than the module path, that check fails and the JVM
 * prints:
 *
 *     Error: JavaFX runtime components are missing, and are required
 *     to run this application
 *
 * …and quits before your code gets a chance to run.
 *
 * This class sidesteps the check entirely. It does NOT extend Application,
 * so the JVM skips the check. Then, in its main method, it manually asks
 * JavaFX to start up and hands control over to App.
 *
 * WHAT TO DO WITH IT
 * ------------------
 * In your IDE's run configuration (or on the command line), make the main
 * class be **app.Launcher** instead of **app.App**. Everything else stays
 * the same. If you ever switch to running with a proper --module-path flag
 * (Solution A from the earlier fix), you can go back to running app.App
 * directly and this class becomes harmless/unused.
 *
 * Think of it as a small door in front of the main door — the main door
 * has a guard (the JVM's module check) who asks a question you can't
 * answer yet, so you go through the side door instead.
 */
public class Launcher {

    /**
     * The real entry point when you run the app through this class.
     *
     * What it does, in plain English:
     *
     *   1. Application.launch(...) is JavaFX's official "boot me up" call.
     *      It spins up the JavaFX toolkit (window manager, event thread,
     *      rendering pipeline) and then calls the given class's
     *      start(...) method on the correct thread.
     *
     *   2. We tell it which class to hand control to: App.class. So the
     *      flow becomes:
     *
     *          Launcher.main → Application.launch(App.class) → App.start(Stage)
     *
     *   3. The args array from our main is forwarded unchanged, so any
     *      command-line arguments a user passes still reach App.start.
     *
     * Why this avoids the "JavaFX runtime components are missing" error:
     *   That error is triggered by the JVM's *launcher* when the class
     *   named on the command line extends Application. Launcher does not,
     *   so the check is never performed. By the time App's start runs,
     *   JavaFX is already up (because launch() started it), so no
     *   complaint is made.
     */
    public static void main(String[] args) {
        // "Please start JavaFX, and when it's ready, call App.start(stage).
        //  Any command-line arguments are forwarded to it."
        javafx.application.Application.launch(MainApp.class, args);
    }
}