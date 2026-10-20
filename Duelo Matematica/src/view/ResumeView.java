package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.GameService;

/**
 * RESUME VIEW — the "you have a game in progress" card.
 *
 * Shown right after a successful login (or when the player clicks the
 * "Logged in: X" button on the menu) IF a paused game exists. It lets
 * the player choose:
 *
 *   [RESUME GAME]         → jump back into the paused round, same
 *                            question, same score.
 *   [CHOOSE NEW CATEGORY] → go to the category picker; the paused game
 *                            stays saved until the player starts a new
 *                            one (which overwrites it).
 *   [X]                    → back to the menu; the paused game stays
 *                            saved for later.
 *
 * WHAT THE VIEW SHOWS
 *   A small summary line — "Calculus | Question 5/10 | Score: 3" — so
 *   the player knows what they're resuming. That info is pulled from
 *   the GameService passed in by the controller.
 *
 * The view exposes its two buttons so ResumeController can wire them.
 * No navigation logic lives here.
 */
public class ResumeView {

    private final VBox root;
    private final Button resumeButton;
    private final Button newCategoryButton;
    private final Button backButton;

    /**
     * @param savedGame the paused GameService — used to print the summary.
     *                  Never null (the controller checks hasSavedGame()
     *                  before constructing this view).
     */
    public ResumeView(GameService savedGame) {
        // ---- Header: title + X ----
        Label titleLabel = new Label("RESUME GAME?");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );

        BorderPane header = new BorderPane();
        header.setCenter(titleLabel);
        header.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ---- Summary line ----
        String summary = savedGame.getCategory()
                       + "  |  Question " + (savedGame.getCurrentIndex() + 1)
                       + "/" + savedGame.getTotal()
                       + "  |  Score: " + savedGame.getScore();
        Label summaryLabel = new Label(summary);
        summaryLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");
        summaryLabel.setWrapText(true);

        // ---- Action buttons ----
        resumeButton      = bigButton("RESUME GAME",      "Continue where you left off");
        newCategoryButton = bigButton("CHOOSE NEW CATEGORY", "Start a fresh round");

        VBox buttons = new VBox(12, resumeButton, newCategoryButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setFillWidth(true);

        // ---- Assemble card ----
        root = new VBox(20, header, summaryLabel, buttons);
        root.setPadding(new Insets(20, 30, 25, 30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle(
            "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
            "-fx-border-width: 1px;"
        );
    }

    /** Build one wide button with a title and a smaller subtitle. */
    private Button bigButton(String title, String subtitle) {
        Button b = new Button(title + "\n" + subtitle);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setMinHeight(60);
        b.setStyle(
            "-fx-font-size: 14px; -fx-font-weight: bold;" +
            "-fx-text-alignment: center;" +
            "-fx-border-color: #000; -fx-border-width: 2px;" +
            "-fx-background-color: #f9f9f9; -fx-cursor: hand;"
        );
        return b;
    }

    // ---- Accessors used by ResumeController ----
    public Node   getRoot()              { return root; }
    public Button getResumeButton()      { return resumeButton; }
    public Button getNewCategoryButton() { return newCategoryButton; }
    public Button getBackButton()        { return backButton; }
}
