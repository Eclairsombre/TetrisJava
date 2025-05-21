package Tetris.VueController.Page.TetrisComponent;

import Tetris.Model.Piece.Piece;
import Tetris.Utils.PieceColor;
import Tetris.Utils.ObservableMessage;
import static Tetris.Utils.PieceColor.getColorCell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * Displays the game board.
 */
@SuppressWarnings("deprecation")
public class GameBoardView extends JPanel implements Observer {
    private final CustomJPanel[][] cases;
    /// cases the cases of the game board
    private final int width;
    /// width the width of the game board
    private final int height;
    /// height the height of the game board
    private final JPanel boardPanel;
    /// boardPanel the panel that contains the game board

    /**
     * Constructor for the GameBoardView class.
     *
     * @param width           The width of the game board.
     * @param height          The height of the game board.
     * @param backgroundColor The background color of the game board.
     */
    public GameBoardView(int width, int height, Color backgroundColor) {
        this.cases = new CustomJPanel[height][width];
        this.width = width;
        this.height = height;
        setLayout(new GridBagLayout());
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(height, width, 0, 0));
        for (int y = 0; y < cases.length; y++) {
            for (int x = 0; x < cases[y].length; x++) {
                cases[y][x] = new CustomJPanel(Color.BLACK);
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
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                setSize(getSize().width / 2, getSize().height / 2); // Adjust the size of the boardPanel
            }
        });
    }


    /**
     * Updates the colors of the cells based on the current piece and the grid state.
     */
    public void updateBoard(PieceColor[][] grid, Piece piece, int RDropMaxY) {
        List<Integer> whiteLines = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
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
            if (RDropCoords[i][1] > maxY && RDropCoords[i][1] < height) { // shape of the piece below the piece
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

    @Override
    public void setSize(int width, int height) {
        int ratio = 13;
        int offset = 20;
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
                new_height - offset
        ));
        revalidate();
    }

    /**
     * Reception of the updates from the game, through the observer pattern.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof ObservableMessage OM)) {
            System.err.println("Error: arg is not a ObservableMessage");
            return;
        }
        if (OM.message().equals("grid")) {
            SwingUtilities.invokeLater(() -> updateBoard(
                    OM.grid(),
                    OM.pieceManager().getCurrentPiece(),
                    OM.maxRDropY())
            );
        }
    }
}
