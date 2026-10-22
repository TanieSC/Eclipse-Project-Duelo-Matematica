package view;

import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
 * in every single screen, we put the code here ONCE. 
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
                // Load the font at size 12. (The size here doesn't matter, we just 
                // need JavaFX to read the file and memorize its internal name).
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
        
        // Force the label to be exactly 200 pixels tall so it doesn't stretch weirdly.
        headerLabel.setPrefHeight(200); 
        headerLabel.setMinHeight(200);  
        headerLabel.setMaxHeight(200);  
        
        headerLabel.setStyle(
                "-fx-font-weight: bold;" +
                // Negative padding pulls the box up, line-spacing pulls the 3 lines closer together.
                "-fx-padding: -100px 60px; -fx-background-color: #f4f4f4;" +
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
        headerLabel.setFont(Font.font(customFontFamily, 20));
        
        // Standard grey background with a bold font and some padding.
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
        headerLabel.setFont(Font.font(customFontFamily, 14));
    	return headerLabel;
    }
    
    /**
     * BODY TEXT — Standard small text for paragraphs.
     */
    public static Label createBodyTextLabel(String text) { 
        Label headerLabel = new Label(text);
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setFont(Font.font(customFontFamily, 10));
    	return headerLabel;
    }
    
   /**
    * CLOSE BUTTON — Creates a standard transparent "X" button.
    */
   public static Button createCloseButton(String text) {
       Button closeButton = new Button(text);
       // Transparent background so it looks like a clean text "X".
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
    * SPRITE BUTTON (DYNAMIC TEXT WIDTH) — Calculates the exact pixel width 
    * of the text and sizes the button to fit it perfectly.
    */
   public static Button createSpriteButton(String imagePath, String text, Runnable onClickAction) {
       // 1. Create a temporary invisible Text object to measure the string.
       Text measure = new Text(text);
       measure.setFont(getCustomFont(18)); 
       
       // 2. Ask JavaFX exactly how wide that text is in pixels.
       double textWidth = measure.getLayoutBounds().getWidth();
       
       // 3. Add 80 pixels of padding so the text doesn't touch the wooden edges.
       double calculatedWidth = textWidth + 80; 
       double fixedHeight = 60; // Keep a consistent height
       
       // 4. Generate the button using the manual size method.
       Button btn = createSpriteButton(imagePath, calculatedWidth, fixedHeight, onClickAction);       
       
      // 4. Set the initial text (this triggers the resize)
       btn.setText(text);
       
       return btn;
   }

   /**
    * THE MAIN SPRITE BUTTON ENGINE — Builds the 3-frame animated button.
    */
   public static Button createSpriteButton(String imagePath, double width, double height, Runnable onClickAction) {
       
       // 1. Load the spritesheet image. If missing, return a boring default button.
       InputStream stream = ViewUtils.class.getResourceAsStream("/_assets" + imagePath);
       if (stream == null) {
           System.err.println("ERROR: Image NOT FOUND at path: /_assets" + imagePath);
           return new Button("Missing Image");
       }
      
       Image spriteSheet = new Image(stream);
       // 2. The image is 3 frames wide. Divide by 3 to get the width of one frame.
       double frameWidth = spriteSheet.getWidth() / 3.0;
       double frameHeight = spriteSheet.getHeight();
      
       // 3. Create the ImageView. The "Viewport" acts like a camera lens, 
       // starting at the first frame (Normal state).
       ImageView imageView = new ImageView(spriteSheet);
       imageView.setPreserveRatio(false); 
       imageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));

       // 4. Create the Text Label that will sit on top of the wood.
       Label textLabel = new Label();
       textLabel.setFont(getCustomFont(14)); 
       textLabel.setTextFill(Color.web("#6b3e26")); //(Color.BLACK);
       
       // --- THE ALIGNMENT FIXES ---
       
       // FIX 1: Remove hidden padding that JavaFX puts inside labels by default.
       textLabel.setPadding(Insets.EMPTY);
       
       // FIX 2: Center the text inside the label box.
       textLabel.setAlignment(Pos.CENTER);
       textLabel.setTextAlignment(TextAlignment.CENTER);
       
       // FIX 3: Custom fonts often have invisible space at the top. We physically 
       // nudge the text UP by 6 pixels to make it look truly centered on the wood.
       textLabel.setTranslateY(-6);

       // Optional: Create a shadow for the text (currently disabled in your code).
       //DropShadow textShadow = new DropShadow(2, 0, 1, Color.rgb(0, 0, 0, 0.7));
       //textLabel.setEffect(null);

       // 5. Stack the Image and Text on top of each other.
       StackPane graphicStack = new StackPane(imageView, textLabel);
       StackPane.setAlignment(imageView, Pos.CENTER);
       StackPane.setAlignment(textLabel, Pos.CENTER);

       // 6. Create the Button. We tell it to ONLY show the graphic (our StackPane).
       Button button = new Button();
       button.setGraphic(graphicStack);
       button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
       
       // FIX 4: Strip away the Button's hidden background borders and padding.
       button.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-background-insets: 0; -fx-border-width: 0;");
      
       // 7. Sync the Button's text property with our custom textLabel.
       button.textProperty().addListener((obs, oldV, newV) -> textLabel.setText(newV));

       // 8. SIZING MODE LOGIC
       if (width > 0 && height > 0) {
           // --- MANUAL SIZE MODE ---
           button.setPrefSize(width, height);
           button.setMinSize(width, height);
           button.setMaxSize(width, height);
           
           // FIX 5: Bind the StackPane tightly to the Button's dimensions so 
           // it stretches the image perfectly to fill the space.
           graphicStack.prefWidthProperty().bind(button.widthProperty());
           graphicStack.prefHeightProperty().bind(button.heightProperty());
           graphicStack.minWidthProperty().bind(button.widthProperty());
           graphicStack.minHeightProperty().bind(button.heightProperty());
           graphicStack.maxWidthProperty().bind(button.widthProperty());
           graphicStack.maxHeightProperty().bind(button.heightProperty());
           
           imageView.fitWidthProperty().bind(graphicStack.widthProperty());
           imageView.fitHeightProperty().bind(graphicStack.heightProperty());
           
       } else {
           // --- AUTO-SIZE MODE ---
           // Add padding so the text has breathing room.
           graphicStack.setPadding(new Insets(10, 30, 10, 30));
           
           // Make image "invisible" to the layout engine so the text dictates the width.
           imageView.setManaged(false); 
           imageView.fitWidthProperty().bind(button.widthProperty());
           imageView.fitHeightProperty().bind(button.heightProperty());
           
           // Center the image behind the text.
           imageView.layoutXProperty().bind(button.widthProperty().subtract(imageView.fitWidthProperty()).divide(2));
           imageView.layoutYProperty().bind(button.heightProperty().subtract(imageView.fitHeightProperty()).divide(2));
       }
      
       // 9. HOVER ANIMATION (Frame 1)
       button.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
          if (button.isArmed()) return; 
          imageView.setViewport(new Rectangle2D(isNowHovered ? frameWidth : 0, 0, frameWidth, frameHeight));
       });

       // 10. PRESS ANIMATION (Frame 2)
       button.armedProperty().addListener((obs, wasArmed, isNowArmed) -> {
          if (isNowArmed) {
              // PRESSED: Slide to the 3rd frame.
              imageView.setViewport(new Rectangle2D(frameWidth * 2, 0, frameWidth, frameHeight));
              
              // Move the text DOWN by 1 pixel from its base offset (-6 becomes -5)
              // to make it look physically pushed into the wood.
              textLabel.setTranslateY(-5);
              textLabel.setEffect(null); 
          } else {
              // RELEASED: Go back to Hover or Normal frame.
              imageView.setViewport(new Rectangle2D(button.isHover() ? frameWidth : 0, 0, frameWidth, frameHeight));
              
              // Snap text back to its base offset (-6).
              textLabel.setTranslateY(-6);
              textLabel.setEffect(null);
          }
       });

       // 11. Attach the click action.
       if (onClickAction != null) {
          button.setOnAction(e -> onClickAction.run());
       }

       return button;
  }
   
   /**
    * SPRITE BUTTON (2 FRAMES) — For square icon buttons with only 2 states.
    * Frame 1 (Left) = Normal, Frame 2 (Right) = Hover/Pressed
    */
   public static Button createTwoFrameSpriteButton(String imagePath, double size, Runnable onClickAction) {
       InputStream stream = ViewUtils.class.getResourceAsStream("/_assets" + imagePath);
       if (stream == null) {
           System.err.println("ERROR: Image NOT FOUND at path: /_assets" + imagePath);
           return new Button("Missing Image");
       }
      
       Image spriteSheet = new Image(stream);
       
       // This image has 2 frames side-by-side. We divide the width by 2.
       double frameWidth = spriteSheet.getWidth() / 2.0;
       double frameHeight = spriteSheet.getHeight();
      
       ImageView imageView = new ImageView(spriteSheet);
       imageView.setPreserveRatio(false); 
       // Start with the first frame (Normal)
       imageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));

       Button button = new Button();
       button.setGraphic(imageView);
       button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
       // Remove all default button styling
       button.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-background-insets: 0; -fx-border-width: 0;");
      
       // Lock the size to be perfectly square
       button.setPrefSize(size, size);
       button.setMinSize(size, size);
       button.setMaxSize(size, size);
       
       // Stretch image to fit the button
       imageView.fitWidthProperty().bind(button.widthProperty());
       imageView.fitHeightProperty().bind(button.heightProperty());
      
       //===================================
       //Add this code if hover is desired
       //===================================
       
       // HOVER ANIMATION (Shows 2nd frame)
       //button.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
      //    if (button.isArmed()) return; 
      //    imageView.setViewport(new Rectangle2D(isNowHovered ? frameWidth : 0, 0, frameWidth, frameHeight));
      // });

       // PRESS ANIMATION (Also shows 2nd frame)
       //button.armedProperty().addListener((obs, wasArmed, isNowArmed) -> {
       //   if (isNowArmed) {
       //       imageView.setViewport(new Rectangle2D(frameWidth, 0, frameWidth, frameHeight));
      //    } else {
      //        imageView.setViewport(new Rectangle2D(button.isHover() ? frameWidth : 0, 0, frameWidth, frameHeight));
      //    }
      // });       
       //=======================================
       //=======================================
       
       //========================================
       //Add this code if pressed only is desired
       //========================================
       
       // PRESS ANIMATION (Shows 2nd frame only when clicking)
       //button.armedProperty().addListener((obs, wasArmed, isNowArmed) -> {
       //   if (isNowArmed) {
       //       // Button is pressed down -> Show right frame
       //       imageView.setViewport(new Rectangle2D(frameWidth, 0, frameWidth, frameHeight));
       //   } else {
       //       // Button is released -> Always go back to normal frame
       //       imageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
       //   }
       //});      
       //=======================================
       //=======================================

       // ==========================================
       // 1. CREATE THE GLOW EFFECT
       // ==========================================
       DropShadow glow = new DropShadow();
       glow.setColor(Color.rgb(255, 235, 59, 0.9)); // Soft Yellow/Gold glow
       glow.setRadius(20);                          // How wide the glow spreads
       glow.setSpread(0.3);                         // How solid the glow is near the button
       glow.setOffsetX(0);                          // 0 means the glow surrounds evenly
       glow.setOffsetY(0);

       // ==========================================
       // 2. APPLY GLOW ON HOVER
       // ==========================================
       button.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
           if (button.isArmed()) return; 
           
           if (isNowHovered) {
               imageView.setEffect(glow); // Turn on the light
           } else {
               imageView.setEffect(null); // Turn off the light
           }
       });

       // ==========================================
       // 3. PRESS ANIMATION (Keep your 2nd frame here)
       // ==========================================
       button.armedProperty().addListener((obs, wasArmed, isNowArmed) -> {
          if (isNowArmed) {
              // Pressed down
              imageView.setViewport(new Rectangle2D(frameWidth, 0, frameWidth, frameHeight));
          } else {
              // Released
              imageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
              // Ensure glow returns if the mouse is still over the button
              if (button.isHover()) {
                  imageView.setEffect(glow);
              }
          }
       });
       
       if (onClickAction != null) {
          button.setOnAction(e -> onClickAction.run());
       }

       return button;
   }
}