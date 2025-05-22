package Tetris.Model.Piece.PieceTemplate;

import Tetris.Model.Piece.Piece;
import Tetris.Utils.PieceColor;

/**
 * PieceZ class represents the Z-shaped Tetris piece.
 */
public class PieceZ extends Piece {
    /**
     * Constructor for PieceZ.
     *
     * @param color The color of the piece.
     */
    public PieceZ(PieceColor color) {
        super(color);
        int[][] shape = {{0, 0}, {1, 0}, {1, 1}, {2, 1}};
        super.setShape(shape);
    }
}
