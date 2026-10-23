package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;

public class GameSettingsView {

    private final VBox root;
    private final Button backButton;
    private final Slider musicSlider;
    private final Slider sfxSlider;

    public GameSettingsView() {
        Label titleLabel = ViewUtils.createPopupHeaderLabel("SETTINGS");
        backButton = ViewUtils.createCloseButton("X");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);


        musicSlider = new Slider(0, 100, 50);
        sfxSlider = new Slider(0, 100, 50);

        // Create the wrappers (Width: 250, Height: 40)
        StackPane musicWrapper = ViewUtils.createCustomSliderNode(musicSlider, 200, 40);
        StackPane sfxWrapper = ViewUtils.createCustomSliderNode(sfxSlider, 200, 40);

        // Make sure the wrapper tells the GridPane how big it wants to be
        musicWrapper.setPrefWidth(200);
        sfxWrapper.setPrefWidth(200);
        
        Label musicLabel = ViewUtils.createBodyTextLabel("MUSIC"); //new Label("MUSIC");        
        Label sfxLabel = ViewUtils.createBodyTextLabel("SFX"); //new Label("SFX");

        //USE THE NEW REUSABLE GRID METHOD! (hGap: 25, vGap: 25)
        GridPane formGrid = ViewUtils.createStandardFormGrid(15, 15);

        //Just add your items to the grid
        formGrid.add(musicLabel, 0, 0);
        formGrid.add(musicWrapper, 1, 0); 
        formGrid.add(sfxLabel, 0, 1);
        formGrid.add(sfxWrapper, 1, 1);   

        root = new VBox(30, headerBox, formGrid);
        root.setPadding(new Insets(15, 30, 30, 30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setPrefWidth(400);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
    }

    public Node getRoot() { return root; }
    public Button getBackButton() { return backButton; }
    public Slider getMusicSlider() { return musicSlider; }
    public Slider getSfxSlider() { return sfxSlider; }
}