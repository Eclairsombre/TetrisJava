package Tetris.VueController.Page;

import Tetris.Model.Utils.StatsValues;
import Tetris.VueController.BasicComponent.Button;

import javax.swing.*;
import java.awt.*;

public class PopupPage extends JPanel {
    private final JLabel scoreLabel, levelLabel, timeLabel;

    /**
     * Constructor for the PopupPage class.
     * Displays a JPanel above the grid board with the specified text, color, and buttons.
     *
     * @param text            The text to display in the popup.
     * @param color           The color of the text.
     * @param statsValues     The StatsValues object containing the stats to display.
     * @param actionButton    The action button to be displayed.
     * @param backHomeButton  The button to go back to the home screen.
     */
    public PopupPage(String text, Color color, StatsValues statsValues, Button actionButton, Button backHomeButton) {
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

        scoreLabel = new JLabel("Score: " + statsValues.score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        levelLabel = new JLabel("Niveau: " + (statsValues.level.level()), SwingConstants.CENTER);
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

    /**
     * Update the stats displayed in the popup.
     *
     * @param statsValues The StatsValues object containing the updated stats.
     */
    public void updateStats(StatsValues statsValues) {
        scoreLabel.setText("Score : " + statsValues.score);
        levelLabel.setText("Niveau : " + (statsValues.level.level()));
        timeLabel.setText("Temps de jeu: : " + statsValues.getTime());
    }
}