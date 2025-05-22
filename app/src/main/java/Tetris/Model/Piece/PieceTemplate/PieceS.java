package Tetris.Model.Piece.PieceTemplate;

import Tetris.Model.Piece.Piece;
import Tetris.Utils.PieceColor;

/**
 * PieceS class represents the S-shaped Tetris piece.
 */
public class PieceS extends Piece {
    /**
     * Constructor for PieceS.
     *
     * @param color The color of the piece.
     */
    public PieceS(PieceColor color) {
        super(color);
        int[][] shape = {{0, 1}, {1, 1}, {1, 0}, {2, 0}};
        super.setShape(shape);
    }
}
