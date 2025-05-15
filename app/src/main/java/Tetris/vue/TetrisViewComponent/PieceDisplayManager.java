package Tetris.vue.TetrisViewComponent;

import javax.swing.*;
import java.awt.*;

public class PieceDisplayManager extends JPanel {
    public PieceDisplayManager(JPanel[][][] nextPiecesTabs, JPanel[][] holdPieceCells, int width, int height, Color background, String[] scores) {
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
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(5, 1));
        scorePanel.setBorder(BorderFactory.createTitledBorder("Meilleurs Scores"));
        scorePanel.setBackground(Color.LIGHT_GRAY);
        scorePanel.setBounds(600, 750, 230, 100);
        add(scorePanel);

        for (int i = 0; i < Math.min(5, scores.length); i++) {
            JLabel scoreLabel = new JLabel(scores[i]);
            scorePanel.add(scoreLabel);
        }

        setBackground(background);
    }
}
