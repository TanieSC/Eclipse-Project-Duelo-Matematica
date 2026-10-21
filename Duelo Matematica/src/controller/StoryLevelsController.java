package controller;

import javafx.scene.control.Button;
import view.StoryLevelsView;

public class StoryLevelsController {

    public StoryLevelsController(StoryLevelsView view, Navigator nav) {
        // Wire the Pause and Gear buttons
        view.getPauseButton().setOnAction(e -> nav.goToGameStartMenu());
        view.getGearButton().setOnAction(e -> nav.goToSettings());

        // Wire the Level buttons
        Button[] buttons = view.getLevelButtons();
        for (int i = 0; i < buttons.length; i++) {
            int level = i + 1;
            double[] targetCoords = (double[]) buttons[i].getUserData();
            
            buttons[i].setOnAction(e -> {
                view.animateAssetTo(targetCoords[0], targetCoords[1], () -> {
                    nav.goToStoryScreen(level);
                });
            });
        }
    }
}