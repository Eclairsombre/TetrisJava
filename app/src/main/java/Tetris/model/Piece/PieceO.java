package Tetris.model.Piece;

public class PieceO extends Piece {

    public PieceO(PieceColor color) {
        super(color);
        int[][] shape = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        super.setShape(shape);
    }

    @Override
    public int[][] getRotatedPosition(boolean isLeft) {
        // O piece does not change shape on rotation
        return getShape();
    }
}
