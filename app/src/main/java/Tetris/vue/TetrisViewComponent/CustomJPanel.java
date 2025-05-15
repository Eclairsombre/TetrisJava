package Tetris.vue.TetrisViewComponent;

import javax.swing.*;
import java.awt.*;

public class CustomJPanel extends JPanel {
    private static float ratio;

    CustomJPanel(Color color, float ratio) {
        this.ratio = ratio;
        this.setBackground(color);
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, getHeight() / 10),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color == Color.BLACK ? Color.BLACK : color.darker().darker(), getHeight() / 8),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(0, 0, getHeight() / 8, 0, color == Color.BLACK ? Color.BLACK : color.darker().darker().darker()),
                                BorderFactory.createMatteBorder(getHeight() / 8, getHeight() / 8, getHeight() / 4, getHeight() / 4, color == Color.BLACK ? Color.BLACK : color.darker())
                        )
                )
        ));
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        preferredSize.setSize(preferredSize.getWidth() * ratio, preferredSize.getHeight() * ratio);
        super.setSize(preferredSize);
    }
}
