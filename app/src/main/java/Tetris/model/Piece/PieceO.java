package Tetris.model.Piece;

public class PieceO extends Piece {

    public PieceO(String color) {
        super(color);
        String[][] shape = {
                { " ", " ", color, color },
                { " ", " ", color, color },
                { " ", " ", " ", " " },
                { " ", " ", " ", " " }
        };
        super.setShape(shape);
    }
}
