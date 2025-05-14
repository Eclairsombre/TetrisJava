package Tetris.vue;

import javax.swing.*;
import java.awt.*;

public class PieceDisplayManager extends JPanel {
    public PieceDisplayManager(JPanel[][][] nextPiecesTabs, JPanel[][] holdPieceCells, int width, int height) {
        LayoutManager layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        JLabel holdPieceLabel = new JLabel("HOLD", SwingConstants.CENTER);
        holdPieceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(holdPieceLabel, BorderLayout.CENTER);

        PieceDisplayView holdPiecePanel = new PieceDisplayView(holdPieceCells, (int) (width * 1.5), (int) (height * 1.5));
        add(holdPiecePanel, BorderLayout.SOUTH);


        JPanel NPPanel = new JPanel();
        NPPanel.setLayout(new BoxLayout(NPPanel, BoxLayout.Y_AXIS));
        JLabel nextPieceLabel = new JLabel("NEXT", SwingConstants.CENTER);
        nextPieceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        NPPanel.add(nextPieceLabel, BorderLayout.CENTER);

        PieceDisplayView[] nextPiecePanel = new PieceDisplayView[3];
        for (int i = 0; i < 3; i++) {
            nextPiecePanel[i] = new PieceDisplayView(nextPiecesTabs[i], width, height);
            NPPanel.add(nextPiecePanel[i]);
        }
        add(NPPanel);
    }
}
