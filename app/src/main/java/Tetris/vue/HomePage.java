package Tetris.vue;

import Tetris.controller.Game;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class HomePage extends JFrame implements Observer {
    private String musicPath = "data/music/tetris.wav"; // Chemin par défaut

    public HomePage(Game game) {
        setTitle("Accueil - Tetris");
        setSize(900, 1050);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Bienvenue sur Tetris !", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(welcomeLabel, BorderLayout.CENTER);

        Button startButton = new Button("Jouer", () -> {
            game.startGame(); // Start threads
            TetrisView tetrisView = new TetrisView(game, musicPath);
            SwingUtilities.invokeLater(tetrisView::start);
            game.getGrid().addObserver(tetrisView);
            dispose();
        });

        Button chooseMusicButton = new Button("Choisir la musique", () ->
                musicPath = MusicChoosePage.chooseMusic(HomePage.this));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(chooseMusicButton);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        // do nothing here
    }

    public void start() {
        setVisible(true);
    }
}
