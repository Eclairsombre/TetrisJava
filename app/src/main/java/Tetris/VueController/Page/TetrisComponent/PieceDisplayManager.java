package Tetris.VueController.Page.TetrisComponent;

import Tetris.Model.Utils.FileWriterAndReader;

import javax.swing.*;
import java.awt.*;

/**
 * PieceDisplayManager is a custom JPanel that displays the next pieces and the hold piece.
 */
public class PieceDisplayManager extends JPanel {
    private final FileWriterAndReader fileWriterAndReader;
    /// @param fileWriterAndReader FileWriterAndReader instance to read and write scores
    private final JPanel scorePanel = new JPanel();
    /// @param scorePanel JPanel to display the best scores

    /**
     * Constructor for PieceDisplayManager.
     *
     * @param nextPiecesTabs the next pieces to be displayed
     * @param holdPieceCells the hold piece cells
     * @param width          the width of the cells
     * @param height         the height of the cells
     * @param background     the background color of the panel
     * @param FWAR           the FileWriterAndReader instance to read and write scores
     */
    public PieceDisplayManager(JPanel[][][] nextPiecesTabs, JPanel[][] holdPieceCells, int width, int height, Color background, FileWriterAndReader FWAR) {
        LayoutManager layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        JLabel holdPieceLabel = new JLabel("HOLD", SwingConstants.CENTER);
        holdPieceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        holdPieceLabel.setBackground(background);
        add(holdPieceLabel, BorderLayout.CENTER);

        PieceDisplayView holdPiecePanel = new PieceDisplayView(holdPieceCells, width, height, 1.5f);
        holdPiecePanel.setBackground(background);
        add(holdPiecePanel, BorderLayout.SOUTH);

        JPanel NPPanel = new JPanel();
        NPPanel.setLayout(new BoxLayout(NPPanel, BoxLayout.Y_AXIS));
        JLabel nextPieceLabel = new JLabel("NEXT", SwingConstants.CENTER);
        nextPieceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        NPPanel.add(nextPieceLabel, BorderLayout.CENTER);

        PieceDisplayView[] nextPiecePanel = new PieceDisplayView[3];
        for (int i = 0; i < 3; i++) {
            nextPiecePanel[i] = new PieceDisplayView(nextPiecesTabs[i], width, height, 1);
            nextPiecePanel[i].setBackground(background);
            NPPanel.add(nextPiecePanel[i]);
        }
        NPPanel.setBackground(background);

        add(NPPanel);
        scorePanel.setLayout(new GridLayout(5, 1));
        scorePanel.setBorder(BorderFactory.createTitledBorder("Meilleurs Scores"));
        scorePanel.setBackground(Color.LIGHT_GRAY);
        scorePanel.setBounds(600, 750, 230, 100);
        add(scorePanel);

        fileWriterAndReader = FWAR;
        updateBestScores();

        setBackground(background);
    }

    /**
     * Updates the best scores displayed in the score panel.
     */
    public void updateBestScores() {
        scorePanel.removeAll();
        String[] scores = fileWriterAndReader.readFromFile();
        for (String score : scores) {
            JLabel scoreLabel = new JLabel(score);
            scorePanel.add(scoreLabel);
        }
        repaint();
        revalidate();
    }
}
