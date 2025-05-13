package Tetris.model.Piece;

public class PieceL extends Piece {

    public PieceL(String color) {
        super(color);
        String[][] shape = {
                { " ", " ", color, " " },
                { " ", " ", color, " " },
                { " ", " ", color, color },
                { " ", " ", " ", " " }
        };
        super.setShape(shape);
    }

}
