package Tetris.vue.BasicComponent;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MusicPlayer {
    private Clip clip;

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
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Boucle en continu
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
            clip.setFramePosition(0); // Rewind to the beginning
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }
}