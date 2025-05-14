package Tetris.vue.TetrisViewComponent;

import javax.swing.*;
import java.awt.*;

public class DashBoardView extends JPanel {
    private final JLabel scoreLabel, levelLabel, timerLabel;

    public DashBoardView(Color backgroundColor) {
        // Panel score
        setBackground(backgroundColor);

        JPanel scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(200, 100));
        scoreLabel = new JLabel("Score : 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scorePanel.add(scoreLabel, BorderLayout.CENTER);
        scorePanel.setFocusable(false);
        scorePanel.setBackground(backgroundColor);
        add(scorePanel);

        // Panel level
        JPanel levelPanel = new JPanel();
        levelPanel.setPreferredSize(new Dimension(200, 100));
        levelLabel = new JLabel("Niveau : 1", SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 24));
        levelPanel.add(levelLabel, BorderLayout.CENTER);
        levelPanel.setFocusable(false);
        levelPanel.setBackground(backgroundColor);
        add(levelPanel);

        // Panel timer
        JPanel timerPanel = new JPanel();
        timerPanel.setPreferredSize(new Dimension(300, 100));
        timerLabel = new JLabel("Temps écoulé : 00:00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerPanel.add(timerLabel, BorderLayout.CENTER);
        timerPanel.setFocusable(false);
        timerPanel.setBackground(backgroundColor);
        add(timerPanel);

    }

    public void updateScore(int score) {
        scoreLabel.setText("Score : " + score);
    }
    public void updateLevel(int level) {
        levelLabel.setText("Niveau : " + (level+1));
    }
    public void updateTimer(String time) {
        timerLabel.setText("Temps écoulé : " + time);
    }

}
