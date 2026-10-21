package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;

/**
 * GAME SETTINGS VIEW — The inline Settings popup.
 * 
 * WHAT DOES THIS CLASS DO?
 * It draws the settings panel with a title, a close button, and two 
 * sliders for controlling Music and Sound Effects (SFX).
 */
public class GameSettingsView {

    private final VBox root;
    private final Button backButton;
    private final Slider musicSlider;
    private final Slider sfxSlider;

    public GameSettingsView() {
        // ==========================================
        // 1. THE HEADER
        // ==========================================
        // Changed title to "SETTINGS" to match the design
        Label titleLabel = ViewUtils.createHeaderLabel("SETTINGS");
        backButton = ViewUtils.createCloseButton("X");

        BorderPane headerBox = new BorderPane();
        headerBox.setCenter(titleLabel);
        headerBox.setRight(backButton);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // ==========================================
        // 2. THE CONTENT (Sliders)
        // ==========================================
        // Create the sliders (Min 0, Max 100, Default 50)
        musicSlider = new Slider(0, 100, 50);
        musicSlider.setPrefWidth(150);

        sfxSlider = new Slider(0, 100, 50);
        sfxSlider.setPrefWidth(150);

        // Create the labels
        Label musicLabel = new Label("MUSIC");
        musicLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        Label sfxLabel = new Label("SFX");
        sfxLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // Use a GridPane to align the labels and sliders
        GridPane formGrid = new GridPane();
        formGrid.setHgap(25); // Horizontal space between label and slider
        formGrid.setVgap(25); // Vertical space between rows
        formGrid.setAlignment(Pos.CENTER);

        // Add to grid (Column 0 = Label, Column 1 = Slider)
        formGrid.add(musicLabel, 0, 0);
        formGrid.add(musicSlider, 1, 0);
        formGrid.add(sfxLabel, 0, 1);
        formGrid.add(sfxSlider, 1, 1);

        // ==========================================
        // 3. ASSEMBLING THE CARD
        // ==========================================
        root = new VBox(30, headerBox, formGrid);
        root.setPadding(new Insets(15, 30, 30, 30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #777777; -fx-border-width: 1px;");
    }

    // ============================================================
    // PUBLIC METHODS
    // ============================================================

    public Node getRoot() { 
        return root; 
    }
    
    public Button getBackButton() { 
        return backButton; 
    }
    
    // You can use these later in a controller to save the volume levels
    public Slider getMusicSlider() { 
        return musicSlider; 
    }
    
    public Slider getSfxSlider() { 
        return sfxSlider; 
    }
}