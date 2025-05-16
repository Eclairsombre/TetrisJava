package Tetris.vue;

import Tetris.controller.Game;
import Tetris.model.Grid;
import Tetris.model.StatsValues;

import javax.swing.*;
import java.awt.*;

public class GameOverPopup extends JDialog {
    private final TetrisView parentView;

    public GameOverPopup(TetrisView parent, Game game) {
        super(parent, "Game Over", true);
        this.parentView = parent;
        setFocusable(false); // To avoid focus issues

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JLabel gameOverLabel = new JLabel("GAME OVER", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 36));
        gameOverLabel.setForeground(Color.RED);
        mainPanel.add(gameOverLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        StatsValues statsValues = game.getStatsValues();

        JLabel scoreLabel = new JLabel("Score: " + statsValues.score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel levelLabel = new JLabel("Niveau: " + (statsValues.level.getLevel() + 1), SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 24));
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel timeLabel = new JLabel("Temps de jeu: " + statsValues.getTime(), SwingConstants.CENTER);
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

        Button retryButton = new Button("Rejouer", () -> {
            // TODO : seems to have sometimes two Threads scheduler running at the same time when restart
            // TODO : also have sometimes two screen of the gameOverPopup
            // I think it come from the move piece endGame call which is not thread safe
            setVisible(false);
            game.reset();
        });

        Button menuButton = new Button("Menu Principal", () -> {
            dispose();
            parentView.getMusicPlayer().stop();
            parentView.dispose();
            // TODO : try to avoid creating a new game object and import Game
            Game newGame = new Game(new Grid(game.getLengthGrid()[0], game.getLengthGrid()[1]));
            HomePage homePage = new HomePage(newGame);
            homePage.start();
        });

        buttonPanel.add(retryButton);
        buttonPanel.add(menuButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}