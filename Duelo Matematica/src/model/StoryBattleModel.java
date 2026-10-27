package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StoryBattleModel {

    public static class Question {
        public String topic;
        public String difficulty;
        public String text;
        public List<String> options;
        public int correctIndex;

        public Question(model.Question q) {
            this.topic = q.getTopic();
            this.difficulty = q.getDifficulty();
            this.text = q.getText();
            
            String correctAnswer = q.getCorrectAnswer();
            List<String> shuffled = new ArrayList<>();
            shuffled.add(correctAnswer);
            Collections.addAll(shuffled, q.getWrongAnswers());
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
    
    public double greenX;
    public double redX;

    public StoryBattleModel() {
        questionQueue = new ArrayList<>();
        
        // PULL FROM THE REAL MATH QUESTIONS BANK!
        List<model.Question> allQuestions = MathQuestions.loadAll();
        
        // Shuffle the 50 questions
        Collections.shuffle(allQuestions);
        
        // Convert them into Story Mode Questions
        for (int i = 0; i < 10; i++) {
            questionQueue.add(new Question(allQuestions.get(i)));
        }
        
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
    }

    public Question getCurrentQuestion() { return currentQuestion; }

    public Result submitAnswer(int selectedIndex) {
        boolean isCorrect = (selectedIndex == currentQuestion.correctIndex);
        double step = BASE_STEP_DISTANCE;
        
        if (isCorrect) {
            greenX += step;
            redX += step;
        } else {
            greenX -= step;
            redX -= step;
        }
        return checkWinCondition(isCorrect);
    }

    public Result submitOpponentAnswer(int chosenIndex, long delayMs) {
        boolean isCorrect = (chosenIndex == currentQuestion.correctIndex);
        
        double speedBonus = 0.0;
        if (isCorrect) {
            if (delayMs >= 1000 && delayMs <= 2000) speedBonus = 0.25;
            else if (delayMs > 2000 && delayMs <= 4000) speedBonus = 0.05;
        }
        
        double step = BASE_STEP_DISTANCE * (1.0 + speedBonus);

        if (isCorrect) {
            greenX -= step; 
            redX -= step;
        } else {
            greenX += step; 
            redX += step;
        }
        return checkWinCondition(isCorrect);
    }

    private Result checkWinCondition(boolean isCorrect) {
        double leftEdge = -PLATFORM_WIDTH / 2.0;
        double rightEdge = PLATFORM_WIDTH / 2.0;

        if (greenX < leftEdge) return Result.DEFEAT;
        if (redX + BOX_WIDTH > rightEdge) return Result.VICTORY;
        return isCorrect ? Result.CORRECT : Result.INCORRECT;
    }
}