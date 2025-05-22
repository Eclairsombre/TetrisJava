package Tetris.Model.Piece.PieceTemplate;

import Tetris.Model.Piece.Piece;
import Tetris.Utils.PieceColor;

/**
 * PieceJ class represents the J-shaped Tetris piece.
 */
public class PieceJ extends Piece {
    /**
     * Constructor for PieceJ.
     *
     * @param color The color of the piece.
     */
    public PieceJ(PieceColor color) {
        super(color);
        int[][] shape = {{0, 0}, {1, 0}, {2, 0}, {2, 1}};
        super.setShape(shape);
    }
}
