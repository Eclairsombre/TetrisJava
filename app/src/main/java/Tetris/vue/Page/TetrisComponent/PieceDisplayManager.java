package Tetris.vue.Page.TetrisComponent;

import Tetris.model.FileWriterAndReader;

import javax.swing.*;
import java.awt.*;

/**
 * PieceDisplayManager is a custom JPanel that displays the next pieces and the hold piece.
 */
public class PieceDisplayManager extends JPanel {
    FileWriterAndReader fileWriterAndReader;
    JPanel scorePanel = new JPanel();
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
