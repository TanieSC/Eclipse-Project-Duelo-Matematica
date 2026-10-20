package model;

/**
 * QUESTION — one quiz card (pure data, no JavaFX).
 *
 * Holds a prompt, one correct answer, and three wrong answers. Every
 * string may contain LaTeX wrapped in $...$ markers; the view will turn
 * those markers into rendered images later.
 *
 * It also carries a 'topic' (used by the category filter) and a
 * 'difficulty' string ("Easy" / "Medium" / "Hard" / "Very Hard") which
 * the game header shows as a color-coded badge.
 *
 * Fields are final — once built, never changes. Safe to share.
 */
public class Question {

    private final String id;              // "1", "2", … — for debugging
    private final String topic;           // "Calculus 1", "Statistics", …
    private final String difficulty;      // "Easy" / "Medium" / "Hard" / "Very Hard"
    private final String text;            // prompt, may contain $...$
    private final String correctAnswer;   // one right answer, may contain $...$
    private final String[] wrongAnswers;  // always length 3

    public Question(String id,
                    String topic,
                    String difficulty,
                    String text,
                    String correctAnswer,
                    String wrong1,
                    String wrong2,
                    String wrong3) {
        this.id            = id;
        this.topic         = topic;
        this.difficulty    = difficulty;
        this.text          = text;
        this.correctAnswer = correctAnswer;
        this.wrongAnswers  = new String[] { wrong1, wrong2, wrong3 };
    }

    public String   getId()            { return id; }
    public String   getTopic()         { return topic; }
    public String   getDifficulty()    { return difficulty; }
    public String   getText()          { return text; }
    public String   getCorrectAnswer() { return correctAnswer; }
    public String[] getWrongAnswers()  { return wrongAnswers; }
}