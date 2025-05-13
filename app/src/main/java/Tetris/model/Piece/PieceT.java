package Tetris.model.Piece;

public class PieceT extends Piece {

    public PieceT(String color) {
        super(color);
        int[][] shape = {{0, 1}, {1, 1}, {2, 1}, {1, 0}};
        super.setShape(shape);
    }

}
