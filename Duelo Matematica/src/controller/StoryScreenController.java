package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import model.StoryModel;
import view.StoryScreenView;

import java.util.List;

public class StoryScreenController {

    private Timeline storyTimeline;

    public StoryScreenController(StoryScreenView view, StoryModel model, int level, Navigator nav) {
        // Pause button behavior
        view.getPauseButton().setOnAction(e -> {
            if (storyTimeline != null) storyTimeline.pause();
            showPauseDialog();
            nav.goToStoryLevels(); // Go back to map when paused
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

    private void showPauseDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pause");
        alert.setHeaderText(null);
        alert.setContentText("Game Paused");
        alert.show();
    }
}