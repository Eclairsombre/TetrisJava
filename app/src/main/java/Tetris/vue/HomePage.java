package Tetris.vue;

import Tetris.controller.Game;
import Tetris.model.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class HomePage extends JFrame implements Observer {
    private JLabel statusLabel;

    public HomePage(Game game) {
        setTitle("Accueil - Tetris");
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
                game.startGame(); // Démarre les threads
                TetrisView tetrisView = new TetrisView(game);
                SwingUtilities.invokeLater(tetrisView::start);
                game.getGrid().addObserver(tetrisView);

                dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
    public void start() {
        setVisible(true);
    }


}
