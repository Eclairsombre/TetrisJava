package Tetris.Model.Piece.PieceTemplate;

import Tetris.Model.Piece.Piece;
import Tetris.Utils.PieceColor;

/**
 * PieceO class represents the O-shaped Tetris piece.
 */
public class PieceO extends Piece {
    /**
     * Constructor for PieceO.
     *
     * @param color The color of the piece.
     */
    public PieceO(PieceColor color) {
        super(color);
        int[][] shape = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        super.setShape(shape);
    }

    /**
     * Override the Piece getRotatedPosition method because O tetromino haven't any rotation in our game.
     *
     * @param isLeft Indicates whether the rotation is to the left or right.
     * @return The rotated position of the O piece.
     */
    @Override
    public int[][] getRotatedPosition(boolean isLeft) {
        // O piece does not change shape on rotation
        return getShape();
    }
}
