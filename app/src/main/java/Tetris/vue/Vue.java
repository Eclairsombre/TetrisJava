package Tetris.vue;

import Tetris.controller.Game;
import Tetris.model.Piece.PieceColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class Vue extends JFrame implements Observer {
    private final JPanel[][] cases;
    private final JLabel scoreLabel;
    private final JPanel[][] nextPieceCells;

    private final Game game;

    public Vue(Game g) {
        this.game = g;
        setTitle("Tetris");
        setSize(700, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cases = new JPanel[game.getGrid().getHeight()][game.getGrid().getWidth()];

        // Game board
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

        // Panel score
        JPanel scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(200, 100));
        scoreLabel = new JLabel("Score : " + game.getGrid().getScore(), SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scorePanel.add(scoreLabel, BorderLayout.CENTER);

        // Panel next piece
        JPanel nextPiecePanel = new JPanel();
        nextPiecePanel.setPreferredSize(new Dimension(200, 200));
        nextPiecePanel.setLayout(new BorderLayout());

        JPanel npPanel = new JPanel();
        npPanel.setLayout(new GridLayout(4, 4, 0, 0));
        nextPieceCells = new JPanel[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextPieceCells[j][i] = new JPanel();
                nextPieceCells[j][i].setBackground(Color.LIGHT_GRAY);
                nextPieceCells[j][i].setPreferredSize(new Dimension(40, 40));
                npPanel.add(nextPieceCells[j][i]);
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


        add(screen);
        setVisible(true);
    }

    private Color getColorCell(PieceColor color) {
        return switch (color) {
            case PieceColor.RED -> new Color(0xFF0000);
            case PieceColor.GREEN -> new Color(0x00FF00);
            case PieceColor.BLUE -> new Color(0x0000FF);
            case PieceColor.YELLOW -> new Color(0xFFFF00);
            case PieceColor.PINK -> new Color(0x800080);
            case PieceColor.ORANGE -> new Color(0xFF7F00);
            case PieceColor.CYAN -> new Color(0x00FFFF);
            default -> Color.BLACK;
        };
    }

    public Color getColorCell(int x, int y) {
        return getColorCell(game.getGrid().getCell(x, y));
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
                    case KeyEvent.VK_Q -> game.rotatePieceLeft();
                    case KeyEvent.VK_D -> game.rotatePieceRight();
                    default -> {
                        // Do nothing
                    }
                }
            }
        });
    }

    private void updateBoard() {
        for (int i = 0; i < game.getGrid().getWidth(); i++) {
            for (int j = 0; j < game.getGrid().getHeight(); j++) {
                cases[j][i].setBackground(getColorCell(i, j));
            }
        }
        repaint();
    }

    private void updateNextPiece() {
        PieceColor[][] nextPiece = game.getGrid().getNextPiece();
        Color color = getColorCell(game.getGrid().getNextPieceColor());
        int temp = 0;
        if (nextPiece[3][1] == PieceColor.NONE) {
            temp = 1;
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (temp != 1) {
                    nextPieceCells[j][i].setBackground((nextPiece[j][i] == PieceColor.NONE) ? Color.BLACK : color);
                } else {
                    nextPieceCells[j][i].setBackground((i > 0 && j > 0 && !(nextPiece[j - 1][i - 1] == PieceColor.NONE)) ? color : Color.BLACK);
                }
            }
        }
        repaint();
    }

    private void updateScore() {
        scoreLabel.setText("Score : " + game.getGrid().getScore());
        repaint();
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        try {
            if (arg instanceof Integer) {
                updateScore();
            } else {
                if (SwingUtilities.isEventDispatchThread()) {
                    updateBoard();
                    if (game.getGrid().isNewNextPiece()) {
                        game.getGrid().setNewNextPiece(false);
                        updateNextPiece();
                    }
                } else {
                    SwingUtilities.invokeAndWait(() -> {
                        updateBoard();
                        if (game.getGrid().isNewNextPiece()) {
                            game.getGrid().setNewNextPiece(false);
                            updateNextPiece();
                        }
                    });
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
