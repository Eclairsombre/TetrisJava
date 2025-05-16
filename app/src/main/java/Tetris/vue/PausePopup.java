package Tetris.vue;

import Tetris.controller.Game;
import Tetris.model.Grid;
import Tetris.model.StatsValues;

import javax.swing.*;
import java.awt.*;

public class PausePopup extends JDialog {
    private final TetrisView parentView;
    private final JLabel scoreLabel, levelLabel, timeLabel;

    public PausePopup(TetrisView parent, Game game) {
        super(parent, "Pause", true);
        this.parentView = parent;
        setFocusable(false);

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JLabel gameOverLabel = new JLabel("PAUSE", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 36));
        gameOverLabel.setForeground(Color.BLACK);
        mainPanel.add(gameOverLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        StatsValues statsValues = game.getStatsValues();


        scoreLabel = new JLabel("Score: " + statsValues.score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        levelLabel = new JLabel("Niveau: " + (statsValues.level.getLevel() + 1), SwingConstants.CENTER);
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

        Button resumeButton = new Button("Resume", () -> {
            setVisible(false);

            game.resumeGame();
        });

        Button menuButton = new Button("Menu Principal", () -> {
            dispose();
            parentView.getMusicPlayer().stop();
            parentView.dispose();
            // TODO : try to avoid creating a new game object and import Game
            Game newGame = new Game(new Grid(game.getLengthGrid()[0], game.getLengthGrid()[1]));
            HomePage homePage = new HomePage(newGame);
        });

        buttonPanel.add(resumeButton);
        buttonPanel.add(menuButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public void updateStats(StatsValues statsValues) {
        scoreLabel.setText("Score : " + statsValues.score);
        levelLabel.setText("Niveau : " + (statsValues.level.getLevel() + 1));
        timeLabel.setText("Temps de jeu: : " + statsValues.getTime());
    }
}