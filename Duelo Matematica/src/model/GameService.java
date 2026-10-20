package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GAME SERVICE — the "referee" of one game round.
 *
 * Holds all the state of a play-through: the filtered question pool,
 * the shuffled 10-question session, which question is current, the score,
 * and the shuffled option order for the current question.
 *
 * NEW: it also remembers which category it was created with. That's what
 * lets a paused game stay self-contained — Session stores just the
 * GameService, and everything needed to resume is inside it.
 *
 * No JavaFX. Could be unit-tested with a few asserts.
 */
public class GameService {

    public static final int QUESTIONS_PER_GAME = 10;

    private final String category;          // e.g. "Calculus", "All", …
    private final List<Question> pool;      // already filtered
    private List<Question> session;         // this round's shuffled 10
    private int currentIndex;
    private int score;
    private String[] currentOptions;        // shuffled 4 options
    private int correctOptionIndex;         // 0..3, where correct sits

    public GameService(List<Question> allQuestions, String category) {
        // Remember the category so resume works even after long delays.
        this.category = (category == null) ? "All" : category;

        if (category == null || category.equalsIgnoreCase("All")) {
            this.pool = new ArrayList<>(allQuestions);
        } else if (category.equalsIgnoreCase("Calculus")) {
            this.pool = allQuestions.stream()
                .filter(q -> q.getTopic().startsWith("Calculus"))
                .collect(Collectors.toList());
        } else {
            this.pool = allQuestions.stream()
                .filter(q -> q.getTopic().equalsIgnoreCase(category))
                .collect(Collectors.toList());
        }
        if (this.pool.isEmpty()) this.pool.addAll(allQuestions);
    }

    /** Category label — used by the game header and the resume card. */
    public String getCategory() { return category; }

    /** Shuffle pool → take 10 → reset score/index → prepare Q1. */
    public void startNewGame() {
        List<Question> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled);
        int take = Math.min(QUESTIONS_PER_GAME, shuffled.size());
        session = new ArrayList<>(shuffled.subList(0, take));
        currentIndex = 0;
        score = 0;
        prepareCurrentQuestion();
    }

    /** Shuffle the 4 options for the current question. */
    private void prepareCurrentQuestion() {
        Question q = session.get(currentIndex);
        List<Integer> order = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(order);
        currentOptions = new String[4];
        for (int i = 0; i < 4; i++) {
            int original = order.get(i);
            if (original == 0) {
                correctOptionIndex = i;
                currentOptions[i] = q.getCorrectAnswer();
            } else {
                currentOptions[i] = q.getWrongAnswers()[original - 1];
            }
        }
    }

    public Question getCurrentQuestion()    { return session.get(currentIndex); }
    public String[] getCurrentOptions()     { return currentOptions; }
    public int      getCorrectOptionIndex() { return correctOptionIndex; }
    public int      getCurrentIndex()       { return currentIndex; }
    public int      getTotal()              { return session.size(); }
    public int      getScore()              { return score; }
    public boolean  isGameOver()            { return currentIndex >= session.size(); }

    public boolean submitAnswer(int picked) {
        boolean correct = (picked == correctOptionIndex);
        if (correct) score++;
        return correct;
    }

    public void advance() {
        if (currentIndex < session.size() - 1) {
            currentIndex++;
            prepareCurrentQuestion();
        } else {
            currentIndex++;
        }
    }
}