package Tetris.VueController.Page.TetrisComponent;

import javax.swing.*;
import java.awt.*;

/**
 * PieceDisplayView is a custom JPanel that displays the next pieces in a Tetris game.
 * It uses a grid layout to arrange the pieces in a 4x4 grid.
 */
public class PieceDisplayView extends JPanel {
    /**
     * Constructor for PieceDisplayView.
     *
     * @param nextPieceCells
     * @param width
     * @param height
     * @param ratio
     */
    public PieceDisplayView(JPanel[][] nextPieceCells, int width, int height, float ratio) {
        int length = 4;
        JPanel npPanel = new JPanel();
        npPanel.setLayout(new GridLayout(length, length, 0, 0));
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                nextPieceCells[j][i] = new CustomJPanel(Color.BLACK);
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
                Math.round(width * length * ratio),
                Math.round(height * length * ratio)
        ));
        add(npPanel);
        setFocusable(false);
    }
}
