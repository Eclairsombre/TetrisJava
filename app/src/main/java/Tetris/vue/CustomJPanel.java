package Tetris.vue;

import javax.swing.*;
import java.awt.*;

public class CustomJPanel extends JPanel {
    CustomJPanel(Color color) {
        this.setBackground(color);
    }

    private Color editDarkerRGB(Color color) {
        int red = (int) (color.getRed() * 0.8);
        int green = (int) (color.getGreen() * 0.8);
        int blue = (int) (color.getBlue() * 0.8);
        return new Color(red, green, blue);
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, getHeight() / 10),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color == Color.BLACK ? Color.BLACK : editDarkerRGB(editDarkerRGB(color)), getHeight() / 8),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(0, 0, getHeight() / 8, 0, color == Color.BLACK ? Color.BLACK : editDarkerRGB(editDarkerRGB(editDarkerRGB(color)))),
                                BorderFactory.createMatteBorder(getHeight() / 8, getHeight() / 8, getHeight() / 4, getHeight() / 4, color == Color.BLACK ? Color.BLACK : editDarkerRGB(color))
                        )
                )
        ));
    }
}
