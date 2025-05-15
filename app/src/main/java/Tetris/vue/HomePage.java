package Tetris.vue;

import Tetris.controller.Game;
import Tetris.model.Grid;
import Tetris.vue.MusicChoosePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class HomePage extends JFrame implements Observer {
    private JLabel statusLabel;
    private Game game;
    private String musicPath = "data/music/tetris.wav"; // Chemin par défaut

    public HomePage(Game game) {
        setTitle("Accueil - Tetris");
        this.game = game;
        setSize(900, 1050);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Bienvenue sur Tetris !", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(welcomeLabel, BorderLayout.CENTER);

        JButton startButton = new JButton("Jouer");
        startButton.setFont(new Font("Arial", Font.PLAIN, 18));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        // Nouveau bouton pour choisir la musique
        JButton chooseMusicButton = new JButton("Choisir la musique");
        chooseMusicButton.setFont(new Font("Arial", Font.PLAIN, 18));
        chooseMusicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPath = MusicChoosePage.chooseMusic(HomePage.this);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(chooseMusicButton); // Ajout du bouton au panel
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void startGame() {
        game.startGame(); // Démarre les threads
        TetrisView tetrisView = new TetrisView(game,musicPath);
        SwingUtilities.invokeLater(tetrisView::start);
        game.getGrid().addObserver(tetrisView);

        dispose();
    }

    @Override
    public void update(Observable o, Object arg) {

    }
    public void start() {
        setVisible(true);
    }


}
