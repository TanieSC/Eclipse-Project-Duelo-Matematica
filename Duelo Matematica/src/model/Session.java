package model;

/**
 * SESSION — the app's "memory of who is currently logged in".
 *
 * JavaFX screens come and go, but we still need to know which user is
 * playing right now (to show their name on the menu, save their progress,
 * etc.). Session is a small global box that holds that one fact.
 *
 * "static" means you never create a Session object — you just call
 * Session.getCurrentUser() anywhere in the program.
 *
 * A private constructor prevents accidental `new Session()` — this class
 * is meant to be used as a utility, not as an object.
 */
public final class Session {

    // The one and only "currently logged in" user. Null means nobody.
    private static User currentUser;

    // Private so nobody can write `new Session()`.
    private Session() { }

    /** Called by the login controller after a successful login. */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /** Any screen can ask "who's playing?" — may return null. */
    public static User getCurrentUser() {
        return currentUser;
    }

    /** Convenience check: true if somebody is logged in. */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /** Called when the player logs out (or on shutdown). */
    public static void logout() {
        currentUser = null;
    }
}