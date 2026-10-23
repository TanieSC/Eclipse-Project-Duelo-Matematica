package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class CreditsView {

    private final VBox root;
    private final Button backButton;

    public CreditsView() {
        Label titleLabel = ViewUtils.createPopupHeaderLabel("CREDITS");
        backButton = ViewUtils.createCloseButton("X");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
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

        root = new VBox(30, headerBox, grid);
        root.setPadding(new Insets(15, 40, 40, 40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
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