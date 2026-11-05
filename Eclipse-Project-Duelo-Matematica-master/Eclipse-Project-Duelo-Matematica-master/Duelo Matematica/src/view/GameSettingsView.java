package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;

public class GameSettingsView {

    private final StackPane root;
    private final Button backButton;
    private final Slider musicSlider;
    private final Slider sfxSlider;

    public GameSettingsView() {
        // ==========================================
        // 1. THE HEADER (Title Only)
        // ==========================================
        Label titleLabel = ViewUtils.createPopupHeaderLabel("SETTINGS");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. THE CONTENT (Sliders)
        // ==========================================
        musicSlider = new Slider(0, 100, 50);
        sfxSlider = new Slider(0, 100, 50);

        // Create the wrappers (Width: 200, Height: 40)
        StackPane musicWrapper = ViewUtils.createCustomSliderNode(musicSlider, 200, 40);
        StackPane sfxWrapper = ViewUtils.createCustomSliderNode(sfxSlider, 200, 40);

        // Make sure the wrapper tells the GridPane how big it wants to be
        musicWrapper.setPrefWidth(200);
        sfxWrapper.setPrefWidth(200);
        
        Label musicLabel = ViewUtils.createBodyTextLabel("MUSIC");       
        Label sfxLabel = ViewUtils.createBodyTextLabel("SFX");

        GridPane formGrid = ViewUtils.createStandardFormGrid(15, 15);

        formGrid.add(musicLabel, 0, 0);
        formGrid.add(musicWrapper, 1, 0); 
        formGrid.add(sfxLabel, 0, 1);
        formGrid.add(sfxWrapper, 1, 1);   

        // ==========================================
        // 3. GROUP CONTENT
        // ==========================================
        VBox contentBox = new VBox(30, headerBox, formGrid);
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
        root.setPrefWidth(400); // Keep the overall width of the popup
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Align and position the button exactly like ConcedeView
        StackPane.setAlignment(backButton, Pos.TOP_RIGHT);
        StackPane.setMargin(backButton, new Insets(-10, -4, 0, 0)); 
    }

    public Node getRoot() { return root; }
    public Button getBackButton() { return backButton; }
    public Slider getMusicSlider() { return musicSlider; }
    public Slider getSfxSlider() { return sfxSlider; }
}