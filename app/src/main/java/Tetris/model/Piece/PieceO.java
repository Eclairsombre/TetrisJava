package Tetris.model.Piece;

public class PieceO extends Piece {

    public PieceO(String color) {
        super(color);
        int[][] shape = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        super.setShape(shape);
    }
}
