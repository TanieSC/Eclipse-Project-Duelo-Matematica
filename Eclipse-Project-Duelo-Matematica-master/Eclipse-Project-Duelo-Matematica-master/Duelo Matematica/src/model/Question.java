package model;

/**
 * QUESTION — the "blueprint" for a single quiz question.
 * 
 * WHAT DOES THIS CLASS DO?
 * Think of this class like a single index card in a flashcard deck. 
 * It is a pure data container. It does not know how to draw a button, 
 * how to calculate a score, or how to change screens. It just holds 
 * the information for one question.
 * 
 * The fields are marked as `final`. This is an important concept! It means 
 * that once the card is created, the information on it can NEVER be changed. 
 * It's locked in. This makes it very safe to pass around the app without 
 * worrying that some other part of the code will accidentally mess it up.
 */
public class Question {

    // A unique identifier for the question (like a serial number). 
    // Useful if we ever need to log errors or debug.
    private final String id;              
    
    // The category this question belongs to (e.g., "Calculus 1" or "Statistics").
    private final String topic;           
    
    // How hard the question is: "Easy", "Medium", "Hard", or "Very Hard".
    private final String difficulty;      
    
    // The actual question being asked. This may contain math formulas 
    // wrapped in dollar signs (like $x^2$), which the screen will turn into pictures.
    private final String text;            
    
    // The one and only correct answer.
    private final String correctAnswer;   
    
    // A list that holds the three incorrect answers.
    private final String[] wrongAnswers;  

    /**
     * CONSTRUCTOR — creates a brand new Question card.
     * 
     * When we want to build a new question, we have to provide all these 
     * details up front. We pass in the three wrong answers separately, 
     * and then this method packs them neatly into a single list (array).
     */
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
        
        // Take the three separate wrong answers and group them together 
        // so they are easier to manage later.
        this.wrongAnswers  = new String[] { wrong1, wrong2, wrong3 };
    }

    // --- GETTERS ---
    // These are like small windows that let other parts of the app 
    // (like the Game Screen) peek at the data. Because there are no 
    // "setter" methods, nobody can change the data once it's made.

    /** Returns the unique ID. */
    public String   getId()            { return id; }
    
    /** Returns the topic (e.g., "Calculus 1"). */
    public String   getTopic()         { return topic; }
    
    /** Returns the difficulty level (e.g., "Easy"). */
    public String   getDifficulty()    { return difficulty; }
    
    /** Returns the text of the question. */
    public String   getText()          { return text; }
    
    /** Returns the correct answer. */
    public String   getCorrectAnswer() { return correctAnswer; }
    
    /** Returns the list of the three wrong answers. */
    public String[] getWrongAnswers()  { return wrongAnswers; }
}