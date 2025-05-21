package Tetris.VueController.BasicComponent;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class to play background music.
 */
public class MusicPlayer {
    private Clip clip;
    /// clip the clip to play

    /**
     * Constructor to initialize the music player with a file path.
     *
     * @param filePath the path to the audio file
     */
    public MusicPlayer(String filePath) {
        try {
            InputStream audioStream = getClass().getClassLoader().getResourceAsStream(filePath);
            if (audioStream == null) {
                throw new IOException("Fichier audio introuvable : " + filePath);
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(audioStream));
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erreur lors de la lecture du fichier audio : " + e.getMessage());
        }
    }

    public void play() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void reset() {
        if (clip != null) {
            clip.setFramePosition(0);
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }
}