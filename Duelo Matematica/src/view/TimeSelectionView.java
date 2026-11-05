package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * TIME SELECTION VIEW — choose match duration before network 1v1 search.
 */
public class TimeSelectionView {

    private final StackPane root;
    private final Button btn10;
    private final Button btn25;
    private final Button btn30;
    private final Button backButton;

    public TimeSelectionView() {
        Label headerLabel = ViewUtils.createPopupHeaderLabel("MATCH DURATION");

        Label hint = ViewUtils.createBodyTextLabel("Pick how long the match should last.");

        btn10 = ViewUtils.createSpriteButton("/buttons/btn.png", "10 MIN", null);
        btn25 = ViewUtils.createSpriteButton("/buttons/btn.png", "25 MIN", null);
        btn30 = ViewUtils.createSpriteButton("/buttons/btn.png", "30 MIN", null);

        HBox buttonBox = new HBox(15, btn10, btn25, btn30);
        buttonBox.setAlignment(Pos.CENTER);

        VBox contentBox = new VBox(20, headerLabel, hint, buttonBox);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(25, 40, 40, 40));

        backButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_exit.png", 40, null);

        root = new StackPane(contentBox, backButton);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        StackPane.setAlignment(backButton, Pos.TOP_RIGHT);
        StackPane.setMargin(backButton, new Insets(-10, -4, 0, 0));
    }

    public Node getRoot() { return root; }
    public Button get10MinButton() { return btn10; }
    public Button get25MinButton() { return btn25; }
    public Button get30MinButton() { return btn30; }
    public Button getBackButton()  { return backButton; }
}
