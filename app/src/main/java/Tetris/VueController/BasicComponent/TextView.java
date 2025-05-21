package Tetris.VueController.BasicComponent;

import javax.swing.*;
import java.awt.*;

/**
 * TextView is a custom JPanel that displays a JLabel with a specified font size and background color.
 */
public class TextView extends JPanel {
    /**
     * Constructor for TextView.
     *
     * @param fontSize the font size of the text
     * @param color    the background color of the panel
     * @param label    the JLabel to be displayed
     */
    public TextView(int fontSize, Color color, JLabel label) {
        setLayout(new BorderLayout());
        setBackground(color);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        add(label, BorderLayout.CENTER);
        setFocusable(false);
        setBackground(color);
    }
}
