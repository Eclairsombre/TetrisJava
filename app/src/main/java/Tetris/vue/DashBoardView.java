package Tetris.vue;

import javax.swing.*;
import java.awt.*;

public class DashBoardView extends JPanel {
    private final JLabel scoreLabel;

    public DashBoardView() {
        // Panel score
        JPanel scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(200, 100));
        scoreLabel = new JLabel("Score : 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scorePanel.add(scoreLabel, BorderLayout.CENTER);
        scorePanel.setFocusable(false);
        add(scorePanel);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score : " + score);
    }
}
