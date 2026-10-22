package view;

import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import javafx.scene.control.ContentDisplay;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * VIEW UTILS — A "Toolbox" for shared visual elements.
 * 
 * WHAT DOES THIS CLASS DO?
 * Instead of copy-pasting the same code to make headers, fonts, or buttons 
 * in every single view, we put the code here ONCE. 
 * 
 * Any screen in the app can call `ViewUtils.createMainTitleLabel(...)` or 
 * `ViewUtils.createSpriteButton(...)` to instantly get perfectly styled 
 * elements, keeping the whole app looking consistent.
 * 
 * Because the methods are 'static', you never need to write `new ViewUtils()`. 
 * You just call the methods directly.
 */
public final class ViewUtils {

    // This remembers the name of our custom font so we can use it anywhere.
    // We default to "System" just in case the font fails to load.
    private static String customFontFamily = "System"; 
	
    // Private constructor so nobody tries to create an object of this toolbox.
    private ViewUtils() { }

    /**
     * LOAD CUSTOM FONT — Reads the "Duelo.ttf" file from the _assets folder 
     * and registers it with JavaFX.
     * 
     * You MUST call this once when the app first starts.
     */
    public static void loadCustomFont() {
        try {
            // Try to find the font file inside the assets folder.
            java.io.InputStream stream = ViewUtils.class.getResourceAsStream("/_assets/fonts/Duelo.ttf");
            
            if (stream != null) {
                // Load the font at size 12 (size doesn't matter here, we just want the name).
                Font font = Font.loadFont(stream, 12);
                if (font != null) {
                    // Save the font's actual internal name so we can use it later.
                    customFontFamily = font.getFamily();
                    System.out.println("Successfully loaded font: " + customFontFamily);
                }
            } else {
                System.err.println("ERROR: Font file NOT FOUND at /_assets/fonts/Duelo.ttf");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * GET CUSTOM FONT — A quick way to grab our custom font at any size.
     */
    public static Font getCustomFont(double size) {
        return Font.font(customFontFamily, size);
    }
    
    /**
     * MAIN TITLE — Creates the huge "Duelo X Matematica" logo style label.
     */
    public static Label createMainTitleLabel(String text) { 
        Label headerLabel = new Label(text);
        
        // Center the text inside the box horizontally and vertically.
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setTextAlignment(TextAlignment.CENTER);
        
        // Apply our custom font at size 26.
        headerLabel.setFont(Font.font(customFontFamily, 26));
        
        // Force the label to be exactly 200 pixels tall.
        headerLabel.setPrefHeight(200); 
        headerLabel.setMinHeight(200);  
        headerLabel.setMaxHeight(200);  
        
        headerLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-padding: 2px 60px; -fx-background-color: #f4f4f4;" +
                // line-spacing is negative to squish the 3 lines of text closer together!
                "-fx-alignment: center; -fx-text-alignment: center; -fx-line-spacing: -40px; " 
            );
        
    	return headerLabel;
    }
    
    /**
     * POPUP HEADER — Creates the standard header for cards (e.g., "SETTINGS", "LOGIN").
     */
    public static Label createPopupHeaderLabel(String text) {
        Label headerLabel = new Label(text);
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setFont(Font.font(customFontFamily, 24));
        
        // Standard grey background with a bold font.
        headerLabel.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-border-width: 3px; " +
            "-fx-padding: 12px 12px; " +
            "-fx-background-color: #f4f4f4; " +
            "-fx-alignment: center; " +
            "-fx-text-alignment: center;"
        );
        
        return headerLabel;
    }
    
    /**
     * SUB HEADER — Smaller headers for sections inside a screen.
     */
    public static Label createSubHeaderLabel(String text) { 
        Label headerLabel = new Label(text);
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setFont(Font.font(customFontFamily, 18));
    	return headerLabel;
    }
    
    /**
     * BODY TEXT — Standard small text for paragraphs.
     */
    public static Label createBodyTextLabel(String text) { 
        Label headerLabel = new Label(text);
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setFont(Font.font(customFontFamily, 14));
    	return headerLabel;
    }
    
   /**
    * CLOSE BUTTON — Creates a standard transparent "X" button.
    */
   public static Button createCloseButton(String text) {
       Button closeButton = new Button(text);
       closeButton.setStyle(
               "-fx-background-color: transparent; -fx-font-weight: bold;" +
               "-fx-font-size: 14px; -fx-cursor: hand;"
       );
       return closeButton;
   }
   
   /**
    * SPRITE BUTTON (AUTO-SIZE) — Creates a wooden button that automatically 
    * sizes itself based on the length of the text.
    */
   public static Button createSpriteButton(String imagePath, Runnable onClickAction) {
       // Pass "-1, -1" to tell the engine below to auto-size based on text.
       return createSpriteButton(imagePath, -1, -1, onClickAction);
   }

   /**
    * SPRITE BUTTON (MANUAL SIZE) — The main engine that builds the 3-frame 
    * animated button using the spritesheet image.
    * 
    * @param width  The exact width. Pass -1 to auto-size.
    * @param height The exact height. Pass -1 to auto-size.
    */
   public static Button createSpriteButton(String imagePath, double width, double height, Runnable onClickAction) {
       
       // 1. Load the image. If it can't find it, print an error and return a boring text button.
       InputStream stream = ViewUtils.class.getResourceAsStream("/_assets" + imagePath);
       if (stream == null) {
           System.err.println("ERROR: Image NOT FOUND at path: /_assets" + imagePath);
           return new Button("Missing Image");
       }
      
       Image spriteSheet = new Image(stream);
       
       // 2. Calculate the math. The image has 3 frames side-by-side.
       // We divide the total width by 3 to know how wide a single frame is.
       double frameWidth = spriteSheet.getWidth() / 3.0;
       double frameHeight = spriteSheet.getHeight();
      
       // 3. Create the ImageView (the picture). 
       ImageView imageView = new ImageView(spriteSheet);
       imageView.setPreserveRatio(false); 
       
       // The "Viewport" is like a camera lens. We start by looking at the FIRST frame (x=0).
       imageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));

