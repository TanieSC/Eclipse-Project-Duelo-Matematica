package model;

import java.util.ArrayList;
import java.util.List;

/**
 * LEADERBOARD MODEL — The data for the leaderboard.
 * 
 * WHAT DOES THIS CLASS DO?
 * It holds the list of player entries (rank, username, tier). 
 * It doesn't know anything about UI or buttons. Right now, it fills 
 * itself with hardcoded dummy data, but later you can connect this 
 * to a real database.
 */
public class LeaderBoardModel {

    // The data container for a single row on the leaderboard
    public static class LeaderboardEntry {
        private final int rank;
        private final String user;
        private final String tier;

        public LeaderboardEntry(int rank, String user, String tier) {
            this.rank = rank;
            this.user = user;
            this.tier = tier;
        }

        public int getRank()   { return rank; }
        public String getUser(){ return user; }
        public String getTier(){ return tier; }
    }

    private final List<LeaderboardEntry> entries = new ArrayList<>();

    public LeaderBoardModel() {
        // Hardcoded dummy data (as noted in your original file)
        entries.add(new LeaderboardEntry(1, "CIRE", "ROYAL"));
        entries.add(new LeaderboardEntry(2, "MathWizard99", "DIAMOND")); 
        entries.add(new LeaderboardEntry(3, "EulerKing", "GOLD"));
        entries.add(new LeaderboardEntry(4, "GaussProExtra", "GOLD"));    
        entries.add(new LeaderboardEntry(5, "LogicMaster", "SILVER"));   
        entries.add(new LeaderboardEntry(6, "StatGenius", "SILVER"));
        entries.add(new LeaderboardEntry(7, "DerivDemon", "BRONZE"));
        entries.add(new LeaderboardEntry(8, "IntegralHero", "BRONZE"));  
        entries.add(new LeaderboardEntry(9, "NewtonApprentice", "BRONZE")); 
    }

    public List<LeaderboardEntry> getEntries() {
        return entries;
    }
}
