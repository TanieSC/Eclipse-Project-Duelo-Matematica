package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import javafx.scene.control.ContentDisplay;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * VIEW UTILS — A "Toolbox" for shared visual elements.
 * 
 * WHAT DOES THIS CLASS DO?
 * Instead of copy-pasting the same code to make headers or buttons in every 
 * single view, we put the code here ONCE. 
 * 
 * Any screen in the app can call `ViewUtils.createHeaderLabel(...)` or 
 * `ViewUtils.createSpriteButton(...)` to instantly get perfectly styled 
 * elements, keeping the whole app looking consistent.
 * 
 * Because the methods are 'static', you never need to write `new ViewUtils()`. 
 * You just call the methods directly.
 */
public final class ViewUtils {

    // Private constructor so nobody tries to create `new ViewUtils()`. 
    // It's just a toolbox, not an object you build.
    private ViewUtils() { }

    /**
     * TOOL 1: Creates a standardized, styled header label.
     * 
     * @param text The text to display inside the header (e.g., "LOGIN").
     * @return A fully styled JavaFX Label with a gray background and bold text.
     */
    public static Label createHeaderLabel(String text) {
        Label headerLabel = new Label(text);
        headerLabel.setAlignment(Pos.CENTER);
        
        // This is the CSS styling for the header (font size, bold, background, padding)
        headerLabel.setStyle(
            "-fx-font-size: 26px; " +
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
    * TOOL 2: Creates a standardized "X" (Close) button.
    * 
    * @param text The text to display (usually just "X").
    * @return A styled JavaFX Button that looks like a close icon.
    */
   public static Button createCloseButton(String text) {
       Button closeButton = new Button(text);
       // Make the background transparent and make it look clickable
       closeButton.setStyle(
               "-fx-background-color: transparent; -fx-font-weight: bold;" +
               "-fx-font-size: 14px; -fx-cursor: hand;"
       );
       
       return closeButton;
   }
   
   /**
    * OVERLOAD 1: Auto-size based on Text.
    * 
    * If you call this method without providing sizes, it automatically calls
    * the method below and passes "-1, -1" to tell it "just shrink-wrap the text".
    */
   public static Button createSpriteButton(String imagePath, Runnable onClickAction) {
       return createSpriteButton(imagePath, -1, -1, onClickAction);
   }

   /**
    * OVERLOAD 2: The main engine for creating the animated wooden buttons.
    * 
    * @param imagePath      The path to the 3-frame image in the _assets folder.
    * @param width          The exact width (or -1 to auto-size to text).
    * @param height         The exact height (or -1 to auto-size to text).
    * @param onClickAction  What happens when the user clicks it.
    */
   public static Button createSpriteButton(String imagePath, double width, double height, Runnable onClickAction) {
       
       // 1. Load the image. If it can't find it, print an error and return a boring text button.
       InputStream stream = ViewUtils.class.getResourceAsStream("/_assets" + imagePath);
       if (stream == null) {
           System.err.println("ERROR: Image NOT FOUND at path: /_assets" + imagePath);
           return new Button("Missing Image");
       }
      
       Image spriteSheet = new Image(stream);
       
       // 2. Calculate the math for the spritesheet.
       // The image has 3 frames side-by-side. We divide the total width by 3.
       double frameWidth = spriteSheet.getWidth() / 3.0;
       double frameHeight = spriteSheet.getHeight();
      
       // 3. Create the ImageView (the picture itself).
       ImageView imageView = new ImageView(spriteSheet);
       imageView.setPreserveRatio(false); 
       // The "Viewport" is like a camera lens. We start by looking at the FIRST frame (x=0).
       imageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));

       // 4. Create a Text Label manually. 
       // We do this because if we just use button.setText(), the text layout gets messy with images.
       Label textLabel = new Label();
       DropShadow textShadow = new DropShadow(2, 0, 1, Color.rgb(0, 0, 0, 0.7));
       String textStyle = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 2, 0, 0, 1);";
       textLabel.setStyle(textStyle);

       // 5. Stack the Image and the Text on top of each other.
       StackPane graphicStack = new StackPane(imageView, textLabel);
       graphicStack.setAlignment(Pos.CENTER);
       
       // 6. Create the actual Button. We give it the StackPane as its graphic.
       Button button = new Button();
       button.setGraphic(graphicStack);
       button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); // Tell the button to ignore its own text system
       button.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
      
       // 7. Connect the button's text to our custom text label.
       // If someone calls button.setText("PLAY"), our textLabel updates automatically.
       button.textProperty().addListener((obs, oldV, newV) -> textLabel.setText(newV));

       // 8. THE SIZING LOGIC (This is where the magic happens)
       if (width > 0 && height > 0) {
           // --- MANUAL SIZE MODE ---
           // If the user provided a width and height, lock EVERYTHING to that exact size.
           button.setPrefSize(width, height);
           button.setMinSize(width, height);
           button.setMaxSize(width, height);
           
           graphicStack.setPrefSize(width, height);
           graphicStack.setMinSize(width, height);
           graphicStack.setMaxSize(width, height);
           
           // Force the image to stretch exactly to the StackPane's size
           imageView.fitWidthProperty().bind(graphicStack.widthProperty());
           imageView.fitHeightProperty().bind(graphicStack.heightProperty());
           
       } else {
           // --- AUTO-SIZE MODE ---
           // If no size was provided, we add padding so the text has breathing room.
           graphicStack.setPadding(new Insets(10, 30, 10, 30));
           
           // We make the image "unmanaged", meaning it becomes invisible to the layout engine.
           // This allows the TEXT to dictate how wide the button becomes.
           imageView.setManaged(false); 
           
           // The image stretches to match the button's final calculated size
           imageView.fitWidthProperty().bind(button.widthProperty());
           imageView.fitHeightProperty().bind(button.heightProperty());
           
           // Manually center the image behind the text
           imageView.layoutXProperty().bind(button.widthProperty().subtract(imageView.fitWidthProperty()).divide(2));
           imageView.layoutYProperty().bind(button.heightProperty().subtract(imageView.fitHeightProperty()).divide(2));
       }
      
       // 9. HOVER ANIMATION
       // When the mouse is over the button, slide the "camera lens" to the middle frame.
       button.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
          if (button.isArmed()) return; // If they are currently clicking, don't change the frame
          imageView.setViewport(new Rectangle2D(isNowHovered ? frameWidth : 0, 0, frameWidth, frameHeight));
       });

       // 10. PRESS ANIMATION
       // "Armed" means the mouse button is held down.
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