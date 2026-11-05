package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import model.StoryModel;
import view.StoryScreenView;

import java.util.List;

public class StoryScreenController {

    private Timeline storyTimeline;

    public StoryScreenController(StoryScreenView view, StoryModel model, int level, Navigator nav) {
        
        // Pause button shows the overlay and pauses the text
        view.getPauseButton().setOnAction(e -> {
            if (storyTimeline != null) storyTimeline.pause();
            
            view.showPauseOverlay(
                () -> { // onResume (runs when popup is closed via Resume)
                    if (storyTimeline != null) storyTimeline.play();
                },
                () -> { // onExitToMap (runs when popup is closed via Back to Map)
                    if (storyTimeline != null) storyTimeline.stop();
                    nav.goToStoryLevels();
                }
            );
        });

        // Skip button goes straight to battle
        view.getSkipButton().setOnAction(e -> {
            if (storyTimeline != null) storyTimeline.stop();
            nav.goToStoryBattle(level); 
        });

        // Start the story sequence
        List<String> lines = model.getStoryLines(level);
        playStorySequence(view, lines, level, nav);
    }

    private void playStorySequence(StoryScreenView view, List<String> lines, int level, Navigator nav) {
        storyTimeline = new Timeline();

        for (int i = 0; i < lines.size(); i++) {
            final String lineText = lines.get(i);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds((i + 1) * 1.4), e -> {
                view.addLine(lineText);
            });
            storyTimeline.getKeyFrames().add(keyFrame);
        }

        storyTimeline.setOnFinished(e -> {
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), evt -> {
                nav.goToStoryBattle(level); // Automatically load the battle
            }));
            delay.play();
        });

        storyTimeline.play();
    }
}