package Tetris.vue.Page;

import Tetris.vue.BasicComponent.Button;
import Tetris.vue.BasicComponent.MusicPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.prefs.Preferences;

public class MusicChoosePage extends JPanel {
    private MusicPlayer previewPlayer;
    private String paths = "data/music/TetrisOST.wav";

    public MusicChoosePage(Runnable returnToMenu) {
        setSize(400, 250);
        File musicDir = new File("app/src/main/resources/data/music/");
        if (!musicDir.exists() || !musicDir.isDirectory()) {
            musicDir = new File("data/music/");
        }
        File[] files = musicDir.listFiles((dir, name) -> name.endsWith(".wav") || name.endsWith(".mp3"));
        String[] columnNames = {"Nom du fichier"};
        Object[][] data = (files != null && files.length > 0)
                ? java.util.Arrays.stream(files).map(f -> new Object[]{f.getName()}).toArray(Object[][]::new)
                : new Object[][]{{"Aucune musique trouvée"}};

        JTable table = new JTable(data, columnNames);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane comboBox = new JScrollPane(table);

        Tetris.vue.BasicComponent.Button playButton = new Tetris.vue.BasicComponent.Button("Play", () -> {
            int idx = table.getSelectedRow();
            if (files != null && idx >= 0 && idx < files.length) {
                String path = "data/music/" + files[idx].getName();
                stopPreview();
                previewPlayer = new MusicPlayer(path);
                previewPlayer.play();
            }
        });

        Tetris.vue.BasicComponent.Button stopButton = new Tetris.vue.BasicComponent.Button("Stop", this::stopPreview);

        Tetris.vue.BasicComponent.Button okButton = new Button("OK", () -> {
            int idx = table.getSelectedRow();
            if (files != null && idx >= 0 && idx < files.length) {
                saveMusicPath("data/music/" + files[idx].getName());
                paths = "data/music/" + files[idx].getName();
            }
            stopPreview();
            returnToMenu.run();
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

    private void saveMusicPath(String path) {
        Preferences prefs = Preferences.userNodeForPackage(MusicChoosePage.class);
        prefs.put("selectedMusicPath", path);
    }

    public String getPath() {
        return paths;
    }
}
