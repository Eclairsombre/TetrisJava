package Tetris.VueController.Page;

import Tetris.Model.Utils.StatsValues;
import Tetris.VueController.BasicComponent.Button;

import javax.swing.*;
import java.awt.*;

/**
 * PopupPage class represents a popup window that displays game statistics and buttons.
 * It extends JPanel and is used to show information such as score, level, and time.
 */
public class PopupPage extends JPanel {
    /// @param scoreLabel   The label to display the score.
    /// @param levelLabel   The label to display the level.
    /// @param timeLabel    The label to display the time.

    /**
     * Constructor for the PopupPage class.
     * Displays a JPanel above the grid board with the specified text, color, and buttons.
     *
     * @param text           The text to display in the popup.
     * @param color          The color of the text.
     * @param actionButton   The action button to be displayed.
     * @param backHomeButton The button to go back to the home screen.
     */
    public PopupPage(String text, Color color, Button actionButton, Button backHomeButton) {
        setFocusable(false); // To avoid focus issues
        setSize(300, 200);

        JLabel gameOverLabel = new JLabel(text, SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 36));
        gameOverLabel.setForeground(color);

        setLayout(new GridLayout(3, 1, 0, 10));
        add(gameOverLabel);
        add(actionButton);
        add(backHomeButton);
    }
}