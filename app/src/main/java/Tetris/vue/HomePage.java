package Tetris.vue;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class HomePage extends JPanel implements Observer {// Chemin par défaut

    public HomePage(Button startButton, Button chooseMusicButton) {
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Bienvenue sur Tetris !", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(welcomeLabel, BorderLayout.CENTER);

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
}
