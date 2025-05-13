package Tetris.model.Piece;

public class PieceJ extends Piece {

    public PieceJ(String color) {
        super(color);
        int[][] shape = {{0, 0}, {1, 0}, {2, 0}, {2, 1}};
        super.setShape(shape);
    }

}
