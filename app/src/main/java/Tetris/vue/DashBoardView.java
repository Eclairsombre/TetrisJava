package Tetris.vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashBoardView extends JPanel {
    private final JLabel scoreLabel, levelLabel, timerLabel;

    public DashBoardView() {
        // Panel score
        JPanel scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(200, 100));
        scoreLabel = new JLabel("Score : 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scorePanel.add(scoreLabel, BorderLayout.CENTER);
        scorePanel.setFocusable(false);
        add(scorePanel);

        // Panel level
        JPanel levelPanel = new JPanel();
        levelPanel.setPreferredSize(new Dimension(200, 100));
        levelLabel = new JLabel("Niveau : 1", SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 24));
        levelPanel.add(levelLabel, BorderLayout.CENTER);
        levelPanel.setFocusable(false);
        add(levelPanel);

        // Panel timer
        JPanel timerPanel = new JPanel();
        timerPanel.setPreferredSize(new Dimension(300, 100));
        timerLabel = new JLabel("Temps écoulé : 00:00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerPanel.add(timerLabel, BorderLayout.CENTER);
        timerPanel.setFocusable(false);
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
