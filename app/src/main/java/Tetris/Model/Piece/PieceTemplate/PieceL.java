package Tetris.Model.Piece.PieceTemplate;

import Tetris.Model.Piece.Piece;
import Tetris.Model.Piece.PieceColor;

/**
 * PieceL class represents the L-shaped Tetris piece.
 */
public class PieceL extends Piece {

    /**
     * Constructor for PieceL.
     *
     * @param color The color of the piece.
     */
    public PieceL(PieceColor color) {
        super(color);
        int[][] shape = {{0, 1}, {1, 1}, {2, 1}, {2, 0}};
        super.setShape(shape);
    }

}
