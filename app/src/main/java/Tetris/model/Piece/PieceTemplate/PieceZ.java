package Tetris.model.Piece.PieceTemplate;

import Tetris.model.Piece.Piece;
import Tetris.model.Piece.PieceColor;

public class PieceZ extends Piece {

    public PieceZ(PieceColor color) {
        super(color);
        int[][] shape = {{0, 0}, {1, 0}, {1, 1}, {2, 1}};
        super.setShape(shape);
    }
}
