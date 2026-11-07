package model;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    private static MediaPlayer bgmPlayer;
    private static double bgmVolume = 0.5;
    private static double sfxVolume = 0.5;

    // Cache to keep SFX in RAM for instant playback
    private static final Map<String, AudioClip> sfxCache = new HashMap<>();

    /**
     * CALL THIS ONCE IN YOUR MAIN CLASS BEFORE STAGE.SHOW()
     * This loads the audio from the disk into RAM so there is no delay on click.
     */
    public static void preloadSfx() {
        String[] ui_sounds = {
            "sfx_button_hover.wav",
            "sfx_button_pressed.wav",
            "sfx_paper.wav",
            "sfx_wax_break.wav"
        };
        
        for (String sound : ui_sounds) {
            try {
                var resource = AudioManager.class.getResource("/_assets/audio/sfx/ui/" + sound);
                if (resource != null) {
                    sfxCache.put(sound, new AudioClip(resource.toExternalForm()));
                } else {
                    System.err.println("SFX not found: " + sound);
                }
            } catch (Exception e) {
                System.err.println("Error preloading " + sound + ": " + e.getMessage());
            }
        }
        
        String[] combat_sounds = {
                "sfx_hit.wav",
                "sfx_death.wav"
            };

        for (String sound : combat_sounds) {
            try {
                var resource = AudioManager.class.getResource("/_assets/audio/sfx/combat/" + sound);
                if (resource != null) {
                    sfxCache.put(sound, new AudioClip(resource.toExternalForm()));
                } else {
                    System.err.println("SFX not found: " + sound);
                }
            } catch (Exception e) {
                System.err.println("Error preloading " + sound + ": " + e.getMessage());
            }
        }
    }

    // Play Sound Effects (Now instant!)
    public static void playSFX(String fileName) {
        AudioClip clip = sfxCache.get(fileName);
        
       // printCache();
        
        if (clip != null) {
            clip.setVolume(sfxVolume);
            clip.play();
        } 
    }

    public static void printCache() {
        System.out.println("--- SFX CACHE CONTENTS ---");
        if (sfxCache.isEmpty()) {
            System.out.println("Cache is empty!");
            return;
        }

        // Loop through the Map and print each key and value
        sfxCache.forEach((key, clip) -> {
            System.out.println("Key: [" + key + "] -> Object: " + clip);
        });
        System.out.println("--------------------------");
    }
    
    // ... (Keep playBGM, stopBGM, setBgmVolume, setSfxVolume as they were) ...
    
    public static void playBGM(String fileName) {
        try {
            if (bgmPlayer != null) {
                bgmPlayer.stop();
                bgmPlayer.dispose();
            }
            var resource = AudioManager.class.getResource("/_assets/audio/music/" + fileName);
            if (resource == null) return;

            Media media = new Media(resource.toExternalForm());
            bgmPlayer = new MediaPlayer(media);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.setVolume(bgmVolume);
            bgmPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing BGM: " + e.getMessage());
        }
    }

    public static void stopBGM() {
        if (bgmPlayer != null) bgmPlayer.stop();
    }

    public static void setBgmVolume(double volume) {
        bgmVolume = volume;
        if (bgmPlayer != null) bgmPlayer.setVolume(bgmVolume);
    }

    public static void setSfxVolume(double volume) {
        sfxVolume = volume;
    }
}