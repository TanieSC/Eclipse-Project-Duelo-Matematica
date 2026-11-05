package model;

/**
 * USER PROFILE MODEL — The data for the player's profile.
 * 
 * WHAT DOES THIS CLASS DO?
 * It holds the player's display name, their rank (Tier), and their 
 * skill score (ELO). It is a simple container with no logic.
 */
public class UserProfileModel {
    
    private final String username;
    private final String tier;
    private final int elo;

    public UserProfileModel(String username, String tier, int elo) {
        this.username = username;
        this.tier = tier;
        this.elo = elo;
    }

    public String getUsername() { return username; }
    public String getTier()     { return tier; }
    public int    getElo()      { return elo; }
}
