package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import model.UserProfileModel;

public class UserProfileView {

    private final VBox root;
    private final Button backButton;

    public UserProfileView(UserProfileModel profile) {
        // Standardized Header
        Label titleLabel = ViewUtils.createPopupHeaderLabel("PROFILE");
        backButton = ViewUtils.createCloseButton("X");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setHgap(80); 
        grid.setVgap(25); 
        grid.setAlignment(Pos.CENTER_LEFT);

        // Use Custom Font
        String labelStyle = "-fx-text-fill: #222222;";
        String valueStyle = "-fx-text-fill: #000000;";

        Label userLabel = new Label("USER:");
        userLabel.setFont(ViewUtils.getCustomFont(20));
        userLabel.setStyle(labelStyle);
        Label userValue = new Label(profile.getUsername().toUpperCase());
        userValue.setFont(ViewUtils.getCustomFont(20));
        userValue.setStyle(valueStyle);
        grid.add(userLabel, 0, 0);
        grid.add(userValue, 1, 0);

        Label tierLabel = new Label("TIER:");
        tierLabel.setFont(ViewUtils.getCustomFont(20));
        tierLabel.setStyle(labelStyle);
        Label tierValue = new Label(profile.getTier().toUpperCase());
        tierValue.setFont(ViewUtils.getCustomFont(20));
        tierValue.setStyle(valueStyle);
        grid.add(tierLabel, 0, 1);
        grid.add(tierValue, 1, 1);

        Label eloLabel = new Label("ELO:");
        eloLabel.setFont(ViewUtils.getCustomFont(20));
        eloLabel.setStyle(labelStyle);
        Label eloValue = new Label(String.valueOf(profile.getElo()));
        eloValue.setFont(ViewUtils.getCustomFont(20));
        eloValue.setStyle(valueStyle);
        grid.add(eloLabel, 0, 2);
        grid.add(eloValue, 1, 2);

        root = new VBox(30, headerBox, grid);
        root.setPadding(new Insets(15, 30, 30, 30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;"); // Transparent for wood background
    }

    public Node getRoot()          { return root; }
    public Button getBackButton()  { return backButton; }
}