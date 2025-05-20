package Tetris.vue.Page;

import Tetris.controller.Game;
import Tetris.model.StatsValues;
import Tetris.vue.BasicComponent.Button;

import javax.swing.*;
import java.awt.*;

public class PopupPage extends JPanel {
    private final JLabel scoreLabel, levelLabel, timeLabel;

    public PopupPage(String text, Color color, Game game, Tetris.vue.BasicComponent.Button actionButton, Button backHomeButton) {
        setFocusable(false); // To avoid focus issues

        setSize(400, 250);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JLabel gameOverLabel = new JLabel(text, SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 36));
        gameOverLabel.setForeground(color);
        mainPanel.add(gameOverLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        StatsValues statsValues = game.getStatsValues();

        scoreLabel = new JLabel("Score: " + statsValues.score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        levelLabel = new JLabel("Niveau: " + (statsValues.level.getLevel()), SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 24));
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timeLabel = new JLabel("Temps de jeu: " + statsValues.getTime(), SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(scoreLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(levelLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(timeLabel);
        infoPanel.add(Box.createVerticalGlue());

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        buttonPanel.add(actionButton);
        buttonPanel.add(backHomeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public void updateStats(StatsValues statsValues) {
        scoreLabel.setText("Score : " + statsValues.score);
        levelLabel.setText("Niveau : " + (statsValues.level.getLevel()));
        timeLabel.setText("Temps de jeu: : " + statsValues.getTime());
    }
}