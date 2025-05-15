package Tetris.vue.TetrisViewComponent;

import javax.swing.*;
import java.awt.*;

public class TextView extends JPanel {
    public TextView(int fontSize, Color color, JLabel label) {
        setLayout(new BorderLayout());
        setBackground(color);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        add(label, BorderLayout.CENTER);
        setFocusable(false);
        setBackground(color);
    }
}
