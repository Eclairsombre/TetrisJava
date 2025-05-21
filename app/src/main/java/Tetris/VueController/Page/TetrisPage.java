package Tetris.VueController.Page;

import Tetris.Model.Grid;
import Tetris.Model.Piece.Piece;
import Tetris.Model.Piece.PieceColor;
import Tetris.Model.Piece.PieceManager;
import Tetris.Model.Piece.PieceTemplate.PieceI;
import Tetris.Model.Utils.ObservableMessage;
import Tetris.Model.Utils.StatsValues;
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

/**
 * TetrisPage class represents the main game page of the Tetris game.
 */
@SuppressWarnings("deprecation")
public class TetrisPage extends JPanel implements Observer {
    private final CustomJPanel[][] cases;
    /// cases the displayed grid of the game
    private final CustomJPanel[][] holdPieceCells;
    /// holdPieceCells the displayed cells of the hold piece
    private final CustomJPanel[][][] nextPieceCells;
    /// nextPieceCells the displayed cells of the next pieces
    private final DashBoardView dashBoardVue;
    /// dashBoardVue the dashboard of the game
    private final PieceDisplayManager piecePanel;
    /// piecePanel the panel that displays the pieces
    private final int widthGrid;
    /// widthGrid the width of the grid
    private final int heightGrid;
    /// heightGrid the height of the grid
    Runnable changeToGameOver;
    /// @param changeToGameOver the function to call when the game is over

    /**
     * Constructor for the TetrisPage class.
     * Initializes a Tetris game view
     *
     * @param g                The game instance.
     * @param changeToGameOver Runnable to change to the game over screen.
     */
    public TetrisPage(Grid g, Runnable changeToGameOver) {
        Color backgroundColor = Color.LIGHT_GRAY;
        this.changeToGameOver = changeToGameOver;

        widthGrid = g.getLengthGrid()[0];
        heightGrid = g.getLengthGrid()[1];
        nextPieceCells = new CustomJPanel[3][4][4];
        holdPieceCells = new CustomJPanel[4][4];
        cases = new CustomJPanel[heightGrid][widthGrid];

        GameBoardView boardView = new GameBoardView(widthGrid, heightGrid, cases, backgroundColor);

        dashBoardVue = new DashBoardView(backgroundColor);
        dashBoardVue.setPreferredSize(new Dimension(260, 200));

        JPanel templatePanel = new JPanel();
        piecePanel = new PieceDisplayManager(nextPieceCells, holdPieceCells, 20, 20, backgroundColor, g.getFileWriterAndReader());
        templatePanel.add(piecePanel);
        templatePanel.setBackground(backgroundColor);

        setLayout(new BorderLayout());
        add(boardView, BorderLayout.CENTER);
        add(dashBoardVue, BorderLayout.WEST);
        add(templatePanel, BorderLayout.EAST);
        setVisible(true);

        SwingUtilities.invokeLater(() -> { // to fix or remove to solve the problem if no clue found
            updateNextPiece(g.getPieceManager());
            updateBoard(g.getGrid(), g.getPieceManager().getCurrentPiece(), g.findMaxY(g.getPieceManager().getCurrentPiece()));
        });

        g.addObserver(this);
        setVisible(true);
    }

    /**
     * Method to update the AI label on the dashboard bouton.
     */
    public void updateAILabel(Grid grid) {
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

    /**
     * Updates the colors of the cells based on the current piece and the grid state.
     */
    public void updateBoard(PieceColor[][] grid, Piece piece, int RDropMaxY) {
        List<Integer> whiteLines = new ArrayList<>();
        for (int i = 0; i < widthGrid; i++) {
            for (int j = 0; j < heightGrid; j++) {
                cases[j][i].setBackground(getColorCell(grid[j][i]));
                if (cases[j][i].getBackground() == Color.WHITE && !whiteLines.contains(j)) {
                    whiteLines.add(j);
                }
            }
        }
        int[][] coords = piece.getCoordinates(piece.getX(), piece.getY());
        int maxY = piece.maxYCoord();
        Color color = getColorCell(piece.getColor());
        int[][] RDropCoords = piece.getCoordinates(piece.getX(), RDropMaxY);

        for (int i = 0; i < 4; i++) {
            int x = coords[i][0];
            if (RDropCoords[i][1] > maxY && RDropCoords[i][1] < heightGrid) { // shape of the piece below the piece
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
    public void updateNextPiece(PieceManager pieceManager) {
        for (int i = 0; i < 3; i++) {
            Piece nextPiece = pieceManager.getNextPiece().get(i);
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
        Piece holdPiece = pieceManager.getHoldPiece();
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
            if (!(arg instanceof ObservableMessage(
                    String message, PieceColor[][] grid, StatsValues statsValues,
                    PieceManager pieceManager, int maxRDropY
            ))) {
                System.err.println("Error: arg is not a ObservableMessage");
                return;
            }
            switch (message) {
                case "timer" -> dashBoardVue.updateTimerLabel(statsValues.getTime());
                case "stats" -> dashBoardVue.updateStats(statsValues);
                case "grid" ->
                        SwingUtilities.invokeLater(() -> updateBoard(grid, pieceManager.getCurrentPiece(), maxRDropY));
                case "gameOver" -> {
                    piecePanel.updateBestScores();
                    dashBoardVue.updateAILabel("AI Mode : OFF");
                    changeToGameOver.run();
                }
                case "nextPiece" -> SwingUtilities.invokeLater(() -> updateNextPiece(pieceManager));
                default -> System.err.println("Error: arg is not a valid String");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
