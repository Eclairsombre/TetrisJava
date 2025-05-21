package Tetris.VueController.Page;

import Tetris.VueController.BasicComponent.Button;
import Tetris.VueController.BasicComponent.MusicPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.prefs.Preferences;

/**
 * Displays a list of available music files and provides buttons to play, stop, and confirm the selection.
 */
public class MusicChoosePopup extends JPanel {
    private MusicPlayer previewPlayer;
    /// @param previewPlayer  The MusicPlayer instance used for previewing music.
    private String paths = "data/music/TetrisOST.wav";
    /// @param paths  The path to the selected music file.

    /**
     * Constructor for MusicChoosePopup.
     *
     * @param returnToMenu Runnable to execute when the user confirms their selection.
     */
    public MusicChoosePopup(Runnable returnToMenu) {
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

        Tetris.VueController.BasicComponent.Button playButton = new Tetris.VueController.BasicComponent.Button("Play", () -> {
            int idx = table.getSelectedRow();
            if (files != null && idx >= 0 && idx < files.length) {
                String path = "data/music/" + files[idx].getName();
                stopPreview();
                previewPlayer = new MusicPlayer(path);
                previewPlayer.play();
            }
        });

        Tetris.VueController.BasicComponent.Button stopButton = new Tetris.VueController.BasicComponent.Button("Stop", this::stopPreview);

        Tetris.VueController.BasicComponent.Button okButton = new Button("OK", () -> {
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
        Preferences prefs = Preferences.userNodeForPackage(MusicChoosePopup.class);
        prefs.put("selectedMusicPath", path);
    }

    public String getPath() {
        return paths;
    }
}
