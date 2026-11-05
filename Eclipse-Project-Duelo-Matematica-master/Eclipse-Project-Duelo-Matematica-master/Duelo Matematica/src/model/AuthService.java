package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * AUTH SERVICE — The "Account Manager" connected to an SQLite database.
 */
public class AuthService {

    // Path to the database file. (Assumes game.db is in the project root)
    private static final String URL = "jdbc:sqlite:src/data/duelo.db";

    public AuthService() {
        // Ensure the driver is loaded
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: SQLite JDBC Driver not found! Add the jar to your classpath.");
        }
    }

    /**
     * REGISTER — Creates a new user in the database.
     */
    public boolean register(String email, String username, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) return false;

        try (Connection conn = DriverManager.getConnection(URL)) {
            // Check if email already exists
            if (emailExists(email)) return false;

            // 1. Hash the password using PBKDF2
            String hashedPassword = hashPassword(password);
            
            // 2. Set default ELO
            int elo = 1000;

            // 3. Generate checksum
            String checksum = generateChecksum(email, username, hashedPassword, elo);

            // 4. Insert into DB
            String sql = "INSERT INTO users(email, user_n, pass, elo, checksum) VALUES(?,?,?,?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, username);
                pstmt.setString(3, hashedPassword);
                pstmt.setInt(4, elo);
                pstmt.setString(5, checksum);
                pstmt.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * LOGIN — Verifies the password against the database.
     */
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("pass");
                String username = rs.getString("user_n");
                int elo = rs.getInt("elo");
                String checksum = rs.getString("checksum");

                // Verify Checksum (Prevents DB tampering)
                if (!checksum.equals(generateChecksum(email, username, storedHash, elo))) {
                    System.err.println("SECURITY WARNING: Database record has been tampered with!");
                    return null;
                }

                // Verify Password
                if (verifyPassword(password, storedHash)) {
                    String tier = calculateTier(elo);
                    return new User(email, username, storedHash, tier, elo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Login failed
    }

    /**
     * EMAIL EXISTS — Checks if an email is in the DB.
     */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            return pstmt.executeQuery().next();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * UPDATE PASSWORD — Updates the hashed password for a user.
     */
    public boolean updatePassword(String email, String newPassword) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            String newHash = hashPassword(newPassword);
            // We need to fetch username and elo to recalculate the checksum
            String selectSql = "SELECT user_n, elo FROM users WHERE email = ?";
            PreparedStatement pstmtSelect = conn.prepareStatement(selectSql);
            pstmtSelect.setString(1, email);
            ResultSet rs = pstmtSelect.executeQuery();
            
            if (rs.next()) {
                String username = rs.getString("user_n");
                int elo = rs.getInt("elo");
                String newChecksum = generateChecksum(email, username, newHash, elo);

                String updateSql = "UPDATE users SET pass = ?, checksum = ? WHERE email = ?";
                PreparedStatement pstmtUpdate = conn.prepareStatement(updateSql);
                pstmtUpdate.setString(1, newHash);
                pstmtUpdate.setString(2, newChecksum);
                pstmtUpdate.setString(3, email);
                return pstmtUpdate.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // SECURITY UTILITIES
    // ==========================================

    private String hashPassword(String password) throws Exception {
        int iterations = 120000;
        int keyLength = 256;
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return "pbkdf2$" + iterations + "$" + 
               Base64.getEncoder().encodeToString(salt) + "$" + 
               Base64.getEncoder().encodeToString(hash);
    }

    private boolean verifyPassword(String inputPassword, String storedHash) throws Exception {
        String[] parts = storedHash.split("\\$");
        if (parts.length != 4) return false;

        int iterations = Integer.parseInt(parts[1]);
        byte[] salt = Base64.getDecoder().decode(parts[2]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

        PBEKeySpec spec = new PBEKeySpec(inputPassword.toCharArray(), salt, iterations, expectedHash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        return MessageDigest.isEqual(expectedHash, testHash);
    }

    private String generateChecksum(String email, String username, String hash, int elo) throws Exception {
        String data = email + username + hash + elo;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(data.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * UPDATE ELO — Writes a new Elo rating and recomputes the checksum.
     * Both network 1v1 clients call this after a match. Each side computes
     * its own new rating locally from (myElo, oppElo, actualScore); the
     * Elo formula is zero-sum, so both sides stay in agreement.
     */
    public boolean updateElo(String email, int newElo) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            // Need username + current pass to recompute the checksum.
            String selectSql = "SELECT user_n, pass FROM users WHERE email = ?";
            PreparedStatement pstmtSelect = conn.prepareStatement(selectSql);
            pstmtSelect.setString(1, email);
            ResultSet rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                String username = rs.getString("user_n");
                String passHash = rs.getString("pass");
                String newChecksum = generateChecksum(email, username, passHash, newElo);

                String updateSql = "UPDATE users SET elo = ?, checksum = ? WHERE email = ?";
                PreparedStatement pstmtUpdate = conn.prepareStatement(updateSql);
                pstmtUpdate.setInt(1, newElo);
                pstmtUpdate.setString(2, newChecksum);
                pstmtUpdate.setString(3, email);
                return pstmtUpdate.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Standard Elo formula, K = 32.
     * actualScore: 1.0 = win, 0.5 = draw, 0.0 = loss.
     * Both sides compute this from their OWN perspective — the gains and
     * losses are equal in magnitude, so the ladder stays zero-sum.
     */
    public static int computeNewElo(int myElo, int oppElo, double actualScore) {
        double expected = 1.0 / (1.0 + Math.pow(10.0, (oppElo - myElo) / 400.0));
        return (int) Math.round(myElo + 32.0 * (actualScore - expected));
    }

    private String calculateTier(int elo) {
        if (elo >= 2000) return "Grandmaster";
        if (elo >= 1600) return "Diamond";
        if (elo >= 1400) return "Gold";
        if (elo >= 1200) return "Silver";
        return "Bronze";
    }
}