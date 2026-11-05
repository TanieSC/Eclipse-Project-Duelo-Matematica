package controller;

import model.AudioManager;
import view.StoryLevelsView;

public class StoryLevelsController {

    public StoryLevelsController(StoryLevelsView view, Navigator nav) {
    	
    	AudioManager.playBGM("mus_story.wav"); 
    	
        // Wire the UI buttons
        view.getPauseButton().setOnAction(e -> nav.goToGameStartMenu()); 
        view.getGearButton().setOnAction(e -> nav.goToSettings());

        // When a landmark is clicked, go to the DIALOGUE screen
        view.setOnLevelSelected(level -> {
            nav.goToStoryScreen(level); 
        });
    }
}