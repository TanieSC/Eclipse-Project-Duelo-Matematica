package view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;
import model.AudioManager;

public class AnimatedSprite extends ImageView {
    private final Image[] idleFrames;
    private final Image[] correctFrames;
    private final Image[] wrongFrames;
    private Timeline currentTimeline;
    private final double FPS = 10.0; 

    public AnimatedSprite(String idlePath, int idleCount, 
                          String correctPath, int correctCount, 
                          String wrongPath, int wrongCount, 
                          double width, double height) {
        
        Image idleSheet = new Image(getClass().getResourceAsStream("/_assets" + idlePath));
        Image correctSheet = new Image(getClass().getResourceAsStream("/_assets" + correctPath));
        Image wrongSheet = new Image(getClass().getResourceAsStream("/_assets" + wrongPath));

        this.idleFrames = sliceFrames(idleSheet, idleCount);
        this.correctFrames = sliceFrames(correctSheet, correctCount);
        this.wrongFrames = sliceFrames(wrongSheet, wrongCount);

        this.setFitWidth(Math.round(width));
        this.setFitHeight(Math.round(height));
        this.setPreserveRatio(false);
        this.setSmooth(false); 
        this.setCache(false); 

        playIdle();
    }

    private Image[] sliceFrames(Image spriteSheet, int frameCount) {
        Image[] frames = new Image[frameCount];
        int frameWidth = (int) (spriteSheet.getWidth() / frameCount); 
        int frameHeight = (int) spriteSheet.getHeight();

        for (int i = 0; i < frameCount; i++) {
            frames[i] = new WritableImage(
                spriteSheet.getPixelReader(), 
                i * frameWidth, 0, 
                frameWidth, frameHeight
            );
        }
        return frames;
    }

    public void playIdle() { playAnimation(idleFrames, true); }
    public void playCorrect() { AudioManager.playSFX("sfx_hit.wav"); playAnimation(correctFrames, false); }
    public void playWrong() { playAnimation(wrongFrames, false); }

    private void playAnimation(Image[] frames, boolean loopForever) {
    	
    	//System.out.println("DEBUG: Starting Animation. Frames: " + frames.length); // <-- ADD THIS
    	
        if (currentTimeline != null) currentTimeline.stop();
        currentTimeline = new Timeline();
        double frameDurationMs = 1000.0 / FPS;

        for (int i = 0; i < frames.length; i++) {
            final int frameIndex = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * frameDurationMs), e -> {
                this.setImage(frames[frameIndex]);
            });
            currentTimeline.getKeyFrames().add(keyFrame);
        }

        if (loopForever) {
            currentTimeline.setCycleCount(Timeline.INDEFINITE);
        } else {
            currentTimeline.setCycleCount(1);
            currentTimeline.setOnFinished(e -> playIdle());
        }
        currentTimeline.play();
    }
}