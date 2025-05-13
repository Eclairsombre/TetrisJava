package Tetris.model.Piece;

public class PieceI extends Piece {
    public PieceI(String color) {
        super(color);
        String[][] shape = {
                { " ", color, " ", " " },
                { " ", color, " ", " " },
                { " ", color, " ", " " },
                { " ", color, " ", " " }
        };
        super.setShape(shape);
    }

}
