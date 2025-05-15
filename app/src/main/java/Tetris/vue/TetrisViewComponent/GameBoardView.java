package Tetris.vue.TetrisViewComponent;

import javax.swing.*;
import java.awt.*;

public class GameBoardView extends JPanel {
    JPanel[][] cases;
    int width;
    int height;
    JPanel boardPanel;
    int ratio = 14;
    int offset = 20;

    public GameBoardView(int width, int height, JPanel[][] cases, Color backgroundColor) {
        this.cases = cases;
        this.width = width;
        this.height = height;
        setLayout(new GridBagLayout());
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(height, width, 0, 0));
        for (int y = 0; y < cases.length; y++) {
            for (int x = 0; x < cases[y].length; x++) {
                cases[y][x] = new CustomJPanel(Color.BLACK, 1);
                boardPanel.add(cases[y][x]);
            }
        }
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 3),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.DARK_GRAY, 8),
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3)
                )
        ));
        add(boardPanel);
        setBackground(backgroundColor);
        setFocusable(false);
    }

    @Override
    public void setSize(int width, int height) {
        int length = Math.min(getHeight(), height);
        int new_height;
        int new_width = length / ratio * this.width;
        if (new_width > getWidth()) {
            length = getWidth();
            new_width = length / ratio * this.width; // we change the length so we have to recalculate the width
        }
        new_height = length / ratio * this.height;
        boardPanel.setPreferredSize(new Dimension(
                new_width,
                new_height - offset // random offset to make it look good
        ));
        revalidate();
    }

    @Override
    public void paint(Graphics g) {
        setSize(getSize().width / 2, getSize().height / 2);
        super.paint(g);
    }
}
