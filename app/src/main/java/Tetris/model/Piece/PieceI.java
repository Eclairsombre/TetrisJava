package Tetris.model.Piece;

public class PieceI extends Piece {
    public PieceI(String color) {
        super(color);
        int[][] shape = {{0, 1}, {0, 2}, {0, 3}, {0, 4}};
        super.setShape(shape);
    }

}
