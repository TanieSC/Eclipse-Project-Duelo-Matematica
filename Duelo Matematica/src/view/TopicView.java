package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * TOPIC VIEW — the "pick a category" card.
 *
 * Shown right after a successful login. Three specific category buttons
 * (CALCULUS, STATISTICS, SYMBOLIC LOGIC) plus "ALL TOPICS" for a random
 * mix, plus an X to cancel back to the menu.
 *
 * The view knows nothing about filtering — it just exposes each button.
 * The controller wires them to navigator.goToGame("…"). Consistent with
 * every other view in the app.
 */
public class TopicView {

    private final VBox root;
    private final Button calculusButton;
    private final Button statisticsButton;
    private final Button logicButton;
    private final Button allButton;
    private final Button backButton;

    public TopicView() {
        Label titleLabel = new Label("CHOOSE A CATEGORY");
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

        calculusButton   = topicButton("CALCULUS",       "Calculus 1, 2 and 3");
        statisticsButton = topicButton("STATISTICS",     "Probability and inference");
        logicButton      = topicButton("SYMBOLIC LOGIC", "Propositional and predicate");
        allButton        = topicButton("ALL TOPICS",     "Random mix from every subject");

        VBox topics = new VBox(12, calculusButton, statisticsButton, logicButton, allButton);
        topics.setAlignment(Pos.CENTER);
        topics.setFillWidth(true);

        root = new VBox(20, header, topics);
        root.setPadding(new Insets(20, 30, 25, 30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle(
            "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
            "-fx-border-width: 1px;"
        );
    }

    /** Build one wide button with a title and subtitle in the label text. */
    private Button topicButton(String title, String subtitle) {
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

    // ---- Accessors the controller uses to wire actions ----
    public Node   getRoot()             { return root; }
    public Button getCalculusButton()   { return calculusButton; }
    public Button getStatisticsButton() { return statisticsButton; }
    public Button getLogicButton()      { return logicButton; }
    public Button getAllButton()        { return allButton; }
    public Button getBackButton()       { return backButton; }
}
