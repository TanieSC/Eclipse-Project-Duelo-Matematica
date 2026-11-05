package view;

import javafx.embed.swing.SwingFXUtils; // Cleanly bridges AWT to JavaFX
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LATEX RENDERER — The "Math Translator".
 */
public final class LatexRenderer {

    private static final Pattern LATEX_PATTERN = Pattern.compile("\\$([^$]+)\\$");
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();
    private static final double SCALE = 2.0;

    private LatexRenderer() { }

    public static TextFlow toTextFlow(String raw, int fontSize) {
        TextFlow flow = new TextFlow();
        flow.setLineSpacing(3);
        
        if (raw == null || raw.isEmpty()) return flow;

        Matcher m = LATEX_PATTERN.matcher(raw);
        int last = 0;

        while (m.find()) {       	
            // 1. Normal text
            String plain = raw.substring(last, m.start());
            if (!plain.isEmpty()) {
                Text t = new Text(plain);
                t.setFont(ViewUtils.getCustomFont(fontSize));
                flow.getChildren().add(t); 
            }
            
            // 2. Math formula
            String latex = m.group(1);
            try {
                flow.getChildren().add(renderLatexNode(latex, 20)); 
            } catch (Exception ex) {
                Text t = new Text(latex);
                t.setFont(ViewUtils.getCustomFont(fontSize));
                t.setStyle("-fx-fill: red;");
                flow.getChildren().add(t);
            }
            last = m.end(); 
        }

        // 3. Tail text
        String tail = raw.substring(last);
        if (!tail.isEmpty()) {
            Text t = new Text(tail);
            t.setFont(ViewUtils.getCustomFont(fontSize));
            flow.getChildren().add(t);
        }
        return flow;
    }

    public static ImageView renderLatexNode(String latex, int fontSize) {
        Image img = CACHE.computeIfAbsent(latex, k -> renderLatexImage(k, fontSize));
        
        ImageView iv = new ImageView(img);
        iv.setFitWidth(img.getWidth() / SCALE);
        iv.setFitHeight(img.getHeight() / SCALE);
        
        return iv;
    }

    private static Image renderLatexImage(String latex, int baseFontSize) {
        int renderSize = (int) Math.round(baseFontSize * SCALE);

        String boldLatex = "\\boldsymbol{" + latex + "}";
        
        // JLaTeXMath strictly requires java.awt, so we fully qualify it here
        TeXFormula formula = new TeXFormula(boldLatex);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, renderSize);
        
        // AWT Insets are required by TeXIcon
        icon.setInsets(new java.awt.Insets(1, 1, 1, 1)); 

        // Create the AWT BufferedImage canvas
        BufferedImage bimg = new BufferedImage(
            Math.max(1, icon.getIconWidth()),
            Math.max(1, icon.getIconHeight()),
            BufferedImage.TYPE_INT_ARGB 
        );
        
        // Paint the formula onto the AWT canvas
        java.awt.Graphics2D g2 = bimg.createGraphics();
        g2.setColor(java.awt.Color.BLACK); 
        icon.paintIcon(null, g2, 0, 0);
        g2.dispose(); 

        // Use SwingFXUtils to seamlessly convert AWT to JavaFX
        // This replaces your manual toFxImage() method completely!
        return SwingFXUtils.toFXImage(bimg, null);
    }
}