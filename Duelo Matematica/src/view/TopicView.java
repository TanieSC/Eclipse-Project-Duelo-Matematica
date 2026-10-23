package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TopicView {

    private final VBox root;
    private final Button calculusButton;
    private final Button statisticsButton;
    private final Button logicButton;
    private final Button allButton;
    private final Button backButton;

    public TopicView() {
        Label titleLabel = ViewUtils.createPopupHeaderLabel("CHOOSE A CATEGORY");
        backButton = ViewUtils.createCloseButton("X");

        BorderPane header = new BorderPane();
        header.setCenter(titleLabel);
        header.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        calculusButton   = ViewUtils.createSpriteButton("/images/wood_button.png", "CALCULUS", null);
        statisticsButton = ViewUtils.createSpriteButton("/images/wood_button.png", "STATISTICS", null);
        logicButton      = ViewUtils.createSpriteButton("/images/wood_button.png", "SYMBOLIC LOGIC", null);
        allButton        = ViewUtils.createSpriteButton("/images/wood_button.png", "ALL TOPICS", null);

        VBox topics = new VBox(12, calculusButton, statisticsButton, logicButton, allButton);
        topics.setAlignment(Pos.CENTER);
        topics.setFillWidth(true);

        root = new VBox(20, header, topics);
        root.setPadding(new Insets(20, 30, 25, 30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
    }

    public Node   getRoot()             { return root; }
    public Button getCalculusButton()   { return calculusButton; }
    public Button getStatisticsButton() { return statisticsButton; }
    public Button getLogicButton()      { return logicButton; }
    public Button getAllButton()        { return allButton; }
    public Button getBackButton()       { return backButton; }
}