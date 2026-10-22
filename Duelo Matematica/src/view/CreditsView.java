package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * CREDITS VIEW — The visual layout for the Credits screen.
 */
public class CreditsView {

    private final VBox root;
    private final Button backButton;

    public CreditsView() {
        // Header
        Label titleLabel = ViewUtils.createPopupHeaderLabel("CREDITS");
        backButton = ViewUtils.createCloseButton("X");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // --- LEFT COLUMN ---
        VBox leftCol = new VBox(20);
        leftCol.getChildren().addAll(
            createSection("PROGRAMMERS", "CIRE", "AIYENNA"),
            createSection("UI / UX DESIGN", "HILARIE"),
            createSection("PLAYTESTER", "AIYENNA")
        );

        // --- RIGHT COLUMN ---
        VBox rightCol = new VBox(20);
        rightCol.getChildren().addAll(
            createSection("AUDIO DESIGN", "HILARIE"),
            createSection("ART & ANIMATION", "HILARIE"),
            createSection("PM & ARCHITECT", "CIRE")
        );

        // Use a GridPane to place the two columns side-by-side
        GridPane grid = new GridPane();
        grid.setHgap(50); // Space between the columns
        grid.setAlignment(Pos.CENTER);
        grid.add(leftCol, 0, 0);
        grid.add(rightCol, 1, 0);

        // Assemble the card
        root = new VBox(30, headerBox, grid);
        root.setPadding(new Insets(15, 40, 40, 40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #777777; -fx-border-width: 1px;");
    }

    /**
     * Helper method to create a block of text (Title + Names).
     */
    private VBox createSection(String role, String... names) {
        Label roleLabel = ViewUtils.createSubHeaderLabel(role);
        roleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #222222;"); // Matches image font

        VBox section = new VBox(2); // Small gap between role and names
        section.getChildren().add(roleLabel);

        for (String name : names) {
            Label nameLabel = ViewUtils.createBodyTextLabel(name); //new Label(name);
            nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
            section.getChildren().add(nameLabel);
        }

        return section;
    }

    public Node getRoot() { return root; }
    public Button getBackButton() { return backButton; }
}
