package Tetris.model.Piece;

public class PieceT extends Piece {

    public PieceT(String color) {
        super(color);
        String[][] shape = {
                { " ", " ", color, " " },
                { " ", color, color, color },
                { " ", " ", " ", " " },
                { " ", " ", " ", " " }
        };
        super.setShape(shape);
    }

}
