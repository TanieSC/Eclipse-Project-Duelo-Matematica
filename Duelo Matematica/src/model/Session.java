package model;

/**
 * SESSION — the app's "memory of who is playing and what they were doing".
 *
 * Two pieces of state live here:
 *   1. The current user (who logged in).
 *   2. The paused game, if any (a GameService holding mid-round progress).
 *
 * Both are static — one global instance for the whole app. Any screen
 * can ask "is anyone logged in?" or "do we have a paused game?" without
 * passing anything around.
 *
 * "static" means you never write 'new Session()'. A private constructor
 * enforces that.
 */
public final class Session {

    // The logged-in user, or null if nobody's logged in.
    private static User currentUser;

    // A paused GameService (mid-round), or null if the player has no
    // game in progress. Set when the player leaves a game via X; cleared
    // when a round finishes or the player logs out.
    private static GameService savedGame;

    private Session() { }   // never instantiated

    // ---- user ----
    public static void setCurrentUser(User u) { currentUser = u; }
    public static User getCurrentUser()       { return currentUser; }
    public static boolean isLoggedIn()        { return currentUser != null; }

    /**
     * Log out. Also drops any paused game — it's tied to the account
     * that just logged out, so a different user shouldn't inherit it.
     */
    public static void logout() {
        currentUser = null;
        savedGame = null;
    }

    // ---- paused game ----

    /**
     * Stash the current game so the player can resume later. Called by
     * GameController when the player clicks X mid-round.
     */
    public static void setSavedGame(GameService game) { savedGame = game; }

    /** The paused game, or null if there isn't one. */
    public static GameService getSavedGame() { return savedGame; }

    /** Convenience check: does a paused game exist? */
    public static boolean hasSavedGame() { return savedGame != null; }

    /** Forget the paused game. Called on game completion, logout, restart. */
    public static void clearSavedGame() { savedGame = null; }
}