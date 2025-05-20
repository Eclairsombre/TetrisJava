package Tetris.vue.Page;

import Tetris.vue.BasicComponent.Button;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class HomePage extends JPanel implements Observer {
    private final Button start1PGameButton;
    private final Button start2PGameButton;
    private final Button chooseMusicButton;

    public HomePage(Button start1PGameButton, Button start2PGameButton, Button chooseMusicButton) {
        setLayout(null);

        JLabel welcomeLabel = new JLabel("Bienvenue sur Tetris !", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(welcomeLabel);

        this.start1PGameButton = start1PGameButton;
        this.start2PGameButton = start2PGameButton;
        this.chooseMusicButton = chooseMusicButton;

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.start1PGameButton);
        buttonPanel.add(this.start2PGameButton);
        buttonPanel.add(this.chooseMusicButton);

        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        add(buttonPanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                Dimension size = getSize();
                welcomeLabel.setBounds(0, getHeight() / 4, size.width, 100);
                buttonPanel.setBounds(0, getHeight() - buttonPanel.getHeight(), size.width, 200);
            }
        });

        setVisible(true);
        welcomeLabel.setBounds(0, getHeight() / 4, 800, 100);
        buttonPanel.setBounds(0, getHeight() - buttonPanel.getHeight(), 800, 200);
    }

    @Override
    public void update(Observable o, Object arg) {
        // do nothing here
    }

    public void setButtonsVisibility(boolean enabled) {
        start1PGameButton.setVisible(enabled);
        start2PGameButton.setVisible(enabled);
        chooseMusicButton.setVisible(enabled);
    }
}
