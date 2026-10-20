package model;

/**
 * USER — a simple information container (often called a "Data Class").
 * 
 * WHAT DOES THIS CLASS DO?
 * Think of this class like a physical "ID Card" or a Registration Form.
 * It doesn't know how to draw a screen, how to check if a password is 
 * correct, or how to start a game. Its ONLY job is to hold a person's 
 * information together in one neat package.
 * 
 * WHY USE "FINAL" FIELDS?
 * Notice the word `final` on the variables below. This means once the 
 * ID card is printed (once the User is created), the details can NEVER 
 * be changed. You can't scratch out the email and write a new one. This 
 * makes the app much safer, because no other part of the code can 
 * accidentally mess up a user's data.
 */
public class User {

    // The email address used to log in. This acts as the unique "key" 
    // for the account (no two users can have the same email).
    private final String email;

    // The display name the player wants to be known by in the game.
    private final String username;

    // The secret password used to log in. 
    // (Note: In a real, highly secure app, you would scramble/encrypt 
    // this before saving it, but for this game, we just store it as text).
    private final String password;

    /**
     * CONSTRUCTOR — The "Form Printer".
     * 
     * When the app needs to create a new User, it uses this method. 
     * We must provide the email, username, and password all at once. 
     * The method then locks them into the object.
     * 
     * @param email    The user's email address.
     * @param username The user's display name.
     * @param password The user's secret password.
     */
    public User(String email, String username, String password) {
        this.email    = email;
        this.username = username;
        this.password = password;
    }

    // ============================================================
    // GETTERS — The "Peek Holes".
    // ============================================================
    // These methods let other parts of the app (like the Login Screen 
    // or the Game Screen) look at the user's information. 
    // Because we only have "get" methods and no "set" methods, the 
    // outside world can read the data, but cannot change it.

    /** Returns the user's email address. */
    public String getEmail()    { return email; }

    /** Returns the user's display name. */
    public String getUsername() { return username; }

    /** Returns the user's password. */
    public String getPassword() { return password; }
}