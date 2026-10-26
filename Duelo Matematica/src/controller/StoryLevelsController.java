package controller;

import view.StoryLevelsView;

public class StoryLevelsController {

    public StoryLevelsController(StoryLevelsView view, Navigator nav) {
        // Wire the UI buttons
        view.getPauseButton().setOnAction(e -> nav.goToGameStartMenu()); 
        view.getGearButton().setOnAction(e -> nav.goToSettings());

        // Wire the level selection (Runs when green box finishes moving)
        view.setOnLevelSelected(level -> {
            nav.goToStoryScreen(level); 
        });
    }
}