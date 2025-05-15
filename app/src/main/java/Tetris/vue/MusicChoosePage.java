package Tetris.vue;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.prefs.Preferences;

public class MusicChoosePage extends JDialog {
    private String selectedMusicPath;
    private MusicPlayer previewPlayer;

    public MusicChoosePage(Frame parent) {
        super(parent, "Choisir la musique", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);

        File musicDir = new File("app/src/main/resources/data/music/");
        if (!musicDir.exists() || !musicDir.isDirectory()) {
            musicDir = new File("data/music/");
        }
        File[] files = musicDir.listFiles((dir, name) -> name.endsWith(".wav") || name.endsWith(".mp3"));
        String[] musicNames = (files != null && files.length > 0)
                ? java.util.Arrays.stream(files).map(File::getName).toArray(String[]::new)
                : new String[]{"Aucune musique trouvée"};

        JComboBox<String> comboBox = new JComboBox<>(musicNames);
        JButton playButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");
        JButton okButton = new JButton("OK");

        playButton.addActionListener(e -> {
            int idx = comboBox.getSelectedIndex();
            if (files != null && idx >= 0 && idx < files.length) {
                String path = "data/music/" + files[idx].getName();
                stopPreview();
                previewPlayer = new MusicPlayer(path);
                previewPlayer.play();
            }
        });

        stopButton.addActionListener(e -> stopPreview());

        okButton.addActionListener(e -> {
            int idx = comboBox.getSelectedIndex();
            if (files != null && idx >= 0 && idx < files.length) {
                selectedMusicPath = "data/music/" + files[idx].getName();
                saveMusicPath(selectedMusicPath);
            }
            stopPreview();
            setVisible(false);
        });

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Sélectionnez la musique de fond :"), BorderLayout.NORTH);
        topPanel.add(comboBox, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.add(playButton);
        controlPanel.add(stopButton);
        controlPanel.add(okButton);

        add(topPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void stopPreview() {
        if (previewPlayer != null) {
            previewPlayer.stop();
            previewPlayer = null;
        }
    }

    public static String chooseMusic(Frame parent) {
        // Toujours afficher la page de sélection
        MusicChoosePage dialog = new MusicChoosePage(parent);
        dialog.setVisible(true);
        return dialog.selectedMusicPath != null ? dialog.selectedMusicPath : "data/music/tetris.wav";
    }

    private void saveMusicPath(String path) {
        Preferences prefs = Preferences.userNodeForPackage(MusicChoosePage.class);
        prefs.put("selectedMusicPath", path);
    }
}
