package Tetris.vue;

import javax.swing.*;
import java.awt.*;

public class GameBoardView extends JPanel{
    public GameBoardView(int width, int height, JPanel[][] cases) {
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(height, width, 0, 0));
        boardPanel.setPreferredSize(new Dimension(300, 900));
        for (int y = 0; y < cases.length; y++) {
            for (int x = 0; x < cases[y].length; x++) {
                cases[y][x] = new JPanel();
                cases[y][x].setBackground(Color.BLACK);
                cases[y][x].setPreferredSize(new Dimension(20, 20));
                boardPanel.setLayout(new GridLayout(30, 10, 0, 0));
                boardPanel.add(cases[y][x]);
            }
        }

        add(boardPanel);
        setFocusable(false);
    }
}
