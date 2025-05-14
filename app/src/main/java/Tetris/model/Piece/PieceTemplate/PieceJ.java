package Tetris.model.Piece.PieceTemplate;

import Tetris.model.Piece.Piece;
import Tetris.model.Piece.PieceColor;

public class PieceJ extends Piece {

    public PieceJ(PieceColor color) {
        super(color);
        int[][] shape = {{0, 0}, {1, 0}, {2, 0}, {2, 1}};
        super.setShape(shape);
    }

}
