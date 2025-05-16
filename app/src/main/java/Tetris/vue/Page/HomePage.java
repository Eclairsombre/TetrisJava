package Tetris.vue.Page;

import Tetris.vue.BasicComponent.Button;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class HomePage extends JPanel implements Observer {
    public HomePage(Tetris.vue.BasicComponent.Button startButton, Button chooseMusicButton) {
        setLayout(null);

        JLabel welcomeLabel = new JLabel("Bienvenue sur Tetris !", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(welcomeLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(chooseMusicButton);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        add(buttonPanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                Dimension size = getSize();
                welcomeLabel.setBounds(0, getHeight()/4, size.width, 100);
                buttonPanel.setBounds(0, getHeight() - buttonPanel.getHeight(), size.width, 150);
            }
        });

        setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        // do nothing here
    }
}
