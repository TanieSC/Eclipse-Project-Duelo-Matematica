package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.LeaderBoardModel;
import model.LeaderBoardModel.LeaderboardEntry;

import java.util.List;

public class LeaderBoardView {

    private final StackPane root;
    private final VBox entryListVBox;
    private final Button backButton;
    // REMOVED: private final Button gearButton;

    public LeaderBoardView(LeaderBoardModel model) {
        root = new StackPane();
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

        // THE EXIT BUTTON
        backButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_exit.png", 40, null);

        // ASSEMBLE EVERYTHING (Removed gearButton from here)
        root.getChildren().addAll(leaderboardCard, backButton);
        
        // Center the main card
        StackPane.setAlignment(leaderboardCard, Pos.CENTER);
        
        // Align and position the exit button exactly like ConcedeView
        StackPane.setAlignment(backButton, Pos.TOP_RIGHT);
        StackPane.setMargin(backButton, new Insets(-10, -4, 0, 0)); 

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

        Label rankHeader = ViewUtils.createSubHeaderLabel("RANK");//new Label("RANK");
        rankHeader.setPrefWidth(90);
        rankHeader.setAlignment(Pos.CENTER_LEFT);
        //rankHeader.setFont(ViewUtils.getCustomFont(15));

        Label userHeader = ViewUtils.createSubHeaderLabel("USER");// new Label("USER");
        userHeader.setPrefWidth(160);
        userHeader.setAlignment(Pos.CENTER);
       // userHeader.setFont(ViewUtils.getCustomFont(15));

        Label tierHeader = ViewUtils.createSubHeaderLabel("TIER");// new Label("TIER");
        tierHeader.setPrefWidth(110);
        tierHeader.setAlignment(Pos.CENTER_RIGHT);
       // tierHeader.setFont(ViewUtils.getCustomFont(15));

        header.getChildren().addAll(rankHeader, userHeader, tierHeader);
        return header;
    }

    private HBox createDataRow(String rankText, String userText, String tierText) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(2, 10, 2, 10));

        Label rankLbl = ViewUtils.createBodyTextLabel(rankText);// new Label(rankText);
        rankLbl.setPrefWidth(90);
        rankLbl.setAlignment(Pos.CENTER_LEFT);
       // rankLbl.setFont(ViewUtils.getCustomFont(15));

        Label userLbl =  ViewUtils.createBodyTextLabel(userText);//new Label(userText);
        userLbl.setPrefWidth(160);
        userLbl.setAlignment(Pos.CENTER);
       // userLbl.setFont(ViewUtils.getCustomFont(15));

        Label tierLbl =  ViewUtils.createBodyTextLabel(tierText);//new Label(tierText);
        tierLbl.setPrefWidth(110);
        tierLbl.setAlignment(Pos.CENTER_RIGHT);
       // tierLbl.setFont(ViewUtils.getCustomFont(15));

        row.getChildren().addAll(rankLbl, userLbl, tierLbl);
        return row;
    }

    // REMOVED: createGearButton() method

    public Node getRoot() { return root; }
    public Button getBackButton() { return backButton; }
    // REMOVED: public Button getGearButton() { return gearButton; }
}