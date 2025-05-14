package Tetris.vue;

import Tetris.controller.Game;
import Tetris.model.Piece.Piece;
import Tetris.model.Piece.PieceColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class TetrisView extends JFrame implements Observer {
    private final JPanel[][] cases;
    private final JPanel[][] holdPieceCells;
    private final JPanel[][][] nextPieceCells;
    private final Game game;
    private final DashBoardView dashBoardVue = new DashBoardView();

    public TetrisView(Game g) {
        this.game = g;
        setTitle("Tetris");
        setSize(900, 1050);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cases = new JPanel[game.getGrid().getHeight()][game.getGrid().getWidth()];
        GameBoardView boardView = new GameBoardView(game.getGrid().getWidth(), game.getGrid().getHeight(), cases);
        nextPieceCells = new JPanel[3][4][4];
        holdPieceCells = new JPanel[4][4];
        JPanel templatePanel = new JPanel();
        PieceDisplayManager piecePanel = new PieceDisplayManager(nextPieceCells, holdPieceCells, 20, 20);
        JPanel eastPanel = new JPanel();
        eastPanel.setPreferredSize(new Dimension(50, 20));
        templatePanel.add(piecePanel);
        templatePanel.add(eastPanel);

        JPanel westPanel = new JPanel();
        westPanel.setPreferredSize(new Dimension(50, 20));
        westPanel.setFocusable(false);

        JPanel screen = new JPanel();
        screen.setFocusable(true);
        createEventListener(screen);
        screen.setLayout(new BorderLayout());
        screen.add(westPanel, BorderLayout.WEST);
        screen.add(boardView, BorderLayout.CENTER);
        screen.add(dashBoardVue, BorderLayout.NORTH);
        screen.add(templatePanel, BorderLayout.EAST);

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
                    case KeyEvent.VK_DOWN -> game.movePieceDown(true);
                    case KeyEvent.VK_LEFT -> game.movePieceLeft();
                    case KeyEvent.VK_RIGHT -> game.movePieceRight();
                    case KeyEvent.VK_Q -> game.rotatePieceLeft();
                    case KeyEvent.VK_D -> game.rotatePieceRight();
                    case KeyEvent.VK_SPACE -> {
                        if (game.getGrid().isGameOver()) {
                            game.reset();
                        }
                    }
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
        Piece piece = game.getGrid().getCurrentPiece();
        int[][] coords = piece.getCoordinates(piece.getX(), piece.getY());
        Color color = getColorCell(piece.getColor());
        for (int[] coord : coords) {
            int x = coord[0];
            int y = coord[1];
            if (x >= 0 && x < game.getGrid().getWidth() && y >= 0 && y < game.getGrid().getHeight()) {
                cases[y][x].setBackground(color);
            }
        }
        repaint();
    }

    private void updateNextPiece() {
        for (int i = 0; i < 3; i++) {
            Piece nextPiece = game.getGrid().getNextPiece().get(i);
            int[][] coords = nextPiece.getShape();
            Color color = getColorCell(nextPiece.getColor());
            if (!(coords[3][0] == 1 && coords[3][1] == 3)) {
                coords = nextPiece.getCoordinates(1, 1);
            }
            for (JPanel[] nextPieceCell : nextPieceCells[i]) {
                for (JPanel jPanel : nextPieceCell) {
                    jPanel.setBackground(Color.BLACK);
                }
            }
            for (int[] coord : coords) {
                int x = coord[0];
                int y = coord[1];
                nextPieceCells[i][x][y].setBackground(color);
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                holdPieceCells[i][j].setBackground(Color.BLACK);
            }
        }
        Piece holdPiece = game.getGrid().getHoldPiece();
        if (holdPiece != null) {
            int[][] coords = holdPiece.getShape();
            Color color = getColorCell(holdPiece.getColor());
            if (!(coords[3][0] == 1 && coords[3][1] == 3)) {
                coords = holdPiece.getCoordinates(1, 1);
            }
            for (int[] coord : coords) {
                int x = coord[0];
                int y = coord[1];
                holdPieceCells[x][y].setBackground(color);
            }
        }

        repaint();
    }

    private void updateScore() {
        dashBoardVue.updateScore(game.getGrid().getScore());
        repaint();
    }

    private void updateLevel() {
        dashBoardVue.updateLevel(game.getGrid().getLevel().getLevel());
        repaint();
    }

    private void updateTimer() {
        dashBoardVue.updateTimer(game.getGrid().getSeconds());
        repaint();
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            if (arg instanceof Integer) {
                updateScore();
                updateLevel();
                updateTimer();
            } else {
                SwingUtilities.invokeLater(() -> {
                    updateBoard();
                    updateLevel();
                    updateTimer();
                    if (game.getGrid().isNewNextPiece()) {
                        game.getGrid().setNewNextPiece(false);
                        updateNextPiece();
                    }
                    repaint();
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
