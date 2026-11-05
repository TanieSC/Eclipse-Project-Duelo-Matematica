package model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * STORY MODEL — Holds the dialogue and text for the story mode.
 */
public class StoryModel {

    private static final Map<Integer, List<String>> STORY_DATABASE = new HashMap<>();

    static {
        STORY_DATABASE.put(1, Arrays.asList(
            "you are jobless...",
            "....",
            "...",
            "...",
            "you have 0 coins left in your pocket.",
            "a mysterious math warrior appears!",
            "prepare for your first battle!"
        ));
        STORY_DATABASE.put(2, Arrays.asList(
            "level 2 begins...",
            "you enter the local math tavern.",
            "the challenges are getting tougher.",
            "solve fast or go home broke!"
        ));
        STORY_DATABASE.put(3, Arrays.asList(
            "the ultimate math boss awaits...",
            "the final showdown of arithmetic mastery!",
            "victory or defeat!"
        ));
    }

    public List<String> getStoryLines(int level) {
        return STORY_DATABASE.getOrDefault(level, Arrays.asList("Story not found."));
    }
}