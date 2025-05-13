package Tetris.vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Tetris.model.Grid;

public class Vue extends JFrame implements Observer {
    private final JPanel[][] cases;
    private final Grid grid;
    private final JPanel screen = new JPanel();

    public Vue(Grid m) {
        setTitle("Color Change Example");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        this.grid = m;
        cases = new JPanel[grid.getHeight()][grid.getWidth()];
        for (int y = 0; y < cases.length; y++) {
            for (int x = 0; x < cases[y].length; x++) {
                cases[y][x] = new JPanel();
                cases[y][x].setBackground(getColorCell(x, y));
                cases[y][x].setPreferredSize(new Dimension(50, 50));
                panel.add(cases[y][x]);
            }
        }
        panel.setLayout(new java.awt.GridLayout(grid.getHeight(), grid.getWidth()));
        panel.setPreferredSize(new Dimension(400, 800));

        JPanel scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(200, 200));
        scorePanel.setLayout(new BorderLayout());
        JLabel scoreLabel = new JLabel("Score : 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scorePanel.add(scoreLabel, BorderLayout.CENTER);

        JPanel nextPiecePanel = new JPanel();
        nextPiecePanel.setPreferredSize(new Dimension(200, 200));
        nextPiecePanel.setLayout(new BorderLayout());
        JLabel nextPieceLabel = new JLabel("Prochaine pièce", SwingConstants.CENTER);
        nextPieceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nextPiecePanel.add(nextPieceLabel, BorderLayout.CENTER);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                JPanel nextPieceCell = new JPanel();
                nextPieceCell.setBackground((i + j) % 2 == 0 ? Color.GRAY : Color.LIGHT_GRAY);
                nextPieceCell.setMinimumSize(new Dimension(50, 50));
                nextPieceCell.setPreferredSize(new Dimension(50, 50));
                nextPieceCell.setMaximumSize(new Dimension(50, 50));
                nextPiecePanel.add(nextPieceCell);
            }
        }

        screen.setLayout(new BorderLayout());
        screen.add(panel, BorderLayout.CENTER);
        screen.add(scorePanel, BorderLayout.EAST);
        screen.add(nextPiecePanel, BorderLayout.WEST);
        add(screen);
        setVisible(true);
    }

    public Color getColorCell(int x, int y) {
        String res = grid.getCell(x, y);
        switch (res) {
            case "red":
                return Color.RED;
            case "green":
                return Color.GREEN;
            case "blue":
                return Color.BLUE;
            case "yellow":
                return Color.YELLOW;
            case "pink":
                return Color.PINK;
            default:
                return Color.BLACK;
        }
    }

    public void start() {
        grid.addObserver(this);
        setVisible(true);
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                cases[i][j].setBackground(getColorCell(i, j));
            }
        }
    }
}
