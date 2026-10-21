package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * GAME STORY MODEL — The rules and data for the Tug-of-War battle.
 */
public class GameStoryModel {

    // LOCAL EMBEDDED QUESTION BANK (from your original code)
    private static final String[][] LOCAL_MATH_QUESTIONS = {
        {"1", "Calculus 1", "Easy", "What is the derivative of f(x) = x⁵ - 3x² + 1?", "5x⁴ - 6x", "5x⁴ - 6x + 1", "x⁴ - 6x", "5x⁴ - 3x"},
        {"2", "Calculus 1", "Easy", "Evaluate the limit of (x² - 9) / (x - 3) as x → 3.", "6", "0", "3", "Undefined"},
        {"3", "Calculus 1", "Easy", "What is the derivative of f(x) = cos(4x)?", "-4sin(4x)", "4sin(4x)", "-sin(4x)", "-4cos(4x)"},
        {"4", "Calculus 1", "Easy", "Evaluate the limit of (eˣ - 1) / x as x → 0.", "1", "0", "∞", "e"},
        {"5", "Calculus 1", "Medium", "Find the critical points of f(x) = x³ - 6x² + 9x.", "x = 1 and x = 3", "x = 0 and x = 3", "x = -1 and x = -3", "x = 2 and x = 4"},
        {"6", "Statistics", "Easy", "What is the mean of the dataset {2, 4, 6, 8, 10}?", "6", "5", "7", "30"},
        {"7", "Statistics", "Easy", "Probability of rolling an even number on a fair 6-sided die?", "1/2", "1/6", "1/3", "2/3"},
        {"8", "Symbolic Logic", "Easy", "Truth value of P ∧ Q when P = True and Q = False?", "False", "True", "Undefined", "Tautology"},
        {"9", "Symbolic Logic", "Easy", "What is the contrapositive of P → Q?", "¬Q → ¬P", "Q → P", "¬P → ¬Q", "P ∧ ¬Q"},
        {"10", "Calculus 2", "Easy", "Evaluate the indefinite integral ∫ x³ dx.", "(1/4)x⁴ + C", "3x² + C", "(1/3)x⁴ + C", "x⁴ + C"}
    };

    public static class Question {
        public String category;
        public String difficulty;
        public String prompt;
        public List<String> options;
        public int correctIndex;

        public Question(String category, String difficulty, String prompt, String[] rawOptions) {
            this.category = category;
            this.difficulty = difficulty;
            this.prompt = prompt;

            String correctAnswer = rawOptions[0];
            List<String> shuffled = new ArrayList<>(Arrays.asList(rawOptions));
            Collections.shuffle(shuffled);

            this.options = shuffled;
            this.correctIndex = shuffled.indexOf(correctAnswer);
        }
    }

    public enum Result { CORRECT, INCORRECT, VICTORY, DEFEAT }

    private static final double PLATFORM_WIDTH = 480.0;
    private static final double BOX_WIDTH = 35.0;
    private static final double BASE_STEP_DISTANCE = 40.0;

    private final List<Question> questionQueue;
    private int currentQuestionIndex;
    private Question currentQuestion;
    private long questionStartTime;
    
    public double greenX;
    public double redX;

    public GameStoryModel() {
        questionQueue = new ArrayList<>();
        for (String[] row : LOCAL_MATH_QUESTIONS) {
            questionQueue.add(new Question(row[1], row[2], row[3], Arrays.copyOfRange(row, 4, 8)));
        }
        Collections.shuffle(questionQueue);
        currentQuestionIndex = 0;
        greenX = -BOX_WIDTH - 5;
        redX = 5;
    }

    public void startGame() {
        greenX = -BOX_WIDTH - 5;
        redX = 5;
        currentQuestionIndex = 0;
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

    public Result submitAnswer(int selectedOptionIndex) {
        long elapsedMs = System.currentTimeMillis() - questionStartTime;
        double elapsedSeconds = elapsedMs / 1000.0;

        double speedBonus = 0.0;
        if (elapsedSeconds >= 1.0 && elapsedSeconds <= 2.0) {
            speedBonus = 0.25;
        } else if (elapsedSeconds > 2.0 && elapsedSeconds <= 4.0) {
            speedBonus = 0.05;
        }

        boolean isCorrect = (selectedOptionIndex == currentQuestion.correctIndex);
        double actualStep = BASE_STEP_DISTANCE * (1.0 + speedBonus);

        if (isCorrect) {
            greenX += actualStep;
            redX += actualStep;
        } else {
            greenX -= actualStep;
            redX -= actualStep;
        }

        double leftPlatformEdge = -PLATFORM_WIDTH / 2.0;
        double rightPlatformEdge = PLATFORM_WIDTH / 2.0;

        if (greenX < leftPlatformEdge) return Result.DEFEAT;
        if (redX + BOX_WIDTH > rightPlatformEdge) return Result.VICTORY;

        return isCorrect ? Result.CORRECT : Result.INCORRECT;
    }
}
