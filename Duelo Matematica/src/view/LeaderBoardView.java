package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.LeaderBoardModel;
import model.LeaderBoardModel.LeaderboardEntry;

import java.util.List;

public class LeaderBoardView {

    private final BorderPane root;
    private final VBox entryListVBox;
    private final Button backButton;
    private final Button gearButton;

    public LeaderBoardView(LeaderBoardModel model) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: transparent;");
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Label titleLabel = ViewUtils.createPopupHeaderLabel("LEADERBOARD");
        
        HBox headerRow = createHeaderRow();

        entryListVBox = new VBox(12);
        entryListVBox.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(entryListVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-viewport-background: transparent; -fx-padding: 0 0 0 10px;");

        scrollPane.setScaleX(-1);
        entryListVBox.setScaleX(-1);

        VBox leaderboardCard = new VBox(18, titleLabel, headerRow, scrollPane);
        leaderboardCard.setAlignment(Pos.TOP_CENTER);
        leaderboardCard.setStyle("-fx-padding: 25px 30px;");
        leaderboardCard.setPrefSize(440, 360);
        leaderboardCard.setMaxSize(440, 360);

        gearButton = createGearButton();

        backButton = new Button("BACK");
        backButton.setPrefSize(130, 42);
        backButton.setFont(ViewUtils.getCustomFont(16));
        backButton.setStyle("-fx-background-color: #d0d0d0; -fx-text-fill: #000000; -fx-cursor: hand;");

        HBox leftContainer = new HBox(gearButton);
        leftContainer.setAlignment(Pos.BOTTOM_LEFT);

        HBox centerContainer = new HBox(backButton);
        centerContainer.setAlignment(Pos.BOTTOM_CENTER);

        Region rightSpacer = new Region();
        rightSpacer.setPrefWidth(42);

        BorderPane bottomBar = new BorderPane();
        bottomBar.setLeft(leftContainer);
        bottomBar.setCenter(centerContainer);
        bottomBar.setRight(rightSpacer);
        bottomBar.setPadding(new Insets(0, 15, 15, 15));

        VBox centerLayout = new VBox(leaderboardCard);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setPadding(new Insets(20, 20, 0, 20));

        root.setCenter(centerLayout);
        root.setBottom(bottomBar);

        updateLeaderboard(model.getEntries());
    }

    public void updateLeaderboard(List<LeaderboardEntry> entries) {
        entryListVBox.getChildren().clear();
        if (entries == null) return;

        for (LeaderboardEntry entry : entries) {
            HBox row = createDataRow(entry.getRank() + ".", formatName(entry.getUser()), entry.getTier());
            entryListVBox.getChildren().add(row);
        }
    }

    private String formatName(String name) {
        if (name == null) return "";
        if (name.length() > 10) return name.substring(0, 10) + "...";
        return name;
    }

    private HBox createHeaderRow() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 10, 0, 10));

        Label rankHeader = new Label("RANK");
        rankHeader.setPrefWidth(90);
        rankHeader.setAlignment(Pos.CENTER_LEFT);
        rankHeader.setFont(ViewUtils.getCustomFont(15));

        Label userHeader = new Label("USER");
        userHeader.setPrefWidth(160);
        userHeader.setAlignment(Pos.CENTER);
        userHeader.setFont(ViewUtils.getCustomFont(15));

        Label tierHeader = new Label("TIER");
        tierHeader.setPrefWidth(110);
        tierHeader.setAlignment(Pos.CENTER_RIGHT);
        tierHeader.setFont(ViewUtils.getCustomFont(15));

        header.getChildren().addAll(rankHeader, userHeader, tierHeader);
        return header;
    }

    private HBox createDataRow(String rankText, String userText, String tierText) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(2, 10, 2, 10));

        Label rankLbl = new Label(rankText);
        rankLbl.setPrefWidth(90);
        rankLbl.setAlignment(Pos.CENTER_LEFT);
        rankLbl.setFont(ViewUtils.getCustomFont(15));

        Label userLbl = new Label(userText);
        userLbl.setPrefWidth(160);
        userLbl.setAlignment(Pos.CENTER);
        userLbl.setFont(ViewUtils.getCustomFont(15));

        Label tierLbl = new Label(tierText);
        tierLbl.setPrefWidth(110);
        tierLbl.setAlignment(Pos.CENTER_RIGHT);
        tierLbl.setFont(ViewUtils.getCustomFont(15));

        row.getChildren().addAll(rankLbl, userLbl, tierLbl);
        return row;
    }

    private Button createGearButton() {
        Button gearBtn = new Button("\u2699");
        gearBtn.setPrefSize(42, 42);
        gearBtn.setFont(ViewUtils.getCustomFont(20));
        gearBtn.setStyle("-fx-border-color: #000000; -fx-border-width: 2px; -fx-background-color: #ffffff; -fx-cursor: hand;");
        return gearBtn;
    }

    public Node getRoot() { return root; }
    public Button getBackButton() { return backButton; }
    public Button getGearButton() { return gearButton; }
}