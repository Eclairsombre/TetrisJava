package Tetris.vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import Tetris.controller.Game;

@SuppressWarnings("deprecation")
public class Vue extends JFrame implements Observer {
    private final JPanel[][] cases;
    private final Game game;

    public Vue(Game g) {
        this.game = g;
        setTitle("Tetris");
        setSize(700, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cases = new JPanel[game.getGrid().getHeight()][game.getGrid().getWidth()];

        // Plateau de jeu (grille Tetris)
        JPanel board = new JPanel();
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(game.getGrid().getHeight(), game.getGrid().getWidth(), 0, 0));
        boardPanel.setPreferredSize(new Dimension(400, 800));
        for (int y = 0; y < cases.length; y++) {
            for (int x = 0; x < cases[y].length; x++) {
                cases[y][x] = new JPanel();
                cases[y][x].setBackground(getColorCell(x, y));
                cases[y][x].setPreferredSize(new Dimension(20, 20));
                boardPanel.setLayout(new GridLayout(20, 10, 0, 0));
                boardPanel.add(cases[y][x]);
            }
        }
        board.add(boardPanel);

        // Panneau score
        JPanel scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(200, 100));
        JLabel scoreLabel = new JLabel("Score : 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scorePanel.add(scoreLabel, BorderLayout.CENTER);

        // Panneau prochaine pièce
        JPanel nextPiecePanel = new JPanel();
        nextPiecePanel.setPreferredSize(new Dimension(200, 200));
        nextPiecePanel.setLayout(new BorderLayout());

        JPanel npPanel = new JPanel();
        npPanel.setLayout(new GridLayout(4, 4, 0, 0));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                JPanel nextPieceCell = new JPanel();
                nextPieceCell.setBackground(Color.LIGHT_GRAY);
                nextPieceCell.setPreferredSize(new Dimension(40, 40));
                npPanel.add(nextPieceCell);
            }
        }
        JLabel nextPieceLabel = new JLabel("Prochaine pièce", SwingConstants.CENTER);
        nextPieceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nextPiecePanel.add(nextPieceLabel, BorderLayout.NORTH);

        JPanel templatePanel = new JPanel();
        templatePanel.add(npPanel);
        nextPiecePanel.add(templatePanel);

        JPanel westPanel = new JPanel();
        westPanel.setPreferredSize(new Dimension(20, 20));

        westPanel.setFocusable(false);
        board.setFocusable(false);
        scorePanel.setFocusable(false);
        nextPiecePanel.setFocusable(false);
        JPanel screen = new JPanel();
        screen.setFocusable(true);
        createEventListener(screen);

        screen.setLayout(new BorderLayout());
        screen.add(westPanel, BorderLayout.WEST);
        screen.add(board, BorderLayout.CENTER);
        screen.add(scorePanel, BorderLayout.NORTH);
        screen.add(nextPiecePanel, BorderLayout.EAST);
        setResizable(false); // stop redefinition of screen;


        add(screen);
        setVisible(true);
    }

    public Color getColorCell(int x, int y) {
        return switch (game.getGrid().getCell(x, y)) {
            case "red" -> Color.RED;
            case "green" -> Color.GREEN;
            case "blue" -> Color.BLUE;
            case "yellow" -> Color.YELLOW;
            case "pink" -> Color.PINK;
            default -> Color.BLACK;
        };
    }

    public void start() {
        game.addObserver(this); // Abonnement déplacé ici
        setVisible(true);
    }


    public void createEventListener(JPanel panel) {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public synchronized void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN -> game.movePieceDown();
                    case KeyEvent.VK_LEFT -> game.movePieceLeft();
                    case KeyEvent.VK_RIGHT -> game.movePieceRight();
                    default -> {
                        // Do nothing
                    }
                }
            }
        });
    }

    private void updateCases() {
        for (int i = 0; i < game.getGrid().getWidth(); i++) {
            for (int j = 0; j < game.getGrid().getHeight(); j++) {
                cases[j][i].setBackground(getColorCell(i, j));
            }
        }
        repaint();
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                updateCases();
            } else {
                SwingUtilities.invokeAndWait(this::updateCases);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
