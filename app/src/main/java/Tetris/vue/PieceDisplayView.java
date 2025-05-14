package Tetris.vue;

import javax.swing.*;
import java.awt.*;

public class PieceDisplayView extends JPanel {
    public PieceDisplayView(JPanel[][] nextPieceCells, int width, int height) {
        JPanel npPanel = new JPanel();
        npPanel.setLayout(new GridLayout(4, 4, 0, 0));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextPieceCells[j][i] = new CustomJPanel(Color.BLACK);
                nextPieceCells[j][i].setPreferredSize(new Dimension(width, height));
                npPanel.add(nextPieceCells[j][i]);
            }
        }

        JPanel templatePanel = new JPanel();
        templatePanel.add(npPanel);
        add(templatePanel);
        setFocusable(false);
    }
}
