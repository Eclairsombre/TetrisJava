package Tetris.VueController.BasicComponent;

import javax.swing.*;
import java.awt.*;

/**
 * Contains a JButton with a specified text and action.
 */
public class Button extends JPanel {
    /**
     * Constructs a Button with the specified text and action.
     *
     * @param text the text to display on the button
     * @param runnable the action to perform when the button is clicked
     */
    public Button(String text, Runnable runnable) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.addActionListener(e -> runnable.run());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(button);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}
