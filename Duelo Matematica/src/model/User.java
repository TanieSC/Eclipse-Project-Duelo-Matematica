package model;

/**
 * USER — a plain data holder.
 *
 * Think of it as a "form" that stores one person's account details:
 * their email, display name, and password. It holds no logic and knows
 * nothing about screens, buttons, or databases — it's just a container.
 *
 * The fields are 'final', meaning once a User is created, its values
 * cannot change. This makes it safe to pass around and compare.
 */
public class User {

    // The email address used to log in — also acts as the account's unique key.
    private final String email;

    // The name shown to other players (doesn't have to be unique).
    private final String username;

    // The password as typed by the user. (In a real app you'd hash it first.)
    private final String password;

    /**
     * Builds a new User object. All three values must be provided up front.
     */
    public User(String email, String username, String password) {
        this.email    = email;
        this.username = username;
        this.password = password;
    }

    // Read-only accessors — the outside world can look but not modify.

    public String getEmail()    { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
