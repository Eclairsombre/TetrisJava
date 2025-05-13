package Tetris.vue;

import java.awt.*;
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
        setTitle("Tetris");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.grid = m;
        cases = new JPanel[grid.getHeight()][grid.getWidth()];

// Plateau de jeu (grille Tetris)
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(grid.getHeight(), grid.getWidth(), 0, 0));
        boardPanel.setPreferredSize(new Dimension(400, 800));
        for (int y = 0; y < cases.length; y++) {
            for (int x = 0; x < cases[y].length; x++) {
                cases[y][x] = new JPanel();
                cases[y][x].setBackground(getColorCell(x, y));
                cases[y][x].setPreferredSize(new Dimension(30, 30));
                boardPanel.add(cases[y][x]);
            }
        }

// Panneau score
        JPanel scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(200, 100));
        JLabel scoreLabel = new JLabel("Score : 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scorePanel.add(scoreLabel, BorderLayout.CENTER);

// Panneau prochaine pièce
        JPanel nextPiecePanel = new JPanel();
        nextPiecePanel.setPreferredSize(new Dimension(200, 200));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                JPanel nextPieceCell = new JPanel();
                nextPieceCell.setBackground(Color.LIGHT_GRAY);
                nextPieceCell.setPreferredSize(new Dimension(40, 40));
                nextPiecePanel.add(nextPieceCell);
            }
        }
        JLabel nextPieceLabel = new JLabel("Prochaine pièce", SwingConstants.CENTER);
        nextPieceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nextPiecePanel.add(nextPieceLabel);

// Layout principal façon Tetris
        screen.setLayout(new BorderLayout());
        screen.add(boardPanel, BorderLayout.CENTER);
        screen.add(scorePanel, BorderLayout.NORTH);
        screen.add(nextPiecePanel, BorderLayout.EAST);
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
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                cases[j][i].setBackground(getColorCell(i, j));
            }
        }
    }
}
