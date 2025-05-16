package Tetris.vue;

import Tetris.controller.Game;
import Tetris.model.Piece.Piece;
import Tetris.model.Piece.PieceColor;
import Tetris.vue.TetrisViewComponent.CustomJPanel;
import Tetris.vue.TetrisViewComponent.DashBoardView;
import Tetris.vue.TetrisViewComponent.GameBoardView;
import Tetris.vue.TetrisViewComponent.PieceDisplayManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class TetrisView extends JFrame implements Observer {
    private final CustomJPanel[][] cases;
    private final CustomJPanel[][] holdPieceCells;
    private final CustomJPanel[][][] nextPieceCells;
    private final Game game;
    private final DashBoardView dashBoardVue;
    private GameOverPopup gameOverPopup;
    private final MusicPlayer musicPlayer;
    private final int widthGrid;
    private final int heightGrid;

    public TetrisView(Game g, String musicPath) {
        this.game = g;
        setTitle("Tetris");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Color backgroundColor = Color.LIGHT_GRAY;
        widthGrid = game.getLengthGrid()[0];
        heightGrid = game.getLengthGrid()[1];
        cases = new CustomJPanel[heightGrid][widthGrid];
        GameBoardView boardView = new GameBoardView(widthGrid, heightGrid, cases, backgroundColor);

        nextPieceCells = new CustomJPanel[3][4][4];
        holdPieceCells = new CustomJPanel[4][4];
        JPanel templatePanel = new JPanel();
        PieceDisplayManager piecePanel = new PieceDisplayManager(nextPieceCells, holdPieceCells, 20, 20, backgroundColor, game.getFileWriterAndReader().readFromFile());
        templatePanel.add(piecePanel);
        templatePanel.setBackground(backgroundColor);

        JPanel westPanel = new JPanel();
        westPanel.setPreferredSize(new Dimension(50, 20));
        westPanel.setFocusable(false);
        westPanel.setBackground(backgroundColor);
        dashBoardVue = new DashBoardView(backgroundColor);
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

        musicPlayer = new MusicPlayer(musicPath);
        musicPlayer.play();

        SwingUtilities.invokeLater(() -> { // to fix or remove to solve the problem if no clue found
            updateNextPiece();
            updateBoard();
        });
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
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
        return getColorCell(game.getGridCell(x, y));
    }

    public void start() {
        game.addObserver(this); // Abonnement déplacé ici
        setVisible(true);
    }

    public void createEventListener(JPanel panel) {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public synchronized void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    game.pauseGame();
                    return;
                }
                if (game.isPaused()) {
                    return;
                }
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN -> game.movePieceDown(true);
                    case KeyEvent.VK_LEFT -> game.movePieceLeft();
                    case KeyEvent.VK_RIGHT -> game.movePieceRight();
                    case KeyEvent.VK_UP -> game.doRdrop();
                    case KeyEvent.VK_Q -> game.rotatePieceLeft();
                    case KeyEvent.VK_D -> game.rotatePieceRight();
                    case KeyEvent.VK_SPACE -> game.exchangeHoldAndCurrent();
                    default -> {
                        // Do nothing
                    }
                }
            }
        });
    }

    private void updateBoard() {
        for (int i = 0; i < widthGrid; i++) {
            for (int j = 0; j < heightGrid; j++) {
                cases[j][i].setBackground(getColorCell(i, j));
            }
        }
        Piece piece = game.getPieceManager().getCurrentPiece();
        int[][] coords = piece.getCoordinates(piece.getX(), piece.getY());
        Color color = getColorCell(piece.getColor());
        int RDropMove = game.getMaxHeightEmpty(piece);
        int[][] RDropCoords = piece.getCoordinates(piece.getX(), RDropMove);

        for (int i = 0; i < 4; i++) {
            int x = coords[i][0];
            cases[RDropCoords[i][1]][x].setBackground(Color.GRAY);
            cases[coords[i][1]][x].setBackground(color);
        }

        this.repaint();
    }

    private void updateNextPiece() {
        for (int i = 0; i < 3; i++) {
            Piece nextPiece = game.getPieceManager().getNextPiece().get(i);
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
        Piece holdPiece = game.getPieceManager().getHoldPiece();
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

    private void showGameOverPopup() {
        gameOverPopup.setVisible(true);
        gameOverPopup.setFocusable(true);
        gameOverPopup.setFocusableWindowState(true);
        gameOverPopup.requestFocus();
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            if (!(arg instanceof String)) {
                System.err.println("Error: arg is not a String");
                return;
            }
            switch ((String) arg) {
                case "pause" -> this.game.pauseGame();
                case "timer" -> dashBoardVue.updateTimerLabel(this.game.getStatsValues().getTime());
                case "stats" -> dashBoardVue.updateStats(this.game.getStatsValues());
                case "grid" -> SwingUtilities.invokeLater(this::updateBoard);
                case "level" -> this.game.updateLevel();
                case "gameOver" -> {
                    this.game.stopGame();
                    this.gameOverPopup = new GameOverPopup(this, this.game);
                    showGameOverPopup();
                }
                case "nextPiece" -> SwingUtilities.invokeLater(this::updateNextPiece);
                default -> System.err.println("Error: arg is not a valid String");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
