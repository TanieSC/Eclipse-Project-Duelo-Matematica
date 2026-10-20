package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * GAME SETTINGS VIEW — a MODAL POPUP version of the settings card.
 * 
 * This popup is explicitly centered over the main application window.
 */
public class GameSettingsView {

    private final Stage stage;
    private final Button backButton;

    public GameSettingsView(Stage owner) {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("Game Settings");
        stage.setResizable(false);

        Label titleLabel = new Label("GAME SETTINGS");
        titleLabel.setStyle("-fx-font-size: 16px;");

        backButton = new Button("X");
        backButton.setStyle(
            "-fx-background-color: transparent; -fx-font-weight: bold;" +
            "-fx-font-size: 14px; -fx-cursor: hand;"
        );

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        Label placeholder = new Label("Settings coming soon…");
        placeholder.setStyle("-fx-font-size: 13px;");

        VBox root = new VBox(20, headerBox, placeholder);
        root.setPadding(new Insets(15, 20, 20, 20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle(
            "-fx-background-color: #f4f4f4; -fx-border-color: #777777;" +
            "-fx-border-width: 1px;"
        );

        stage.setScene(new Scene(root, 300, 200));

        // ============================================================
        // THE CENTERING LOGIC
        // ============================================================
        // setOnShown fires right before the window appears on screen. 
        // At this point, both the owner and the popup have calculated 
        // their true widths and heights.
        stage.setOnShown(event -> {
            if (owner != null) {
                // Calculate the exact center X and Y based on the owner's position
                double centerX = owner.getX() + (owner.getWidth() / 2) - (stage.getWidth() / 2);
                double centerY = owner.getY() + (owner.getHeight() / 2) - (stage.getHeight() / 2);
                stage.setX(centerX);
                stage.setY(centerY);
            }
        });
    }

    public Button getBackButton() { return backButton; }
    public void show()            { stage.showAndWait(); }
    public void close()           { stage.close(); }
}