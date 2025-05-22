package Tetris.Model.Piece.PieceTemplate;

import Tetris.Model.Piece.Piece;
import Tetris.Utils.PieceColor;

import java.util.List;

/**
 * PieceT class represents the T-shaped piece in Tetris.
 */
public class PieceT extends Piece {

    /**
     * Constructor for PieceT
     *
     * @param color the color of the piece
     */
    public PieceT(PieceColor color) {
        super(color);
        int[][] shape = {{0, 1}, {1, 1}, {2, 1}, {1, 0}};
        super.setShape(shape);
    }

    /**
     * Get the direction of the piece
     *
     * @return the direction of the piece
     */
    public String getDirection() {
        int[] down = {0, 1};
        int[] up = {1, 2};
        int[] left = {2, 1};
        int[] right = {1, 0};
        List<int[]> coinToCheck = new java.util.ArrayList<>(List.of(down, up, left, right));
        for (int[] pos : getShape()) {
            if (!(coinToCheck.contains(pos))) {
                switch (pos[0]) {
                    case 0:
                        return "up"; // because down isn't fill
                    case 1:
                        // if the second coordinate is 0, the right block isn't fill so we return left.
                        // else, is the left block so we return the right direction
                        return pos[1] == 0 ? "left" : "right";
                    case 2:
                        return "down"; // because up isn't fill
                }
            }
        }
        return "none";
    }
}
