package Tetris.Model.Piece.PieceTemplate;

import Tetris.Model.Piece.Piece;
import Tetris.Model.Piece.PieceColor;

/**
 * PieceI class represents the I-shaped Tetris piece.
 */
public class PieceI extends Piece {
    /**
     * Constructor for PieceI.
     *
     * @param color The color of the piece.
     */
    public PieceI(PieceColor color) {
        super(color);
        int[][] shape = {{1, 0}, {1, 1}, {1, 2}, {1, 3}};
        super.setShape(shape);
    }

    /**
     * Returns the rotated position of the piece.
     *
     * @param isLeft True if rotating left, false if rotating right.
     * @return The rotated position of the piece.
     */
    @Override
    public int[][] getRotatedPosition(boolean isLeft) {
        int[][] rotatedShape;
        if (getShape()[0][0] == 1) {
            // Rotate to vertical
            rotatedShape = new int[][]{{0, 1}, {1, 1}, {2, 1}, {3, 1}};
        } else {
            // Rotate to horizontal
            rotatedShape = new int[][]{{1, 0}, {1, 1}, {1, 2}, {1, 3}};
        }
        return rotatedShape;
    }
}
