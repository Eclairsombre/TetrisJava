package Tetris.VueController.Page;

import Tetris.Model.Grid;
import Tetris.Model.Piece.Piece;
import Tetris.Model.Piece.PieceColor;
import Tetris.Model.Piece.PieceTemplate.PieceI;
import Tetris.VueController.Page.TetrisComponent.CustomJPanel;
import Tetris.VueController.Page.TetrisComponent.DashBoardView;
import Tetris.VueController.Page.TetrisComponent.GameBoardView;
import Tetris.VueController.Page.TetrisComponent.PieceDisplayManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class TetrisPage extends JPanel implements Observer {
    private final CustomJPanel[][] cases;
    private final CustomJPanel[][] holdPieceCells;
    private final CustomJPanel[][][] nextPieceCells;
    private final Grid grid;
    private final DashBoardView dashBoardVue;
    private final PieceDisplayManager piecePanel;
    private final int widthGrid;
    private final int heightGrid;
    Runnable changeToGameOver;

    /**
     * Constructor for the TetrisPage class.
     * Initializes a Tetris game view
     *
     * @param g                 The game instance.
     * @param changeToGameOver  Runnable to change to the game over screen.
     */
    public TetrisPage(Tetris.Model.Grid g, Runnable changeToGameOver) {
        Color backgroundColor = Color.LIGHT_GRAY;
        this.changeToGameOver = changeToGameOver;
        grid = g;

        widthGrid = grid.getLengthGrid()[0];
        heightGrid = grid.getLengthGrid()[1];
        nextPieceCells = new CustomJPanel[3][4][4];
        holdPieceCells = new CustomJPanel[4][4];
        cases = new CustomJPanel[heightGrid][widthGrid];

        GameBoardView boardView = new GameBoardView(widthGrid, heightGrid, cases, backgroundColor);

        dashBoardVue = new DashBoardView(backgroundColor);
        dashBoardVue.setPreferredSize(new Dimension(260, 200));

        JPanel templatePanel = new JPanel();
        piecePanel = new PieceDisplayManager(nextPieceCells, holdPieceCells, 20, 20, backgroundColor, grid.getFileWriterAndReader());
        templatePanel.add(piecePanel);
        templatePanel.setBackground(backgroundColor);

        setLayout(new BorderLayout());
        add(boardView, BorderLayout.CENTER);
        add(dashBoardVue, BorderLayout.WEST);
        add(templatePanel, BorderLayout.EAST);
        setVisible(true);

        SwingUtilities.invokeLater(() -> { // to fix or remove to solve the problem if no clue found
            updateNextPiece();
            updateBoard();
        });

        grid.addObserver(this);
        setVisible(true);
    }

    /**
     * Method to update the AI label on the dashboard bouton.
     */
    public void updateAILabel() {
        if (grid.isAiMode()) {
            dashBoardVue.updateAILabel("AI Mode : ON");
        } else {
            dashBoardVue.updateAILabel("AI Mode : OFF");
        }
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
            case PieceColor.WHITE -> Color.WHITE;
            default -> Color.BLACK;
        };
    }

    public Color getColorCell(int x, int y) {
        return getColorCell(grid.getCell(x, y));
    }

    /**
     * Updates the colors of the cells based on the current piece and the grid state.
     */
    public void updateBoard() {
        List<Integer> whiteLines = new ArrayList<>();
        for (int i = 0; i < widthGrid; i++) {
            for (int j = 0; j < heightGrid; j++) {
                cases[j][i].setBackground(getColorCell(i, j));
                if (cases[j][i].getBackground() == Color.WHITE && !whiteLines.contains(j)) {
                    whiteLines.add(j);
                }
            }
        }
        Piece piece = grid.getPieceManager().getCurrentPiece();
        int[][] coords = piece.getCoordinates(piece.getX(), piece.getY());
        int maxY = piece.maxYCoord();
        Color color = getColorCell(piece.getColor());
        int RDropMove = grid.findMaxY(piece);
        int[][] RDropCoords = piece.getCoordinates(piece.getX(), RDropMove);

        for (int i = 0; i < 4; i++) {
            int x = coords[i][0];
            if (RDropCoords[i][1] > maxY) { // shape of the piece below the piece
                cases[RDropCoords[i][1]][x].setBackground(Color.GRAY);
            }
            if (whiteLines.contains(coords[i][1])) { // if the line is full
                cases[coords[i][1]][x].setBackground(Color.WHITE); // we set the piece color like the line
            } else {
                cases[coords[i][1]][x].setBackground(color);
            }
        }

        repaint();
    }

    /**
     * Updates the next piece and hold piece displayed in PieceDisplayManager.
     */
    public void updateNextPiece() {
        for (int i = 0; i < 3; i++) {
            Piece nextPiece = grid.getPieceManager().getNextPiece().get(i);
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
        Piece holdPiece = grid.getPieceManager().getHoldPiece();
        if (holdPiece != null) {
            int[][] coords = holdPiece.getShape();
            Color color = getColorCell(holdPiece.getColor());
            if (!(coords[3][0] == 1 && coords[3][1] == 3)) {
                coords = holdPiece.getCoordinates(1, 1);
            }
            int offset = 0;
            if (holdPiece instanceof PieceI && holdPiece.getShape()[0][0] == 0) { // we are horizontally oriented
                offset = 1;
            }
            for (int[] coord : coords) {
                int x = coord[0] - offset;
                int y = coord[1];
                holdPieceCells[x][y].setBackground(color);
            }
        }

        repaint();
    }

    /**
     * General reception of the updates from the game, through the observer pattern.
     */
    @Override
    public void update(Observable o, Object arg) {
        try {
            if (!(arg instanceof String)) {
                System.err.println("Error: arg is not a String");
                return;
            }
            switch ((String) arg) {
                case "timer" -> dashBoardVue.updateTimerLabel(grid.getStatsValues().getTime());
                case "stats" -> dashBoardVue.updateStats(grid.getStatsValues());
                case "grid" -> SwingUtilities.invokeLater(this::updateBoard);
                case "gameOver" -> {
                    piecePanel.updateBestScores();
                    grid.setAiMode(false);
                    dashBoardVue.updateAILabel("AI Mode : OFF");
                    changeToGameOver.run();
                }
                case "nextPiece" -> SwingUtilities.invokeLater(this::updateNextPiece);
                case "level" -> grid.updateLevel();
                case "fixPiece" -> grid.fixPiece();
                default -> System.err.println("Error: arg is not a valid String");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
