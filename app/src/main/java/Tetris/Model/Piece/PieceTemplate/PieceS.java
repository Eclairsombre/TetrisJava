package Tetris.Model.Piece.PieceTemplate;

import Tetris.Model.Piece.Piece;
import Tetris.Model.Piece.PieceColor;

public class PieceS extends Piece {

    public PieceS(PieceColor color) {
        super(color);
        int[][] shape = {{0, 1}, {1, 1}, {1, 0}, {2, 0}};

        super.setShape(shape);
    }

}
