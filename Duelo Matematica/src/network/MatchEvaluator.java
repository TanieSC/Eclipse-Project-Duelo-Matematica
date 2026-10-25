package network;

/**
 * MATCH EVALUATOR - turns "how good a match is this player?" into one number from 0.0 to 1.0.
 *
 * STATUS: NEW FILE in package network. The scoring formula itself is unchanged.
 *
 * FORMULA
 *   eloScore       = max(0, 1 - |myElo - peerElo| / 600)     600 or more apart = 0
 *   proximityScore = e^(-latencyMs / 50)                     about 1.0 under 10 ms, drops past 100 ms
 *   final          = 0.7 * eloScore + 0.3 * proximityScore   clamped to 0..1
 * Examples: same Elo and 10 ms gives about 0.95. Elo 300 apart and 10 ms gives about 0.60.
 * Anyone 600 or more Elo away scores at most 0.30, which is below the 0.35 floor in
 * MatrixMatchmakingNode, so they are never matched.
 *
 * CHANGES compared with the version you pasted
 *   - Now "final" with a private constructor (it only holds a static method).
 *   - The meaning of discoveryLatencyMs changed. It is now measured on THIS computer
 *     (time between asking for the peer's info and receiving it). The old code subtracted a
 *     timestamp written by the OTHER computer, which only works if both clocks agree.
 *
 * TUNING: the three constants below (600, 0.7, 0.3) are the knobs. Elo weight plus proximity
 * weight should add up to 1.0.
 */
public final class MatchEvaluator {
    private static final int MAX_ELO_DELTA_LIMIT = 600;   // Beyond this, Elo score is 0
    private static final double ELO_WEIGHT = 0.7;         // Elo is top priority
    private static final double PROXIMITY_WEIGHT = 0.3;   // Network quality component

    private MatchEvaluator() { }

    /**
     * Grades a discovered peer from 0.0 (terrible match) to 1.0 (perfect match).
     *
     * @param discoveryLatencyMs how long THIS machine waited for the peer's mDNS reply
     *                           (measured locally, so no clock sync between PCs is needed)
     */
    public static double evaluatePeer(int myElo, int peerElo, long discoveryLatencyMs) {
        int eloDelta = Math.abs(myElo - peerElo);
        double eloScore = Math.max(0.0, 1.0 - ((double) eloDelta / MAX_ELO_DELTA_LIMIT));

        // Exponential decay: ~1.0 under 10ms, drops quickly past 100ms
        double proximityScore = Math.exp(-((double) discoveryLatencyMs / 50.0));

        double finalScore = (eloScore * ELO_WEIGHT) + (proximityScore * PROXIMITY_WEIGHT);
        return Math.min(1.0, Math.max(0.0, finalScore));
    }
}