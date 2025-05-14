package Tetris.model.Piece;

public class PieceL extends Piece {

    public PieceL(PieceColor color) {
        super(color);
        int[][] shape = {{0, 1}, {1, 1}, {2, 1}, {2, 0}};
        super.setShape(shape);
    }

}
