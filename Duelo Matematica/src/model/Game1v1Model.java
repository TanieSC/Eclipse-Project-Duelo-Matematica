package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game1v1Model {

    public enum Result { CORRECT, INCORRECT, VICTORY, DEFEAT }

    private static final double PLATFORM_WIDTH = 480.0;
    private static final double BOX_WIDTH = 35.0;
    private static final double BASE_STEP_DISTANCE = 40.0;
    private static final int MAX_MISTAKES = 3;

    private List<Question> questionQueue;
    private int currentQuestionIndex;
    private Question currentQuestion;
    private long questionStartTime;
    
    private int mistakeCount;
    private int correctCount;

    private double greenX;
    private double redX;

    public Game1v1Model(String categoryFilter) {
        initQuestionBank(categoryFilter);
        greenX = -BOX_WIDTH - 5;
        redX = 5;
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
        
        if (questionQueue.isEmpty()) questionQueue.addAll(allQuestions);

        Collections.shuffle(questionQueue);
        currentQuestionIndex = 0;
    }

    public void startGame() {
        currentQuestionIndex = 0;
        greenX = -BOX_WIDTH - 5;
        redX = 5;
        mistakeCount = 0;
        correctCount = 0;
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

    public Question getCurrentQuestion() { return currentQuestion; }
    public double getGreenX() { return greenX; }
    public double getRedX() { return redX; }
    public int getMistakeCount() { return mistakeCount; }
    public int getCorrectCount() { return correctCount; }

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
        double gap = redX - greenX; // Preserve the distance between boxes

        if (isCorrect) {
            greenX += actualStep;
            redX += actualStep;
            correctCount++;
        } else {
            greenX -= actualStep;
            redX -= actualStep;
            mistakeCount++;
        }

        // Check for game over
        double leftEdge = -PLATFORM_WIDTH / 2.0;
        double rightEdge = PLATFORM_WIDTH / 2.0;

        // If 3 mistakes, force the green box to the absolute left edge
        if (mistakeCount >= MAX_MISTAKES) {
            greenX = leftEdge;
            redX = greenX + gap;
            return Result.DEFEAT;
        }
        
        // If pushed off left edge
        if (greenX < leftEdge) {
            greenX = leftEdge;
            redX = greenX + gap;
            return Result.DEFEAT;
        }
        
        // If pushed off right edge (Victory)
        if (redX + BOX_WIDTH > rightEdge) {
            redX = rightEdge - BOX_WIDTH;
            greenX = redX - gap;
            return Result.VICTORY;
        }

        return isCorrect ? Result.CORRECT : Result.INCORRECT;
    }
}