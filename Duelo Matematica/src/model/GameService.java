package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GAME SERVICE — the "Referee" and "Scorekeeper" of a single game round.
 * 
 * WHAT DOES THIS CLASS DO?
 * Think of this as the brain of the game that lives behind the scenes. 
 * It doesn't know anything about screens, buttons, or colors. Its only job 
 * is to handle the rules and the math:
 * 
 * 1. It takes the massive list of 50 questions and filters them based on 
 *    what category the player chose (e.g., "Calculus").
 * 2. It shuffles the deck and picks 10 random questions for this round.
 * 3. It keeps track of the player's score and which question they are on.
 * 4. It shuffles the 4 multiple-choice answers so the correct one isn't 
 *    always in the same spot (like always option A).
 */
public class GameService {

    // The maximum number of questions a player answers in one round.
    public static final int QUESTIONS_PER_GAME = 10;

    // The category the player chose (e.g., "Calculus" or "All").
    private final String category;          
    
    // The "Master List" of all possible questions for the chosen category.
    private final List<Question> pool;      
    
    // The specific 10 questions chosen for THIS specific round.
    private List<Question> session;         
    
    // Which question we are currently looking at (0 = first question, 1 = second, etc.).
    private int currentIndex;
    
    // How many questions the player has gotten right so far.
    private int score;
    
    // The 4 answers currently displayed on the screen (mixed up).
    private String[] currentOptions;        
    
    // Which of those 4 answers is the correct one (0, 1, 2, or 3).
    private int correctOptionIndex;         

    /**
     * CONSTRUCTOR — sets up a new game with the chosen category.
     * 
     * It looks at the full list of questions and filters them. 
     * If the player chose "Calculus", it grabs Calculus 1, 2, and 3. 
     * If they chose "All", it grabs everything.
     */
    public GameService(List<Question> allQuestions, String category) {
        // Remember the category so if they pause and resume, we know what they were playing.
        this.category = (category == null) ? "All" : category;

        if (category == null || category.equalsIgnoreCase("All")) {
            // No filter, keep everything.
            this.pool = new ArrayList<>(allQuestions);
        } else if (category.equalsIgnoreCase("Calculus")) {
            // Grab all topics that start with the word "Calculus".
            this.pool = allQuestions.stream()
                .filter(q -> q.getTopic().startsWith("Calculus"))
                .collect(Collectors.toList());
        } else {
            // Exact match (like "Statistics" or "Symbolic Logic").
            this.pool = allQuestions.stream()
                .filter(q -> q.getTopic().equalsIgnoreCase(category))
                .collect(Collectors.toList());
        }
        
        // Safety net: If the filter matched nothing (typo in the category), 
        // just use the whole list so the game doesn't crash.
        if (this.pool.isEmpty()) this.pool.addAll(allQuestions);
    }

    /** Returns the category name (used by the game header and resume screen). */
    public String getCategory() { return category; }

    /**
     * START NEW GAME — Shuffles the deck and resets the score.
     * 
     * 1. Shuffles the filtered pool of questions.
     * 2. Grabs the first 10 (or fewer if the pool is tiny).
     * 3. Resets the score to 0 and the index to the first question.
     * 4. Prepares the answers for the first question.
     */
    public void startNewGame() {
        List<Question> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled);
        
        // Pick up to 10 questions.
        int take = Math.min(QUESTIONS_PER_GAME, shuffled.size());
        session = new ArrayList<>(shuffled.subList(0, take));
        
        currentIndex = 0;
        score = 0;
        prepareCurrentQuestion();
    }

    /**
     * PREPARE CURRENT QUESTION — Shuffles the 4 multiple-choice answers.
     * 
     * WHY DO WE DO THIS? If we didn't, the correct answer might always be 
     * option A. We scramble the answers like a deck of cards so the correct 
     * answer is in a random spot (A, B, C, or D), making the game fair.
     */
    private void prepareCurrentQuestion() {
        Question q = session.get(currentIndex);
        
        // Create a list of slots: [0, 1, 2, 3] and shuffle them.
        List<Integer> order = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(order);
        
        currentOptions = new String[4];
        for (int i = 0; i < 4; i++) {
            int original = order.get(i);
            if (original == 0) {
                // The '0' represents the correct answer. We remember where it landed.
                correctOptionIndex = i;
                currentOptions[i] = q.getCorrectAnswer();
            } else {
                // The '1, 2, 3' represent the wrong answers.
                currentOptions[i] = q.getWrongAnswers()[original - 1];
            }
        }
    }

    // --- HELPER METHODS (Getters) ---
    // These let the Controller grab the current state of the game.
    
    public Question getCurrentQuestion()    { return session.get(currentIndex); }
    public String[] getCurrentOptions()     { return currentOptions; }
    public int      getCorrectOptionIndex() { return correctOptionIndex; }
    public int      getCurrentIndex()       { return currentIndex; }
    public int      getTotal()              { return session.size(); }
    public int      getScore()              { return score; }
    
    /** Checks if the player has answered all 10 questions. */
    public boolean  isGameOver()            { return currentIndex >= session.size(); }

    /**
     * SUBMIT ANSWER — Checks if the player's click was correct.
     * 
     * @param picked The index of the box they clicked (0 = A, 1 = B, etc.)
     * @return true if they got it right, false if wrong.
     */
    public boolean submitAnswer(int picked) {
        boolean correct = (picked == correctOptionIndex);
        if (correct) score++; // Add a point if they got it right!
        return correct;
    }

    /**
     * ADVANCE — Moves to the next question.
     * 
     * If we haven't reached the end, it bumps the index and shuffles 
     * the next question's answers. If we HAVE reached the end, it pushes 
     * the index past the limit so `isGameOver()` returns true.
     */
    public void advance() {
        if (currentIndex < session.size() - 1) {
            currentIndex++;
            prepareCurrentQuestion();
        } else {
            // We are at the last question. Move past it to trigger "Game Over".
            currentIndex++;
        }
    }
}