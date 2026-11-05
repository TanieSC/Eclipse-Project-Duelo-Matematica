package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * NETWORK 1v1 MODEL — timer + push-box state, both players drive the same boxes.
 *
 * greenX starts left of centre; redX starts right of centre.
 * A "correct" answer from YOU pushes both boxes right (+step).
 * A "correct" answer from the OPPONENT pushes both boxes left (-step).
 * Push green off the LEFT edge   → you lose.
 * Push red   off the RIGHT edge  → you win.
 * Timer expires → whoever's greenX sits right of 0 wins (exact 0 = draw).
 */
public class Network1v1Model {

    public enum Result { ONGOING, PLAYER_VICTORY, OPPONENT_VICTORY, DRAW }

    private static final double PLATFORM_WIDTH = 480.0;
    private static final double BOX_WIDTH = 35.0;
    private static final double BASE_STEP_DISTANCE = 40.0;

    private final String categoryFilter;
    private final int durationSeconds;

    private long matchSeed;
    private List<Question> questionQueue;
    private int currentQuestionIndex;
    private Question currentQuestion;
    private String[] currentOptions;
    private int currentCorrectIndex;

    private double greenX;
    private double redX;

    private long matchStartMs;

    public Network1v1Model(String categoryFilter, int durationSeconds) {
        this.categoryFilter = categoryFilter;
        this.durationSeconds = durationSeconds;
        this.greenX = -BOX_WIDTH - 5;
        this.redX = 5;
    }

    /** Seed must be set BEFORE startGame(). Both clients must use the same seed. */
    public void setSeed(long seed) {
        this.matchSeed = seed;
        buildQuestionQueue();
    }

    private void buildQuestionQueue() {
        questionQueue = new ArrayList<>();
        List<Question> all = MathQuestions.loadAll();
        for (Question q : all) {
            if (categoryFilter == null
                || categoryFilter.equalsIgnoreCase("All")
                || q.getTopic().equalsIgnoreCase(categoryFilter)
                || (categoryFilter.equalsIgnoreCase("Calculus")
                    && q.getTopic().startsWith("Calculus"))) {
                questionQueue.add(q);
            }
        }
        if (questionQueue.isEmpty()) questionQueue.addAll(all);
        Collections.shuffle(questionQueue, new Random(matchSeed));
        currentQuestionIndex = 0;
    }

    public void startGame() {
        greenX = -BOX_WIDTH - 5;
        redX = 5;
        currentQuestionIndex = 0;
        matchStartMs = System.currentTimeMillis();
        loadCurrentQuestion();
    }

    public void nextQuestion() {
        currentQuestionIndex++;
        loadCurrentQuestion();
    }

    private void loadCurrentQuestion() {
        if (currentQuestionIndex >= questionQueue.size()) {
            // reshuffle — never expected in a normal match, but keep it safe
            Collections.shuffle(questionQueue, new Random(matchSeed * 31L + currentQuestionIndex));
            currentQuestionIndex = 0;
        }
        currentQuestion = questionQueue.get(currentQuestionIndex);

        // Deterministic option shuffle, keyed by matchSeed + question index
        List<String> opts = new ArrayList<>();
        opts.add(currentQuestion.getCorrectAnswer());
        Collections.addAll(opts, currentQuestion.getWrongAnswers());
        Collections.shuffle(opts, new Random(matchSeed * 31L + currentQuestionIndex));

        currentOptions = opts.toArray(new String[0]);
        currentCorrectIndex = -1;
        for (int i = 0; i < currentOptions.length; i++) {
            if (currentOptions[i].equals(currentQuestion.getCorrectAnswer())) {
                currentCorrectIndex = i;
                break;
            }
        }
    }

    // ---------- Answer application ----------

    public void applyPlayerAnswer(int selectedIdx, long elapsedMs) {
        boolean correct = (selectedIdx == currentCorrectIndex);
        double step = BASE_STEP_DISTANCE * (1.0 + speedBonus(elapsedMs));
        if (correct) { greenX += step; redX += step; }
        else         { greenX -= step; redX -= step; }
    }

    public void applyOpponentAnswer(boolean correct, long elapsedMs) {
        double step = BASE_STEP_DISTANCE * (1.0 + speedBonus(elapsedMs));
        if (correct) { greenX -= step; redX -= step; }
        else         { greenX += step; redX += step; }
    }

    private double speedBonus(long elapsedMs) {
        double sec = elapsedMs / 1000.0;
        if (sec >= 1.0 && sec <= 2.0) return 0.25;
        if (sec >  2.0 && sec <= 4.0) return 0.05;
        return 0.0;
    }

    // ---------- Result checking ----------

    public Result checkResult() {
        double leftEdge  = -PLATFORM_WIDTH / 2.0;
        double rightEdge =  PLATFORM_WIDTH / 2.0;
        double gap = redX - greenX;

        if (greenX < leftEdge) {
            greenX = leftEdge;
            redX = greenX + gap;
            return Result.OPPONENT_VICTORY;
        }
        if (redX + BOX_WIDTH > rightEdge) {
            redX = rightEdge - BOX_WIDTH;
            greenX = redX - gap;
            return Result.PLAYER_VICTORY;
        }

        if (getRemainingSeconds() <= 0) {
            if (greenX > 0) return Result.PLAYER_VICTORY;
            if (greenX < 0) return Result.OPPONENT_VICTORY;
            return Result.DRAW;
        }
        return Result.ONGOING;
    }

    // ---------- Timer ----------

    public int getRemainingSeconds() {
        long elapsed = (System.currentTimeMillis() - matchStartMs) / 1000L;
        return Math.max(0, durationSeconds - (int) elapsed);
    }

    public int getDurationSeconds() { return durationSeconds; }

    // ---------- Getters ----------

    public Question getCurrentQuestion()      { return currentQuestion; }
    public String[] getCurrentOptions()       { return currentOptions; }
    public int getCurrentCorrectIndex()       { return currentCorrectIndex; }
    public int getCurrentQuestionIndex()      { return currentQuestionIndex; }
    public double getGreenX()                 { return greenX; }
    public double getRedX()                   { return redX; }
}
