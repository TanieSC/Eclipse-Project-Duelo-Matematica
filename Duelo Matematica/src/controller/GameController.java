package controller;

import model.GameService;
import model.Question;
import model.Session;
import view.GameView;
import view.GameSettingsView;
import javafx.stage.Stage;

/**
 * GAME CONTROLLER — the "brain" behind the Game screen.
 * 
 * WHAT DOES THIS CLASS DO?
 * It sits between the visual Game screen (GameView) and the game rules (GameService).
 * When the player clicks an answer, this class checks if it's right. 
 * When the player clicks "Next", this class calculates the next question and tells 
 * the screen to draw it. It also handles pausing the game and opening the Settings popup.
 */
public class GameController {

    // 1. The visual screen (buttons, labels, text boxes).
    private final GameView view;
    
    // 2. The rulebook and scorekeeper.
    private final GameService service;
    
    // 3. The "remote control" used to switch to different screens (like the main menu).
    private final Navigator nav;

    /**
     * CONSTRUCTOR — This runs once when the Game screen is first created.
     * 
     * @param view    The visual Game screen.
     * @param service The active game data (questions, score, etc.).
     * @param nav     The screen swapper.
     * @param resume  If TRUE, it means we are loading a paused game. 
     *                If FALSE, it means we are starting a brand new game.
     */
    public GameController(GameView view, GameService service, Navigator nav, boolean resume) {
        this.view = view;
        this.service = service;
        this.nav = nav;

        // --- WIRING THE BUTTONS ---
        // We tell the view: "When the user does X, call this specific method."
        // The `this::methodName` syntax is just a shortcut way to link a button click to a method.
        
        view.setOnAnswerPicked(this::handleAnswer);
        view.setOnNext(this::handleNext);
        view.setOnPlayAgain(this::handlePlayAgain);
        view.setOnBack(this::handleBack);
        view.setOnSettings(this::openSettings);

        // --- STARTING THE GAME ---
        if (resume) {
            // If we are resuming, don't shuffle the cards again. Just draw 
            // the exact question the player left off on.
            renderCurrentQuestion();
        } else {
            // If it's a new game, shuffle the questions, reset the score, and draw Question 1.
            startGame();
        }
    }

    /**
     * START GAME — Shuffles the deck, resets the score to zero, 
     * and draws the first question.
     */
    private void startGame() {
        service.startNewGame();
        renderCurrentQuestion();
    }

    /**
     * RENDER CURRENT QUESTION — Translates data from the GameService 
     * into visual elements for the GameView.
     * 
     * It grabs the question text, the 4 answers, the difficulty, and the score, 
     * and hands them over to the view so the screen can update.
     */
    private void renderCurrentQuestion() {
        Question q = service.getCurrentQuestion();
        view.showQuestion(
            q.getText(),
            service.getCurrentOptions(),
            service.getCorrectOptionIndex(),
            service.getCategory(),
            q.getDifficulty(),
            service.getCurrentIndex() + 1, // +1 because code counts from 0, but humans count from 1
            service.getTotal(),
            service.getScore()
        );
    }

    /**
     * HANDLE ANSWER — Runs when the player clicks one of the four answer boxes.
     * 
     * 1. It tells the GameService which answer was picked (to check if it's correct and add a point).
     * 2. It tells the View to reveal the "NEXT" button so the player can move on.
     */
    private void handleAnswer(int pickedIndex) {
        service.submitAnswer(pickedIndex);
        view.showNextButton();
    }

    /**
     * HANDLE NEXT — Runs when the player clicks the "NEXT" button.
     * 
     * It tells the GameService to move to the next question.
     * Then it checks: "Is the game over?"
     *   - If YES: Delete the paused game (no need to save a finished game) and show "PLAY AGAIN".
     *   - If NO: Draw the next question on the screen.
     */
    private void handleNext() {
        service.advance(); // Tells the service to move forward
        
        if (service.isGameOver()) {
            Session.clearSavedGame(); 
            view.showPlayAgainButton();
        } else {
            renderCurrentQuestion();
        }
    }

    /**
     * HANDLE PLAY AGAIN — Runs when the player clicks "PLAY AGAIN" 
     * at the end of a round. It simply restarts the game from scratch.
     */
    private void handlePlayAgain() {
        startGame();
    }

    /**
     * HANDLE BACK — Runs when the player clicks the "X" button to quit the game.
     * 
     * THE PAUSE/RESUME LOGIC:
     * If the game is NOT over, we save it to the Session. This allows the player 
     * to log out or go back to the menu, and pick up exactly where they left off later.
     * If the game IS already over, we just delete any saved data.
     * Finally, we tell the Navigator to take us back to the main menu.
     */
    private void handleBack() {
        if (!service.isGameOver()) {
            Session.setSavedGame(service); // Save progress
        } else {
            Session.clearSavedGame();      // Clear progress
        }
        nav.goToMenu(); // Go back to the main menu
    }

    /**
     * OPEN SETTINGS — Runs when the player clicks the Gear/Settings icon.
     * 
     * This opens a completely separate mini-window (a popup) on top of the game.
     * We need to know the main window (the "owner") so the popup knows where to center itself.
     */
    private void openSettings() {
        // Grab the main JavaFX window from the view's scene.
        Stage owner = (Stage) view.getRoot().getScene().getWindow();
        
        // Create the popup screen and its controller.
        GameSettingsView v = new GameSettingsView(owner);
        new GameSettingsController(v);
        
        // Show the popup and pause the game code until it closes.
        v.show(); 
    }
}