       // 4. Create the Text Label manually so it stays centered on the image.
       Label textLabel = new Label();
       DropShadow textShadow = new DropShadow(2, 0, 1, Color.rgb(0, 0, 0, 0.7));
       String textStyle = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 2, 0, 0, 1);";
       textLabel.setStyle(textStyle);

       // 5. Stack the Image and the Text on top of each other.
       StackPane graphicStack = new StackPane(imageView, textLabel);
       graphicStack.setAlignment(Pos.CENTER);
       
       // 6. Create the actual Button and attach the stack.
       Button button = new Button();
       button.setGraphic(graphicStack);
       button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); // Ignore the default text layout
       button.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
      
       // 7. Connect the button's text to our custom text label.
       // If someone calls button.setText("PLAY"), our textLabel updates automatically.
       button.textProperty().addListener((obs, oldV, newV) -> textLabel.setText(newV));

       // 8. SIZING LOGIC
       if (width > 0 && height > 0) {
           // --- MANUAL SIZE MODE ---
           // Lock EVERYTHING to the exact size provided.
           button.setPrefSize(width, height);
           button.setMinSize(width, height);
           button.setMaxSize(width, height);
           graphicStack.setPrefSize(width, height);
           graphicStack.setMinSize(width, height);
           graphicStack.setMaxSize(width, height);
           
           // Stretch the image exactly to the StackPane's size
           imageView.fitWidthProperty().bind(graphicStack.widthProperty());
           imageView.fitHeightProperty().bind(graphicStack.heightProperty());
           
       } else {
           // --- AUTO-SIZE MODE ---
           // Add padding so the text has breathing room.
           graphicStack.setPadding(new Insets(10, 30, 10, 30));
           
           // Make the image "invisible" to the layout engine.
           // This allows the TEXT to dictate how wide the button becomes.
           imageView.setManaged(false); 
           
           // Stretch the image to match the button's final calculated size
           imageView.fitWidthProperty().bind(button.widthProperty());
           imageView.fitHeightProperty().bind(button.heightProperty());
           
           // Manually center the image behind the text
           imageView.layoutXProperty().bind(button.widthProperty().subtract(imageView.fitWidthProperty()).divide(2));
           imageView.layoutYProperty().bind(button.heightProperty().subtract(imageView.fitHeightProperty()).divide(2));
       }
      
       // 9. HOVER ANIMATION
       // When the mouse hovers over, slide the camera lens to the middle frame.
       button.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
          if (button.isArmed()) return; // If they are currently clicking, don't change the frame
          imageView.setViewport(new Rectangle2D(isNowHovered ? frameWidth : 0, 0, frameWidth, frameHeight));
       });

       // 10. PRESS ANIMATION
       button.armedProperty().addListener((obs, wasArmed, isNowArmed) -> {
          if (isNowArmed) {
              // PRESSED: Slide the camera to the 3rd frame (the pressed wood).
              imageView.setViewport(new Rectangle2D(frameWidth * 2, 0, frameWidth, frameHeight));
              
              // Make the text look physically pressed by moving it down/right and removing its shadow.
              textLabel.setTranslateX(2);
              textLabel.setTranslateY(2);
              textLabel.setEffect(null); 
          } else {
              // RELEASED: Go back to Hover (or Normal if mouse dragged away).
              imageView.setViewport(new Rectangle2D(button.isHover() ? frameWidth : 0, 0, frameWidth, frameHeight));
              
              // Snap the text back to its original position and restore the shadow.
              textLabel.setTranslateX(0);
              textLabel.setTranslateY(0);
              textLabel.setEffect(textShadow);
          }
       });

       // 11. Attach the click action.
       if (onClickAction != null) {
          button.setOnAction(e -> onClickAction.run());
       }

       return button;
  }
}