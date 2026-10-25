package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * CONCEDE VIEW — The confirmation dialog for surrendering a match.
 */
public class ConcedeView {

	private final StackPane root;
    private final Button yesButton;
    private final Button noButton;
    private final Button closeButton;

    public ConcedeView() {
          
          // --- 1. THE CONTENT (Title, Message, Buttons) ---
          Label titleLabel = ViewUtils.createPopupHeaderLabel("CONCEDE?");
          
          Label messageLabel = new Label("Are you sure you\nwant to concede?");
          messageLabel.setFont(ViewUtils.getCustomFont(10));
          messageLabel.setStyle("-fx-text-fill: #222222;");
          messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
          messageLabel.setAlignment(Pos.CENTER);
          
          yesButton = ViewUtils.createSpriteButton("/buttons/btn.png", "YES", null);
          noButton  = ViewUtils.createSpriteButton("/buttons/btn.png", "NO", null);

          HBox buttonBox = new HBox(30, yesButton, noButton);
          buttonBox.setAlignment(Pos.CENTER);
          buttonBox.setPadding(new Insets(10, 0, 0, 0));

          VBox contentBox = new VBox(20, titleLabel, messageLabel, buttonBox);
          contentBox.setAlignment(Pos.CENTER);
          
          // Add padding to push the text away from the wooden edges
          contentBox.setPadding(new Insets(25, 40, 40, 40)); 

          // --- 2. THE EXIT BUTTON ---
          closeButton = ViewUtils.createTwoFrameSpriteButton("/buttons/btn_exit.png", 32, null);

          // --- 3. ASSEMBLE EVERYTHING ---
          root = new StackPane(contentBox, closeButton);
          root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
          
          // THE FIX: Add a transparent padding to the root.
          // This creates an invisible buffer zone so the animation clip 
          // doesn't cut off the button when we push it to the edge.
        //  root.setPadding(new Insets(15));

          // Align the button to the top right corner
          StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
          
          // Push the button OUTWARD so it perfectly overlaps the wooden border.
          // (Negative numbers move it up and right)
          StackPane.setMargin(closeButton, new Insets(-10, -4, 0, 0)); 
    }

    // --- Getters for the Controller ---
    public Node getRoot()          { return root; }
    public Button getYesButton()   { return yesButton; }
    public Button getNoButton()    { return noButton; }
    public Button getCloseButton() { return closeButton; }
}