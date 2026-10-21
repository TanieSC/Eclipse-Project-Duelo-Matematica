package model;

import java.util.HashMap;
import java.util.Map;

/**
 * AUTH SERVICE — the "account manager".
 *
 * This is where the real login/register rules live. It keeps an in-memory
 * list of every registered account (a Map from email → User) and exposes
 * two operations:
 *
 *   - register(...) : try to create a new account, tell us if it worked.
 *   - login(...)    : try to find a matching account, return it or null.
 *
 * Because it knows nothing about JavaFX, you could reuse this class
 * unchanged in a mobile app, a web server, or a CLI tool.
 *
 * NOTE: In a real game this would talk to a database. Here we use a
 * HashMap so the app runs with zero setup. A "demo" account is seeded
 * so you can log in immediately.
 */
public class AuthService {

    // The "database". Key = email (lower-case), Value = the User record.
    private final Map<String, User> users = new HashMap<>();

    /**
     * On startup, add one demo account so testers don't have to register.
     *   Email:    demo@duelo.com
     *   Password: 1234
     */
    public AuthService() {
        users.put("demo@duelo.com", new User("demo@duelo.com", "demo", "1234", "Gold", 1450));
    }

    /**
     * REGISTER — try to add a new account.
     *
     * Returns true  → the account was created successfully.
     * Returns false → something was wrong (empty fields, or email already taken).
     *
     * The caller (controller) is responsible for showing an error message
     * based on the result.
     */
    public boolean register(String email, String username, String password) {
        // Reject obviously bad input. In real life you'd also validate email format.
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        // One account per email. If it exists, refuse.
        if (users.containsKey(email)) {
            return false;
        }
        users.put(email, new User(email, username, password));
        return true;
    }

    /**
     * LOGIN — try to find an account whose email AND password both match.
     *
     * Returns the matching User if successful, or null if either the
     * email doesn't exist or the password is wrong. Returning null (rather
     * than throwing) keeps the controller flow simple.
     */
    public User login(String email, String password) {
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    /**
     * NEW: Checks if the email exists in the system.
     */
    public boolean emailExists(String email) {
        return users.containsKey(email.toLowerCase().trim());
    }

    /**
     * NEW: Updates the password for an existing user.
     * Because the User class has 'final' fields, we must create a new User object.
     */
    public boolean updatePassword(String email, String newPassword) {
        String cleanEmail = email.toLowerCase().trim();
        User oldUser = users.get(cleanEmail);
        
        if (oldUser != null) {
            // Create a new User with the same email/username but a new password
            User updatedUser = new User(oldUser.getEmail(), oldUser.getUsername(), newPassword);
            users.put(cleanEmail, updatedUser);
            return true;
        }
        return false;
    }
}