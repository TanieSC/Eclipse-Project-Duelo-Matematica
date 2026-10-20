package model;

/**
 * SESSION — the app's "Short-Term Memory".
 * 
 * WHAT DOES THIS CLASS DO?
 * Think of the Session class like a whiteboard in the middle of a room. 
 * Anyone in the app (any screen, any controller) can walk up to this 
 * whiteboard and see two important pieces of information:
 * 
 * 1. WHO is currently playing (which user logged in).
 * 2. WHAT they were doing (is there a paused game waiting to be finished?).
 * 
 * WHY DO WE USE "STATIC"?
 * Notice every method and variable says `static`. In Java, this means 
 * "there is only ONE copy of this in the whole app." 
 * 
 * We don't want a separate memory for every screen! If you log in on the 
 * Login Screen, the Game Screen needs to know who you are. By making it 
 * static, everyone shares the same memory. No passing data back and forth required.
 * 
 * WHY IS THE CONSTRUCTOR PRIVATE?
 * Normally, you make objects with `new Session()`. But because we only 
 * ever want ONE whiteboard, we make the constructor `private`. This is 
 * Java's way of saying "Stop! You are not allowed to make a new one. 
 * Just use the one we already have."
 */
public final class Session {

    // --- THE WHITEBOARD ---

    // Who is logged in right now? 
    // If nobody is logged in, this is "null" (empty).
    private static User currentUser;

    // Do we have a paused game?
    // If a player quits halfway through, we save their game here. 
    // If they finish or log out, this becomes "null" (empty).
    private static GameService savedGame;

    /**
     * PRIVATE CONSTRUCTOR — prevents anyone from creating a new Session.
     * There is only ever ONE Session in the app.
     */
    private Session() { }

    // ============================================================
    // PART 1: THE USER (Login / Logout)
    // ============================================================

    /** 
     * Saves the user to the whiteboard when they log in. 
     * Called by the LoginController.
     */
    public static void setCurrentUser(User u) { currentUser = u; }

    /** 
     * Looks at the whiteboard to see who is logged in. 
     * Returns the User, or null if nobody is there.
     */
    public static User getCurrentUser()       { return currentUser; }

    /** 
     * Checks if anyone is currently logged in. 
     * Returns TRUE if there is a user, FALSE if the whiteboard is empty.
     */
    public static boolean isLoggedIn()        { return currentUser != null; }

    /**
     * LOGOUT — Wipes the whiteboard completely clean.
     * 
     * We clear the user, AND we delete any paused game. Why delete the game?
     * Because that game belongs to the person who just logged out. If a 
     * different friend logs in later, they shouldn't accidentally resume 
     * your game!
     */
    public static void logout() {
        currentUser = null;
        savedGame = null;
    }

    // ============================================================
    // PART 2: THE PAUSED GAME (Save / Resume)
    // ============================================================

    /**
     * SAVED GAME — Saves a game so the player can come back to it.
     * 
     * This is like hitting "Save" in a video game. When a player clicks 
     * the "X" button to quit mid-game, the GameController calls this method 
     * to stash their progress safely in memory.
     */
    public static void setSavedGame(GameService game) { savedGame = game; }

    /** 
     * Returns the saved game so the player can resume it.
     * If there is no saved game, it returns null.
     */
    public static GameService getSavedGame() { return savedGame; }

    /** 
     * Convenience check: Do we have a saved game right now? 
     * Returns TRUE if there is a game waiting, FALSE if not.
     */
    public static boolean hasSavedGame() { return savedGame != null; }

    /** 
     * CLEAR SAVED GAME — Deletes the save file.
     * 
     * This is called when the player actually finishes a round, or 
     * decides to start a brand new game. Once it's cleared, there is 
     * no going back to the old game.
     */
    public static void clearSavedGame() { savedGame = null; }
}