package Tetris.model.Piece;

public class PieceJ extends Piece {

    public PieceJ(String color) {
        super(color);
        String[][] shape = {
                { " ", " ", color, " " },
                { " ", " ", color, " " },
                { " ", color, color, " " },
                { " ", " ", " ", " " }
        };
        super.setShape(shape);
    }

}
