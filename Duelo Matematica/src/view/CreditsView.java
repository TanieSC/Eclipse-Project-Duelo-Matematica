package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class CreditsView {

    private final StackPane root;
    private final Button backButton;

    public CreditsView() {
        Label titleLabel = ViewUtils.createPopupHeaderLabel("CREDITS");

        // We only put the title in the header now, the button will float on top
        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        VBox leftCol = new VBox(20);
        leftCol.getChildren().addAll(
            createSection("PROGRAMMERS", "CIRE", "AIYENNA"),
            createSection("UI / UX DESIGN", "HILARIE"),
            createSection("PLAYTESTER", "AIYENNA")
        );

        VBox rightCol = new VBox(20);
        rightCol.getChildren().addAll(
            createSection("AUDIO DESIGN", "HILARIE"),
            createSection("ART & ANIMATION", "HILARIE"),
            createSection("PM & ARCHITECT", "CIRE")
        );

        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setAlignment(Pos.CENTER);
        grid.add(leftCol, 0, 0);
        grid.add(rightCol, 1, 0);

        // Group all the content into a VBox
        VBox contentBox = new VBox(30, headerBox, grid);
        // Add top padding to make room for the floating button
        contentBox.setPadding(new Insets(25, 40, 40, 40)); 
        contentBox.setAlignment(Pos.TOP_CENTER);

        // --- THE FIX: Use the exact same button creation as ConcedeView ---
        backButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_exit.png", 40, null);

        // --- ASSEMBLE EVERYTHING ---
        root = new StackPane(contentBox, backButton);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // --- ALIGN AND MARGIN EXACTLY LIKE CONCEDEVIEW ---
        StackPane.setAlignment(backButton, Pos.TOP_RIGHT);
        StackPane.setMargin(backButton, new Insets(-10, -4, 0, 0));
    }

    private VBox createSection(String role, String... names) {
        Label roleLabel = ViewUtils.createSubHeaderLabel(role);
        
        VBox section = new VBox(2); 
        section.getChildren().add(roleLabel);

        for (String name : names) {
            Label nameLabel = ViewUtils.createBodyTextLabel(name);
            section.getChildren().add(nameLabel);
        }
        return section;
    }

    public Node getRoot() { return root; }
    public Button getBackButton() { return backButton; }
}