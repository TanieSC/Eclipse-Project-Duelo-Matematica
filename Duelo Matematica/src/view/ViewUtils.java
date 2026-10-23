package view;

import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import javafx.scene.control.ContentDisplay;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


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
     * STANDARD FORM GRID — Creates a pre-configured GridPane for forms.
     * Column 0 is for Labels. Column 1 is for TextFields (which will stretch).
     */
    public static GridPane createStandardFormGrid(double Hgap, double Vgap) {
        GridPane formGrid = new GridPane();
        formGrid.setHgap(Hgap); // Horizontal gap between columns
        formGrid.setVgap(Vgap); // Vertical gap between rows
        formGrid.setPadding(new Insets(0, 20, 0, 20)); 
        formGrid.setAlignment(Pos.CENTER);

        // Column 0 (Labels) - Keeps its natural width
        ColumnConstraints col1 = new ColumnConstraints(); 
        
        // Column 1 (Fields) - Stretches to fill available space
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS); 
        
        formGrid.getColumnConstraints().addAll(col1, col2);

        return formGrid;
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
            "-fx-alignment: center; " +
            "-fx-text-alignment: center;" + 
            "-fx-border-color: transparent"
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
        headerLabel.setStyle("-fx-border-color: transparent");
    	return headerLabel;
    }
    
    /**
     * BODY TEXT — Standard small text for paragraphs.
     */
    public static Label createBodyTextLabel(String text) { 
        Label headerLabel = new Label(text);
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setFont(Font.font(customFontFamily, 10));
        headerLabel.setStyle("-fx-border-color: transparent");
    	return headerLabel;
    }
    
    /**
     * Creates a standardized TextField with the Duelo font and dark style.
     */
    public static TextField createStandardTextField() {
        TextField field = new TextField();
        field.setPrefWidth(140);
        field.setFont(getCustomFont(10)); // Apply Duelo.ttf
        field.setStyle(
            "-fx-background-color: #666666; " +
            "-fx-text-inner-color: white; " +
            "-fx-background-radius: 0;"
        );
        return field;
    }

    /**
     * Creates a standardized PasswordField with the Duelo font and dark style.
     */
    public static PasswordField createStandardPasswordField() {
        PasswordField field = new PasswordField();
        field.setPrefWidth(140);
        field.setFont(getCustomFont(10)); // Apply Duelo.ttf
        field.setStyle(
            "-fx-background-color: #666666; " +
            "-fx-text-inner-color: white; " +
            "-fx-background-radius: 0;"
        );
        return field;
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
       double fixedHeight = 80; // Keep a consistent height
       
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
       
       InputStream stream = ViewUtils.class.getResourceAsStream("/_assets" + imagePath);
       if (stream == null) {
           System.err.println("ERROR: Image NOT FOUND at path: /_assets" + imagePath);
           return new Button("Missing Image");
       }
      
       Image spriteSheet = new Image(stream);
       
       // --- FORCE WHOLE NUMBERS (Fixes the blur) ---
       int totalWidth = (int) Math.round(spriteSheet.getWidth());
       int totalHeight = (int) Math.round(spriteSheet.getHeight());
       int frameWidth = totalWidth / 3;
       int frameHeight = totalHeight;
       
       // --- 3-SLICE SETUP (SEPARATE LEFT AND RIGHT CAP WIDTHS) ---
       // You can now adjust these percentages independently if needed.
       int capWidthLeft = (int) Math.round(frameWidth * 0.18); 
       int capWidthRight = (int) Math.round(frameWidth * 0.18); 
       
       
       
       // Calculate the middle width by subtracting BOTH caps from the total frame width
       int midWidth = frameWidth - capWidthLeft - capWidthRight;

       ImageView leftCap = new ImageView(spriteSheet);
       ImageView midSection = new ImageView(spriteSheet);
       ImageView rightCap = new ImageView(spriteSheet);

       leftCap.setPreserveRatio(false);
       midSection.setPreserveRatio(false);
       rightCap.setPreserveRatio(false);
       
       leftCap.setSmooth(false);
       midSection.setSmooth(false);
       rightCap.setSmooth(false);

       // Force the caps to EXACTLY match their respective widths
       int colWidthLeft = capWidthLeft + 20;
       int colWidthRight = capWidthRight + 20;
       
       leftCap.setFitWidth(colWidthLeft);
       rightCap.setFitWidth(colWidthRight);

       GridPane woodBackground = new GridPane();
       woodBackground.setAlignment(Pos.CENTER);
       
       // Column 1: Left Cap
       ColumnConstraints col1 = new ColumnConstraints();
       col1.setMinWidth(colWidthLeft);
       col1.setPrefWidth(colWidthLeft);
       col1.setMaxWidth(colWidthLeft);
       
       // Column 2: Middle Stretch
       ColumnConstraints col2 = new ColumnConstraints();
       col2.setHgrow(Priority.ALWAYS); 
       
       // Column 3: Right Cap
       ColumnConstraints col3 = new ColumnConstraints();
       col3.setMinWidth(colWidthRight);
       col3.setPrefWidth(colWidthRight);
       col3.setMaxWidth(colWidthRight);
       
       woodBackground.getColumnConstraints().addAll(col1, col2, col3);
       woodBackground.add(leftCap, 0, 0);
       woodBackground.add(midSection, 1, 0);
       woodBackground.add(rightCap, 2, 0);

       // Bind the middle width to whatever space is left over after subtracting BOTH caps
       midSection.fitWidthProperty().bind(javafx.beans.binding.Bindings.createDoubleBinding(
           () -> Math.max(0, woodBackground.getWidth() - (capWidthLeft + capWidthRight)),
           woodBackground.widthProperty()
       ));

       // Pass both cap widths to the viewport updater
       updateSpriteViewports(leftCap, midSection, rightCap, 0, frameWidth, frameHeight, capWidthLeft, capWidthRight, midWidth);

       // --- TEXT SETUP ---
       Label textLabel = new Label();
       textLabel.setFont(getCustomFont(14)); 
       textLabel.setTextFill(Color.web("#6b3e26")); 
       textLabel.setPadding(Insets.EMPTY);
       textLabel.setAlignment(Pos.CENTER);
       textLabel.setTextAlignment(TextAlignment.CENTER);
       textLabel.setTranslateY(-6);

       // --- STACK SETUP ---
       StackPane graphicStack = new StackPane(woodBackground, textLabel);
       graphicStack.setAlignment(Pos.CENTER);

       // Bind the wood to perfectly fill the stack
       woodBackground.prefWidthProperty().bind(graphicStack.widthProperty());
       woodBackground.prefHeightProperty().bind(graphicStack.heightProperty());
       woodBackground.minWidthProperty().bind(graphicStack.widthProperty());
       woodBackground.minHeightProperty().bind(graphicStack.heightProperty());
       woodBackground.maxWidthProperty().bind(graphicStack.widthProperty());
       woodBackground.maxHeightProperty().bind(graphicStack.heightProperty());

       // Make sure the slices stretch vertically
       leftCap.fitHeightProperty().bind(woodBackground.heightProperty());
       rightCap.fitHeightProperty().bind(woodBackground.heightProperty());
       midSection.fitHeightProperty().bind(woodBackground.heightProperty());

       // --- BUTTON SETUP ---
       Button button = new Button();
       button.setGraphic(graphicStack);
       button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
       button.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-background-insets: 0; -fx-border-width: 0;");
      
       button.textProperty().addListener((obs, oldV, newV) -> textLabel.setText(newV));

       // --- SIZING LOGIC ---
       if (width > 0 && height > 0) {
           // MANUAL SIZE MODE
           button.setPrefSize(width, height);
           button.setMinSize(width, height);
           button.setMaxSize(width, height);
           
           graphicStack.prefWidthProperty().bind(button.widthProperty());
           graphicStack.prefHeightProperty().bind(button.heightProperty());
           graphicStack.minWidthProperty().bind(button.widthProperty());
           graphicStack.minHeightProperty().bind(button.heightProperty());
           graphicStack.maxWidthProperty().bind(button.widthProperty());
           graphicStack.maxHeightProperty().bind(button.heightProperty());
           
       } else {
           // AUTO-SIZE MODE
           button.setMaxWidth(Region.USE_PREF_SIZE);
           button.setMaxHeight(Region.USE_PREF_SIZE);
           
           graphicStack.setPadding(new Insets(10, 30, 10, 30));
       }
      
       // --- HOVER ANIMATION ---
       button.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
          if (button.isArmed()) return; 
          int frame = isNowHovered ? 1 : 0;
          updateSpriteViewports(leftCap, midSection, rightCap, frame, frameWidth, frameHeight, capWidthLeft, capWidthRight, midWidth);
       });

       // --- PRESS ANIMATION ---
       button.armedProperty().addListener((obs, wasArmed, isNowArmed) -> {
          if (isNowArmed) {
              updateSpriteViewports(leftCap, midSection, rightCap, 2, frameWidth, frameHeight, capWidthLeft, capWidthRight, midWidth);
              textLabel.setTranslateY(-5);
          } else {
              int frame = button.isHover() ? 1 : 0;
              updateSpriteViewports(leftCap, midSection, rightCap, frame, frameWidth, frameHeight, capWidthLeft, capWidthRight, midWidth);
              textLabel.setTranslateY(-6);
          }
       });

       if (onClickAction != null) {
          button.setOnAction(e -> onClickAction.run());
       }

       return button;
  }

  // --- UPDATED HELPER FOR SEPARATE CAP WIDTHS ---
  private static void updateSpriteViewports(ImageView left, ImageView mid, ImageView right, 
                                            int frameIndex, int frameWidth, int frameHeight, 
                                            int capWidthLeft, int capWidthRight, int midWidth) {
       int offsetX = frameIndex * frameWidth;
       
       // Left Cap: Start at offsetX, take capWidthLeft
       left.setViewport(new Rectangle2D(offsetX, 0, capWidthLeft, frameHeight));
       
       // Middle: Start after Left Cap, take midWidth
       mid.setViewport(new Rectangle2D(offsetX + capWidthLeft, 0, midWidth, frameHeight));
       
       // Right Cap: Start at the end of the frame, go back by capWidthRight
       right.setViewport(new Rectangle2D(offsetX + frameWidth - capWidthRight, 0, capWidthRight, frameHeight));
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
   
   /**
    * GLOBAL DROPDOWN ANIMATION
    * Wraps the view in the wooden background and animates it dropping down.
    * 
    * @param view            The inline view to show (e.g., LoginView, GameMainMenuView).
    * @param rootContainer   The main StackPane that holds everything.
    * @param backgroundNodes Any screens that should stay in the background (like the menu or overlay).
    */
   public static void animateDropdown(Node view, StackPane rootContainer, Node... backgroundNodes) {
       // 1. Load the 12-frame wooden animation
       Image woodImg = new Image(ViewUtils.class.getResourceAsStream("/_assets/panels/ui_popup_scroll.png"));
       ImageView woodBackground = new ImageView(woodImg);
       
       // 2. Cast the Node to a Region so we can access its width/height
       Region viewRegion = (Region) view;
       
       woodBackground.setPreserveRatio(false);
       woodBackground.fitWidthProperty().bind(viewRegion.widthProperty());
       woodBackground.fitHeightProperty().bind(viewRegion.heightProperty());

       // 3. Create a container for the Wood + The View
       StackPane dropdownContainer = new StackPane(woodBackground, view);
       dropdownContainer.setAlignment(Pos.CENTER); 
       dropdownContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

       // 4. Add background nodes first, then the dropdown container
       rootContainer.getChildren().setAll(backgroundNodes);
       rootContainer.getChildren().add(dropdownContainer);
       
       // Force JavaFX to calculate the size
       rootContainer.applyCss();
       rootContainer.layout();

       double targetHeight = dropdownContainer.getHeight();
       if (targetHeight <= 0) targetHeight = 500; // Safety fallback

       // 5. Create the mask (Curtain) revealing it downwards
       Rectangle clip = new Rectangle();
       clip.widthProperty().bind(dropdownContainer.widthProperty());
       clip.setHeight(0); 
       dropdownContainer.setClip(clip);

       // 6. Animate the 12-frame wooden background
       double frameWidth = woodImg.getWidth() / 12.0;
       double frameHeight = woodImg.getHeight();
       
       Timeline woodTimeline = new Timeline();
       for (int i = 0; i < 12; i++) {
           final int frame = i;
           woodTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * 40), e -> {
               woodBackground.setViewport(new Rectangle2D(frame * frameWidth, 0, frameWidth, frameHeight));
           }));
       }

       // 7. Animate the curtain revealing the view
       Timeline clipTimeline = new Timeline(
           new KeyFrame(Duration.ZERO, new KeyValue(clip.heightProperty(), 0, Interpolator.EASE_IN)),
           new KeyFrame(Duration.millis(480), new KeyValue(clip.heightProperty(), targetHeight, Interpolator.EASE_OUT))
       );

       // 8. Play them together!
       woodTimeline.play();
       clipTimeline.play();
   }
   
   
   public static StackPane createCustomSliderNode(Slider slider, double width, double height) {
       int trackHeight = 24;   // Height of the dark frame
       int fillHeight = 16;    // Height of the light fill
       double trackPaddingX = 6; 
       double handleWidth = 16; 

       // 1. Load images
       java.io.InputStream frameStream = ViewUtils.class.getResourceAsStream("/_assets/slider/ui_slider_frame.png");
       java.io.InputStream fillStream = ViewUtils.class.getResourceAsStream("/_assets/slider/ui_slider_fill.png");
       java.io.InputStream handleStream = ViewUtils.class.getResourceAsStream("/_assets/slider/ui_slider_control.png");

       if (frameStream == null || fillStream == null || handleStream == null) {
           System.err.println("CRITICAL ERROR: Slider images NOT FOUND!");
           StackPane fallback = new StackPane(new Label("Missing Images"));
           fallback.setPrefSize(width, height);
           fallback.setStyle("-fx-background-color: red;");
           return fallback;
       }

       // 2. Setup Frame
       ImageView frameView = new ImageView(new Image(frameStream));
       frameView.setFitWidth(width);
       frameView.setFitHeight(trackHeight);
       frameView.setPreserveRatio(false);
       frameView.setSmooth(false);

       // 3. Setup Fill
       ImageView fillView = new ImageView(new Image(fillStream));
       fillView.setFitWidth(width - (trackPaddingX * 2)); 
       fillView.setFitHeight(fillHeight);
       fillView.setPreserveRatio(false);
       fillView.setSmooth(false);
       
       // Clip the fill
       Rectangle clip = new Rectangle(0, 0, 0, fillHeight);
       fillView.setClip(clip);

       // 4. Setup Handle
       ImageView handleView = new ImageView(new Image(handleStream));
       handleView.setFitWidth(handleWidth);
       handleView.setFitHeight(height);
       handleView.setPreserveRatio(false);
       handleView.setSmooth(false);

       // --- THE FIX: USE A STACKPANE FOR PERFECT CENTERING ---
       // By putting the frame and fill in a StackPane, JavaFX automatically centers them perfectly!
       StackPane trackStack = new StackPane(frameView, fillView);
       
       // Align the fill to the left so it doesn't block the right side of the frame
       StackPane.setAlignment(fillView, Pos.CENTER_LEFT);
       // Push the fill inward by the padding
       StackPane.setMargin(fillView, new Insets(0, 0, 0, trackPaddingX));
       
       // Ensure the StackPane is exactly the size of the frame
       trackStack.setPrefSize(width, trackHeight);
       trackStack.setMaxSize(width, trackHeight);
       trackStack.setMinSize(width, trackHeight);

       // 5. Main Visual Pane
       Pane visualPane = new Pane();
       visualPane.setPrefSize(width, height);
       visualPane.setMaxSize(width, height);

       // Center the trackStack vertically inside the main slider height
       trackStack.setLayoutY((height - trackHeight) / 2.0);
       
       // Handle spans the full height
       handleView.setY(0); 

       visualPane.getChildren().addAll(trackStack, handleView);

       // 6. Setup invisible slider
       slider.setOpacity(0); 
       slider.setPrefSize(width, height);
       slider.setMaxSize(width, height);
       slider.setMinSize(width, height);

       // 7. Wrap everything
       StackPane wrapper = new StackPane(visualPane, slider);
       wrapper.setPrefSize(width, height);
       wrapper.setMinSize(width, height);
       wrapper.setMaxSize(width, height);

       // 8. Update Logic
       slider.valueProperty().addListener((obs, oldVal, newVal) -> {
           double min = slider.getMin();
           double max = slider.getMax();
           double percent = (newVal.doubleValue() - min) / (max - min);
           double trackWidth = width - handleWidth;
           double handleX = trackWidth * percent;
           
           handleView.setX(handleX);
           
           // Clip width calculates exactly to the center of the handle
           double currentClipWidth = handleX + (handleWidth / 2.0) - trackPaddingX;
           clip.setWidth(Math.max(0, currentClipWidth)); 
       });

       // Set initial position
       double initPercent = (slider.getValue() - slider.getMin()) / (slider.getMax() - slider.getMin());
       double trackWidth = width - handleWidth;
       handleView.setX(trackWidth * initPercent);
       
       double initClipWidth = (trackWidth * initPercent) + (handleWidth / 2.0) - trackPaddingX;
       clip.setWidth(Math.max(0, initClipWidth));

       return wrapper;
   }

}