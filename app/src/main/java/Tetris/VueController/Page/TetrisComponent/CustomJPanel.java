package Tetris.VueController.Page.TetrisComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * displays a colored border and background.
 */
public class CustomJPanel extends JPanel {
    private Color baseColor;

    CustomJPanel(Color color) {
        this.baseColor = color;
        setOpaque(true);
        setBackground(color);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateBorder();
            }
        });
    }

    public void updateBorder() {
        setPreferredSize(new Dimension(20, 20));

        super.setBackground(baseColor);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, getHeight() / 10),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(baseColor == Color.BLACK ? Color.BLACK : baseColor.darker().darker(), getHeight() / 8),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(0, 0, getHeight() / 8, 0, baseColor == Color.BLACK ? Color.BLACK : baseColor.darker().darker().darker()),
                                BorderFactory.createMatteBorder(getHeight() / 8, getHeight() / 8, getHeight() / 4, getHeight() / 4, baseColor == Color.BLACK ? Color.BLACK : baseColor.darker())
                        )
                )
        ));
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        this.baseColor = color;
        updateBorder();
    }
}
