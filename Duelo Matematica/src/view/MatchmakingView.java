package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * MATCHMAKING VIEW — "Searching for opponent…" popup with Cancel button.
 */
public class MatchmakingView {

    private final StackPane root;
    private final Label statusLabel;
    private final Label detailLabel;
    private final Button cancelButton;

    public MatchmakingView(int durationSeconds) {
        Label titleLabel = ViewUtils.createPopupHeaderLabel("FINDING OPPONENT");

        Label durationLabel = ViewUtils.createBodyTextLabel(
                "Searching for a " + (durationSeconds / 60) + " minute match…");

        statusLabel = ViewUtils.createBodyTextLabel("Connecting to the local network…");
        detailLabel = ViewUtils.createBodyTextLabel("");

        cancelButton = ViewUtils.createSpriteButton("/buttons/btn.png", "CANCEL", null);

        VBox contentBox = new VBox(20, titleLabel, durationLabel, statusLabel, detailLabel, cancelButton);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30, 50, 40, 50));

        root = new StackPane(contentBox);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
    }

    public Node getRoot() { return root; }
    public Button getCancelButton() { return cancelButton; }
    public void setStatus(String msg) { statusLabel.setText(msg); }
    public void setDetail(String msg) { detailLabel.setText(msg); }
}
