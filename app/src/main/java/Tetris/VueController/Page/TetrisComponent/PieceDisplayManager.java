package Tetris.VueController.Page.TetrisComponent;

import Tetris.Model.Piece.Piece;
import Tetris.Model.Piece.PieceManager;
import Tetris.Model.Piece.PieceTemplate.PieceI;
import Tetris.Model.TetrisInstanceComponent.FileWriterAndReader;
import Tetris.Utils.ObservableMessage;
import static Tetris.Utils.PieceColor.getColorCell;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;


/**
 * PieceDisplayManager is a custom JPanel that displays the next pieces and the hold piece.
 */
@SuppressWarnings("deprecation")
public class PieceDisplayManager extends JPanel implements Observer {
    private final JPanel scorePanel = new JPanel();
    /// scorePanel JPanel to display the best scores
    private final CustomJPanel[][] holdPieceCells;
    /// holdPieceCells the displayed cells of the hold piece
    private final CustomJPanel[][][] nextPiecesTabs;
    /// nextPieceCells the displayed cells of the next pieces

    /**
     * Constructor for PieceDisplayManager.
     * @param width          the width of the cells
     * @param height         the height of the cells
     * @param background     the background color of the panel
     */
    public PieceDisplayManager(int width, int height, Color background) {
        nextPiecesTabs = new CustomJPanel[3][4][4];
        holdPieceCells = new CustomJPanel[4][4];

        LayoutManager layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        JLabel holdPieceLabel = new JLabel("HOLD", SwingConstants.CENTER);
        holdPieceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        holdPieceLabel.setBackground(background);
        add(holdPieceLabel, BorderLayout.CENTER);

        PieceDisplayView holdPiecePanel = new PieceDisplayView(holdPieceCells, width, height, 1.5f);
        holdPiecePanel.setBackground(background);
        add(holdPiecePanel, BorderLayout.SOUTH);

        JPanel NPPanel = new JPanel();
        NPPanel.setLayout(new BoxLayout(NPPanel, BoxLayout.Y_AXIS));
        JLabel nextPieceLabel = new JLabel("NEXT", SwingConstants.CENTER);
        nextPieceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        NPPanel.add(nextPieceLabel, BorderLayout.CENTER);

        PieceDisplayView[] nextPiecePanel = new PieceDisplayView[3];
        for (int i = 0; i < 3; i++) {
            nextPiecePanel[i] = new PieceDisplayView(nextPiecesTabs[i], width, height, 1);
            nextPiecePanel[i].setBackground(background);
            NPPanel.add(nextPiecePanel[i]);
        }
        NPPanel.setBackground(background);

        add(NPPanel);
        scorePanel.setLayout(new GridLayout(5, 1));
        scorePanel.setBorder(BorderFactory.createTitledBorder("Meilleurs Scores"));
        scorePanel.setBackground(Color.LIGHT_GRAY);
        scorePanel.setBounds(600, 750, 230, 100);
        add(scorePanel);

        setBackground(background);
    }

    /**
     * Updates the best scores displayed in the score panel.
     */
    public void updateBestScores(FileWriterAndReader fileWriterAndReader) {
        scorePanel.removeAll();
        String[] scores = fileWriterAndReader.readFromFile();
        for (String score : scores) {
            JLabel scoreLabel = new JLabel(score);
            scorePanel.add(scoreLabel);
        }
        repaint();
        revalidate();
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
            for (JPanel[] nextPieceCell : nextPiecesTabs[i]) {
                for (JPanel jPanel : nextPieceCell) {
                    jPanel.setBackground(Color.BLACK);
                }
            }
            for (int[] coord : coords) {
                int x = coord[0];
                int y = coord[1];
                nextPiecesTabs[i][x][y].setBackground(color);
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
     * Reception of the updates from the game, through the observer pattern.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof ObservableMessage OM)) {
            System.err.println("Error: arg is not a ObservableMessage");
            return;
        }
        if (OM.message().equals("nextPiece")) {
            updateNextPiece(OM.pieceManager());
        } else if (OM.message().equals("gameOver") || OM.message().equals("initBestScores")) {
            updateBestScores(OM.statsValues().fileWriterAndReader);
        }
    }
}
