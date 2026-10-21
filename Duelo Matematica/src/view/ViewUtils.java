package view;

import javafx.geometry.Pos;
import javafx.scene.control.*;

/**
 * VIEW UTILS — A "Toolbox" for shared visual elements.
 * 
 * WHAT DOES THIS CLASS DO?
 * Instead of copy-pasting the same code to make a header label in every 
 * single view, we put the code here ONCE. Any screen in the app can 
 * call `ViewUtils.createHeaderLabel("MY TITLE")` and get a perfectly 
 * styled header back.
 */
public final class ViewUtils {

    // Private constructor so nobody tries to create `new ViewUtils()`
    private ViewUtils() { }

    /**
     * Creates a standardized, styled header label.
     * 
     * @param text The text to display inside the header.
     * @return A fully styled JavaFX Label.
     */
    public static Label createHeaderLabel(String text) {
        Label headerLabel = new Label(text);
        headerLabel.setAlignment(Pos.CENTER);
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
    
   public static Button createCloseButton(String text) {
       Button closeButton = new Button(text);
       closeButton.setStyle(
               "-fx-background-color: transparent; -fx-font-weight: bold;" +
               "-fx-font-size: 14px; -fx-cursor: hand;"
       );
       
       return closeButton;
   }
    
}
