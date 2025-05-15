package Tetris.vue.TetrisViewComponent;

import javax.swing.*;
import java.awt.*;

public class PieceDisplayView extends JPanel {
    public PieceDisplayView(JPanel[][] nextPieceCells, int width, int height, float ratio) {
        JPanel npPanel = new JPanel();
        npPanel.setLayout(new GridLayout(4, 4, 0, 0));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextPieceCells[j][i] = new CustomJPanel(Color.BLACK, ratio);
                nextPieceCells[j][i].setPreferredSize(new Dimension(width, height));
                npPanel.add(nextPieceCells[j][i]);
            }
        }

        npPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 3),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.DARK_GRAY, 8),
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3)
                )
        ));
        npPanel.setPreferredSize(new Dimension(
                Math.round(width * 4 * ratio),
                Math.round(height * 4 * ratio)
        ));
        add(npPanel);
        setFocusable(false);
    }
}
