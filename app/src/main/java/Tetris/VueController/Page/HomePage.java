package Tetris.VueController.Page;

import Tetris.VueController.BasicComponent.Button;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;

public class HomePage extends JPanel {
    private final Button[] buttons;

    public HomePage(Button start1PGameButton, Button start2PGameButton, Button chooseMusicButton) {
        setLayout(null);

        JLabel welcomeLabel = new JLabel("Bienvenue sur Tetris !", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(welcomeLabel);

        buttons = new Button[]{start1PGameButton, start2PGameButton, chooseMusicButton};

        JPanel buttonPanel = new JPanel();
        for (Button button : buttons) {
            buttonPanel.add(button);
        }
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
        // forced to set the size here to display labels correctly
        welcomeLabel.setBounds(0, getHeight() / 4, 800, 100);
        buttonPanel.setBounds(0, getHeight() - buttonPanel.getHeight(), 800, 200);
    }

    public void setButtonsVisibility(boolean enabled) {
        for (Button button : buttons) {
            button.setVisible(enabled);
        }
    }
}
