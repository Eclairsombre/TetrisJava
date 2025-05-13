package Tetris.model.Piece;

public class PieceS extends Piece {

    public PieceS(String color) {
        super(color);
        String[][] shape = {
                {" ", " ", color, color},
                {" ", color, color, " "},
                {" ", " ", " ", " "},
                {" ", " ", " ", " "}
        };
        super.setShape(shape);
    }

}
