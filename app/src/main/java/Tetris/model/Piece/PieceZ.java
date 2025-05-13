package Tetris.model.Piece;

public class PieceZ extends Piece {

    public PieceZ(String color) {
        super(color);
        String[][] shape = {
                { " ", color, color, " " },
                { " ", " ", color, color },
                { " ", " ", " ", " " },
                { " ", " ", " ", " " }
        };
        super.setShape(shape);
    }

}
