package Tetris.vue;

import javax.swing.*;
import java.awt.*;

public class PieceDisplayView extends JPanel {
    public PieceDisplayView(JPanel[][] nextPieceCells) {
        setPreferredSize(new Dimension(150, 150));
        setLayout(new BorderLayout());

        JPanel npPanel = new JPanel();
        npPanel.setLayout(new GridLayout(4, 4, 0, 0));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextPieceCells[j][i] = new JPanel();
                nextPieceCells[j][i].setBackground(Color.LIGHT_GRAY);
                nextPieceCells[j][i].setPreferredSize(new Dimension(40, 40));
                npPanel.add(nextPieceCells[j][i]);
            }
        }

        JPanel templatePanel = new JPanel();
        templatePanel.add(npPanel);
        add(templatePanel);
        setFocusable(false);
    }
}
