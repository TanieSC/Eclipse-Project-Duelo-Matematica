package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import model.UserProfileModel;

/**
 * USER PROFILE VIEW — The inline visual layout for the player's profile.
 * 
 * Matches the design image: Header with title and X button, and a 
 * 2-column grid showing USER, TIER, and ELO.
 */
public class UserProfileView {

    private final VBox root;
    private final Button backButton;

    public UserProfileView(UserProfileModel profile) {
        // ==========================================
        // 1. HEADER (Title + Close Button)
        // ==========================================
        Label titleLabel = ViewUtils.createHeaderLabel("PROFILE");
        backButton = ViewUtils.createCloseButton("X");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. STATS GRID (USER, TIER, ELO)
        // ==========================================
        GridPane grid = new GridPane();
        grid.setHgap(80); // Space between label and value
        grid.setVgap(25); // Space between rows
        grid.setAlignment(Pos.CENTER_LEFT);

        // Font styles
        String labelStyle = "-fx-font-size: 20px; -fx-text-fill: #222222;";
        String valueStyle = "-fx-font-size: 20px; -fx-text-fill: #000000;";

        // Row 1: USER
        Label userLabel = new Label("USER:");
        userLabel.setStyle(labelStyle);
        Label userValue = new Label(profile.getUsername().toUpperCase());
        userValue.setStyle(valueStyle);
        grid.add(userLabel, 0, 0);
        grid.add(userValue, 1, 0);

        // Row 2: TIER
        Label tierLabel = new Label("TIER:");
        tierLabel.setStyle(labelStyle);
        Label tierValue = new Label(profile.getTier().toUpperCase());
        tierValue.setStyle(valueStyle);
        grid.add(tierLabel, 0, 1);
        grid.add(tierValue, 1, 1);

        // Row 3: ELO
        Label eloLabel = new Label("ELO:");
        eloLabel.setStyle(labelStyle);
        Label eloValue = new Label(String.valueOf(profile.getElo()));
        eloValue.setStyle(valueStyle);
        grid.add(eloLabel, 0, 2);
        grid.add(eloValue, 1, 2);

        // ==========================================
        // 3. ASSEMBLE CARD
        // ==========================================
        root = new VBox(30, headerBox, grid);
        root.setPadding(new Insets(15, 30, 30, 30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle(
                "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
                "-fx-border-width: 1px;"
            );
    }

    public Node getRoot()          { return root; }
    public Button getBackButton()  { return backButton; }
}