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

public final class LatexRenderer {

    private static final Pattern LATEX_PATTERN = Pattern.compile("\\$([^$]+)\\$");
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();
    private static final double SCALE = 2.0;

    private LatexRenderer() { }

    public static TextFlow toTextFlow(String raw, int fontSize) {
        TextFlow flow = new TextFlow();
        flow.setLineSpacing(3);
        
        // Note: We removed the MaxHeight restriction here. Let it grow naturally.
        
        if (raw == null || raw.isEmpty()) return flow;

        Matcher m = LATEX_PATTERN.matcher(raw);
        int last = 0;

        while (m.find()) {
            String plain = raw.substring(last, m.start());
            if (!plain.isEmpty()) {
                Text t = new Text(plain);
                t.setFont(Font.font("Arial", fontSize));
                flow.getChildren().add(t);
            }
            String latex = m.group(1);
            try {
                flow.getChildren().add(renderLatexNode(latex, fontSize));
            } catch (Exception ex) {
                Text t = new Text(latex);
                t.setFont(Font.font("Arial", fontSize));
                t.setStyle("-fx-fill: red;");
                flow.getChildren().add(t);
            }
            last = m.end();
        }

        String tail = raw.substring(last);
        if (!tail.isEmpty()) {
            Text t = new Text(tail);
            t.setFont(Font.font("Arial", fontSize));
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

        TeXFormula formula = new TeXFormula(latex);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, renderSize);
        icon.setInsets(new Insets(1, 1, 1, 1));

        BufferedImage bimg = new BufferedImage(
            Math.max(1, icon.getIconWidth()),
            Math.max(1, icon.getIconHeight()),
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = bimg.createGraphics();
        g2.setColor(Color.BLACK); 
        icon.paintIcon(null, g2, 0, 0);
        g2.dispose();

        return toFxImage(bimg);
    }

    private static Image toFxImage(BufferedImage bimg) {
        int w = bimg.getWidth();
        int h = bimg.getHeight();
        int[] buffer = bimg.getRGB(0, 0, w, h, null, 0, w);
        WritableImage fx = new WritableImage(w, h);
        fx.getPixelWriter().setPixels(0, 0, w, h,
                PixelFormat.getIntArgbInstance(), buffer, 0, w);
        return fx;
    }
}