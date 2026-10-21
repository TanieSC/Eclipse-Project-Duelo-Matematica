package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.UserProfileModel;

/**
 * USER PROFILE VIEW — The visual layout for the player's profile.
 * 
 * WHAT DOES THIS CLASS DO?
 * It draws a "Profile Card" displaying the user's avatar, their Username, 
 * their Tier, and their ELO score.
 * 
 * It now includes a standard HEADER with a Title and a Close (X) button 
 * at the top of the card, consistent with the Login and Register views.
 */
public class UserProfileView {

    private final StackPane root;
    private final Button backButton;

    public UserProfileView(UserProfileModel profile) {
        // ==========================================
        // 1. THE HEADER (Title + Close Button)
        // ==========================================
        Label titleLabel = new Label("PLAYER PROFILE");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. AVATAR & STATS
        // ==========================================
        // Avatar Circle
        String initial = profile.getUsername().substring(0, 1).toUpperCase();
        Label avatarText = new Label(initial);
        avatarText.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        avatarText.setTextFill(Color.WHITE);

        StackPane avatar = new StackPane(avatarText);
        avatar.setStyle("-fx-background-color: #444444; -fx-background-radius: 50%;");
        avatar.setPrefSize(100, 100);
        avatar.setMaxSize(100, 100);

        // Username
        Label nameLabel = new Label(profile.getUsername());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Stats (Tier & ELO)
        Label tierLabel = new Label("TIER: " + profile.getTier().toUpperCase());
        tierLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555555; -fx-font-weight: bold;");

        Label eloLabel = new Label("ELO: " + profile.getElo());
        eloLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555555; -fx-font-weight: bold;");

        // ==========================================
        // 3. ASSEMBLING THE CARD
        // ==========================================
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15, 40, 30, 40));
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        card.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #777777; -fx-border-width: 1px;");

        // Add the header at the top, then the avatar and stats
        card.getChildren().addAll(headerBox, avatar, nameLabel, tierLabel, eloLabel);

        // ==========================================
        // 4. THE DARK OVERLAY BACKGROUND
        // ==========================================
        Region darkOverlay = new Region();
        darkOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        root = new StackPane();
        root.getChildren().addAll(darkOverlay, card);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    public Node getRoot()          { return root; }
    public Button getBackButton()  { return backButton; }
}