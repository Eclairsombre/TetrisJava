package Tetris.Model.Piece.PieceTemplate;

import Tetris.Model.Piece.Piece;
import Tetris.Model.Piece.PieceColor;

public class PieceZ extends Piece {

    public PieceZ(PieceColor color) {
        super(color);
        int[][] shape = {{0, 0}, {1, 0}, {1, 1}, {2, 1}};
        super.setShape(shape);
    }
}
