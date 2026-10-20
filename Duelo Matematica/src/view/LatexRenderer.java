package view;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.image.ImageView;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LATEX RENDERER — The "Math Translator".
 * 
 * WHAT DOES THIS CLASS DO?
 * JavaFX standard text boxes cannot display complex math formulas (like 
 * fractions, square roots, or summations). They only show plain text.
 * 
 * This class's job is to solve that problem. It reads a string of text 
 * that contains math wrapped in dollar signs (like "$x^2$"). It uses a 
 * library called JLaTeXMath to draw that math onto an invisible canvas, 
 * turns that canvas into a picture (Image), and places the picture 
 * seamlessly inline with normal text.
 * 
 * It also caches the pictures in memory so if you see the same formula 
 * twice, it doesn't waste time drawing it again.
 */
public final class LatexRenderer {

    // A search tool that looks for text wrapped in dollar signs (the math formulas).
    private static final Pattern LATEX_PATTERN = Pattern.compile("\\$([^$]+)\\$");
    
    // A memory box (Cache). It remembers drawn formulas so we only have to draw them once.
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();
    
    // We draw the math at 2x the normal size, then shrink it down. 
    // This makes the final image look super sharp (not blurry) on modern screens.
    private static final double SCALE = 2.0;

    // Private constructor because this is a utility class; you never create a "new" LatexRenderer.
    private LatexRenderer() { }

    /**
     * TO TEXT FLOW — The main entry point.
     * 
     * This method takes a mixed string (words + math) and returns a JavaFX 
     * "TextFlow". A TextFlow is a special container that can hold normal text 
     * and pictures side-by-side, wrapping them like a paragraph.
     */
    public static TextFlow toTextFlow(String raw, int fontSize) {
        TextFlow flow = new TextFlow();
        flow.setLineSpacing(3);
        
        if (raw == null || raw.isEmpty()) return flow;

        // Start searching the string for math parts ($...$).
        Matcher m = LATEX_PATTERN.matcher(raw);
        int last = 0;

        // Loop through every math formula we find.
        while (m.find()) {
            // 1. Grab the normal text that came BEFORE the math formula.
            String plain = raw.substring(last, m.start());
            if (!plain.isEmpty()) {
                Text t = new Text(plain);
                t.setFont(Font.font("Arial", fontSize));
                flow.getChildren().add(t); // Add the normal text to the flow.
            }
            
            // 2. Grab the math formula itself and draw it.
            String latex = m.group(1);
            try {
                flow.getChildren().add(renderLatexNode(latex, fontSize)); // Add the math image to the flow.
            } catch (Exception ex) {
                // If the math is broken, just show the raw text in red so the game doesn't crash.
                Text t = new Text(latex);
                t.setFont(Font.font("Arial", fontSize));
                t.setStyle("-fx-fill: red;");
                flow.getChildren().add(t);
            }
            last = m.end(); // Remember where we left off.
        }

        // 3. Grab any normal text that came AFTER the last math formula.
        String tail = raw.substring(last);
        if (!tail.isEmpty()) {
            Text t = new Text(tail);
            t.setFont(Font.font("Arial", fontSize));
            flow.getChildren().add(t);
        }
        return flow;
    }

    /**
     * RENDER LATEX NODE — Converts a math string into a JavaFX ImageView.
     * 
     * It checks the memory box first. If the image is there, it grabs it. 
     * If not, it draws a new one.
     */
    public static ImageView renderLatexNode(String latex, int fontSize) {
        // Look in the cache. If it's not there, draw it using 'renderLatexImage'.
        Image img = CACHE.computeIfAbsent(latex, k -> renderLatexImage(k, fontSize));
        
        ImageView iv = new ImageView(img);
        
        // Because we drew it at 2x size (SCALE), we shrink it down to normal size here.
        iv.setFitWidth(img.getWidth() / SCALE);
        iv.setFitHeight(img.getHeight() / SCALE);
        
        return iv;
    }

    /**
     * RENDER LATEX IMAGE — The actual drawing process.
     * 
     * Uses the JLaTeXMath library to paint the formula onto a blank canvas 
     * (BufferedImage).
     */
    private static Image renderLatexImage(String latex, int baseFontSize) {
        int renderSize = (int) Math.round(baseFontSize * SCALE);

        // 1. Turn the text into a math formula object.
        TeXFormula formula = new TeXFormula(latex);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, renderSize);
        icon.setInsets(new Insets(1, 1, 1, 1)); // Add a tiny bit of padding.

        // 2. Create a blank canvas for the image.
        BufferedImage bimg = new BufferedImage(
            Math.max(1, icon.getIconWidth()),
            Math.max(1, icon.getIconHeight()),
            BufferedImage.TYPE_INT_ARGB // Transparent background.
        );
        
        // 3. Paint the formula onto the canvas.
        Graphics2D g2 = bimg.createGraphics();
        g2.setColor(Color.BLACK); 
        icon.paintIcon(null, g2, 0, 0);
        g2.dispose(); // Clean up the paintbrush.

        // 4. Convert the canvas to a format JavaFX can use.
        return toFxImage(bimg);
    }

    /**
     * TO FX IMAGE — The Translator.
     * 
     * The JLaTeXMath library draws using old "Java AWT" tools. JavaFX can't 
     * read those directly. This method copies the pixel colors from the AWT 
     * canvas into a JavaFX image.
     */
    private static Image toFxImage(BufferedImage bimg) {
        int w = bimg.getWidth();
        int h = bimg.getHeight();
        
        // Grab all the pixel colors from the AWT image into a list.
        int[] buffer = bimg.getRGB(0, 0, w, h, null, 0, w);
        
        // Create a new JavaFX image and paste the colors into it.
        WritableImage fx = new WritableImage(w, h);
        fx.getPixelWriter().setPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), buffer, 0, w);
        
        return fx;
    }
}