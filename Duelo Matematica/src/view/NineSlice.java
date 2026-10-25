package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;

/** Draws a frame of a sprite sheet as a resizable 9-slice (caps fixed, middle tiled or stretched). */
public final class NineSlice extends Region {
    private final Canvas canvas = new Canvas();
    private final Image img;
    private final double frameW, frameH;       // size of ONE frame in the source image
    private final double l, r, t, b;           // source-pixel insets (the fixed caps)
    private final double scale;                // integer scale for the caps (2, 3, 4...)
    private final boolean tileMiddle;          // true = repeat middle, false = stretch it
    private int frame = 0;

    public NineSlice(Image img, int frames, double l, double r, double t, double b,
                     double scale, boolean tileMiddle) {
        this.img = img;
        this.frameW = img.getWidth() / frames;
        this.frameH = img.getHeight();
        this.l = l; this.r = r; this.t = t; this.b = b;
        this.scale = scale;
        this.tileMiddle = tileMiddle;

        // Unmanaged so the canvas size never feeds back into this region's preferred size
        canvas.setManaged(false);
        getChildren().add(canvas);
        setMinSize((l + r) * scale, (t + b) * scale);
    }

    public void setFrame(int f) { this.frame = f; redraw(); }

    @Override protected void layoutChildren() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
        redraw();
    }

    private void redraw() {
        double W = getWidth(), H = getHeight();
        if (W <= 0 || H <= 0) return;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);               // crisp pixel art (JavaFX 12+)
        gc.clearRect(0, 0, W, H);

        double sx = frame * frameW;
        double dl = l * scale, dr = r * scale, dt = t * scale, db = b * scale;
        double mSW = frameW - l - r, mSH = frameH - t - b;   // middle size in source
        double mDW = W - dl - dr,    mDH = H - dt - db;      // middle size on screen

        // corners (never scaled except by the integer factor)
        gc.drawImage(img, sx, 0, l, t, 0, 0, dl, dt);
        gc.drawImage(img, sx + frameW - r, 0, r, t, W - dr, 0, dr, dt);
        gc.drawImage(img, sx, frameH - b, l, b, 0, H - db, dl, db);
        gc.drawImage(img, sx + frameW - r, frameH - b, r, b, W - dr, H - db, dr, db);

        // edges + center
        fill(gc, sx + l, 0,           mSW, t,   dl, 0,       mDW, dt,  true,  false); // top
        fill(gc, sx + l, frameH - b,  mSW, b,   dl, H - db,  mDW, db,  true,  false); // bottom
        fill(gc, sx,     t,           l,   mSH, 0,  dt,       dl,  mDH, false, true);  // left
        fill(gc, sx + frameW - r, t,  r,   mSH, W - dr, dt,   dr,  mDH, false, true);  // right
        fill(gc, sx + l, t,           mSW, mSH, dl, dt,       mDW, mDH, true,  true);  // center
    }

    /** Fills a destination rect from a source rect, tiling or stretching along the chosen axes. */
    private void fill(GraphicsContext gc, double sx, double sy, double sw, double sh,
                      double dx, double dy, double dw, double dh,
                      boolean alongX, boolean alongY) {
        if (dw <= 0 || dh <= 0 || sw <= 0 || sh <= 0) return;
        if (!tileMiddle) { gc.drawImage(img, sx, sy, sw, sh, dx, dy, dw, dh); return; }

        double stepX = alongX ? sw * scale : dw;
        double stepY = alongY ? sh * scale : dh;
        for (double y = 0; y < dh; y += stepY) {
            double h = Math.min(stepY, dh - y);
            for (double x = 0; x < dw; x += stepX) {
                double w = Math.min(stepX, dw - x);
                // crop the last tile instead of squashing it
                gc.drawImage(img, sx, sy,
                        alongX ? w / scale : sw, alongY ? h / scale : sh,
                        dx + x, dy + y, w, h);
            }
        }
    }
}
