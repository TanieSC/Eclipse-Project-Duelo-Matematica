package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import model.UserProfileModel;

public class UserProfileView {

    // CHANGED: Root is now a StackPane so the button can float
    private final StackPane root;
    private final Button backButton;

    public UserProfileView(UserProfileModel profile) {
        // ==========================================
        // 1. THE HEADER (Title Only)
        // ==========================================
        Label titleLabel = ViewUtils.createPopupHeaderLabel("PROFILE");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. THE STATS GRID
        // ==========================================
        GridPane grid = ViewUtils.createStandardFormGrid(15, 15);//new GridPane();
        grid.setHgap(80); 
       // grid.setVgap(25); 
        grid.setAlignment(Pos.CENTER); // Centered inside the content box

        //String labelStyle = "-fx-text-fill: #222222;";
        //String valueStyle = "-fx-text-fill: #000000;";

        Label userLabel = ViewUtils.createBodyTextLabel("USER:");//new Label("USER:");
        //userLabel.setFont(ViewUtils.getCustomFont(20));
        //userLabel.setStyle(labelStyle);
        Label userValue = ViewUtils.createBodyTextLabel(profile.getUsername().toUpperCase());//new Label(profile.getUsername().toUpperCase());
        //userValue.setFont(ViewUtils.getCustomFont(20));
        //userValue.setStyle(valueStyle);
        grid.add(userLabel, 0, 0);
        grid.add(userValue, 1, 0);

        Label tierLabel = ViewUtils.createBodyTextLabel("TIER:");//new Label("TIER:");
        //tierLabel.setFont(ViewUtils.getCustomFont(20));
        //tierLabel.setStyle(labelStyle);
        Label tierValue = ViewUtils.createBodyTextLabel(profile.getTier().toUpperCase());//new Label(profile.getTier().toUpperCase());
        //tierValue.setFont(ViewUtils.getCustomFont(20));
       // tierValue.setStyle(valueStyle);
        grid.add(tierLabel, 0, 1);
        grid.add(tierValue, 1, 1);

        Label eloLabel = ViewUtils.createBodyTextLabel("ELO:");//new Label("ELO:");
        //eloLabel.setFont(ViewUtils.getCustomFont(20));
        //eloLabel.setStyle(labelStyle);
        Label eloValue = ViewUtils.createBodyTextLabel(String.valueOf(profile.getElo()));//new Label(String.valueOf(profile.getElo()));
        //eloValue.setFont(ViewUtils.getCustomFont(20));
        //eloValue.setStyle(valueStyle);
        grid.add(eloLabel, 0, 2);
        grid.add(eloValue, 1, 2);

        // ==========================================
        // 3. GROUP CONTENT
        // ==========================================
        VBox contentBox = new VBox(30, headerBox, grid);
        contentBox.setAlignment(Pos.TOP_CENTER);
        
        // Add top padding to make room for the floating exit button
        contentBox.setPadding(new Insets(25, 30, 30, 30)); 

        // ==========================================
        // 4. THE EXIT BUTTON (Same as ConcedeView)
        // ==========================================
        backButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_exit.png", 40, null);

        // ==========================================
        // 5. ASSEMBLE EVERYTHING
        // ==========================================
        root = new StackPane(contentBox, backButton);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Align and position the button exactly like ConcedeView
        StackPane.setAlignment(backButton, Pos.TOP_RIGHT);
        StackPane.setMargin(backButton, new Insets(-10, -4, 0, 0)); 
    }

    public Node getRoot()          { return root; }
    public Button getBackButton()  { return backButton; }
}