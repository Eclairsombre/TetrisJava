package Tetris.vue;

import Tetris.controller.Game;
import Tetris.model.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameOverPopup extends JDialog {

    private final Game game;
    private final TetrisView parentView;

    public GameOverPopup(TetrisView parent, Game game) {
        super(parent, "Game Over", true);
        this.game = game;
        this.parentView = parent;

        // Configuration de la fenêtre
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Création du contenu
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Message de game over
        JLabel gameOverLabel = new JLabel("GAME OVER", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 36));
        gameOverLabel.setForeground(Color.RED);
        mainPanel.add(gameOverLabel, BorderLayout.NORTH);

        // Informations sur le score
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel scoreLabel = new JLabel("Score: " + game.getGrid().getScore(), SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel levelLabel = new JLabel("Niveau: " + (game.getGrid().getLevel().getLevel() + 1), SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 24));
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel timeLabel = new JLabel("Temps de jeu: " + game.getGrid().getTime(), SwingConstants.CENTER);
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

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton retryButton = new JButton("Rejouer");
        retryButton.setFont(new Font("Arial", Font.PLAIN, 18));
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.reset();
                dispose();
            }
        });

        JButton menuButton = new JButton("Menu Principal");
        menuButton.setFont(new Font("Arial", Font.PLAIN, 18));
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fermer la fenêtre de game over
                dispose();
                parentView.dispose();

                // Créer une nouvelle instance de jeu
                Game newGame = new Game(new Grid(game.getGrid().getWidth(), game.getGrid().getHeight()));
                HomePage homePage = new HomePage(newGame);
                homePage.start();
            }
        });

        buttonPanel.add(retryButton);
        buttonPanel.add(menuButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}