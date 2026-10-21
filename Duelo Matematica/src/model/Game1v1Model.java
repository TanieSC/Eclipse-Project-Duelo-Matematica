package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GAME 1V1 MODEL — The rules and data for the Tug-of-War battle.
 * 
 * WHAT DOES THIS CLASS DO?
 * It holds the question queue, keeps track of the player and opponent's 
 * positions (greenX and redX), and calculates the speed bonus. 
 * It doesn't know anything about buttons or animations.
 */
public class Game1v1Model {

    public enum Result { CORRECT, INCORRECT, VICTORY, DEFEAT }

    private static final double PLATFORM_WIDTH = 480.0;
    private static final double BOX_WIDTH = 35.0;
    private static final double BASE_STEP_DISTANCE = 40.0;

    private List<Question> questionQueue;
    private int currentQuestionIndex;
    private Question currentQuestion;
    private long questionStartTime;

    // Starting positions for the boxes
    public double greenX = -BOX_WIDTH - 5;
    public double redX = 5;

    public Game1v1Model(String categoryFilter) {
        initQuestionBank(categoryFilter);
    }

    private void initQuestionBank(String categoryFilter) {
        questionQueue = new ArrayList<>();
        List<Question> allQuestions = MathQuestions.loadAll();

        for (Question q : allQuestions) {
            if (categoryFilter == null || categoryFilter.equalsIgnoreCase("All") || 
                q.getTopic().equalsIgnoreCase(categoryFilter) || 
                (categoryFilter.equalsIgnoreCase("Calculus") && q.getTopic().startsWith("Calculus"))) {
                questionQueue.add(q);
            }
        }
        
        // Fallback if no questions match
        if (questionQueue.isEmpty()) questionQueue.addAll(allQuestions);

        Collections.shuffle(questionQueue);
        currentQuestionIndex = 0;
    }

    public void startGame() {
        currentQuestionIndex = 0;
        greenX = -BOX_WIDTH - 5;
        redX = 5;
        nextQuestion();
    }

    public void nextQuestion() {
        if (currentQuestionIndex >= questionQueue.size()) {
            Collections.shuffle(questionQueue);
            currentQuestionIndex = 0;
        }
        currentQuestion = questionQueue.get(currentQuestionIndex++);
        questionStartTime = System.currentTimeMillis();
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    /**
     * SUBMIT ANSWER — Calculates if the answer was right, applies the speed 
     * bonus, and moves the boxes. Returns the result of the turn.
     */
    public Result submitAnswer(int selectedIndex, int correctIndex) {
        long elapsedMs = System.currentTimeMillis() - questionStartTime;
        double elapsedSeconds = elapsedMs / 1000.0;

        double speedBonus = 0.0;
        if (elapsedSeconds >= 1.0 && elapsedSeconds <= 2.0) {
            speedBonus = 0.25;
        } else if (elapsedSeconds > 2.0 && elapsedSeconds <= 4.0) {
            speedBonus = 0.05;
        }

        boolean isCorrect = (selectedIndex == correctIndex);
        double actualStep = BASE_STEP_DISTANCE * (1.0 + speedBonus);

        if (isCorrect) {
            greenX += actualStep;
            redX += actualStep;
        } else {
            greenX -= actualStep;
            redX -= actualStep;
        }

        // Check for game over
        double leftEdge = -PLATFORM_WIDTH / 2.0;
        double rightEdge = PLATFORM_WIDTH / 2.0;

        if (greenX < leftEdge) return Result.DEFEAT;
        if (redX + BOX_WIDTH > rightEdge) return Result.VICTORY;

        return isCorrect ? Result.CORRECT : Result.INCORRECT;
    }
}
