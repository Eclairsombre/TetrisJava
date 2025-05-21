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
            if (pos[0] == 1 && pos[1] == 2) {
                coinToCheck.remove(up);
            } else if (pos[0] == 0 && pos[1] == 1) {
                coinToCheck.remove(down);
            } else if (pos[0] == 2 && pos[1] == 1) {
                coinToCheck.remove(left);
            } else if (pos[0] == 1 && pos[1] == 0) {
                coinToCheck.remove(right);
            }
        }
        if (coinToCheck.size() == 1) {
            int[] lastPoint = coinToCheck.getFirst();
            if (lastPoint[0] == 0 && lastPoint[1] == 1) {
                return "right";
            } else if (lastPoint[0] == 1 && lastPoint[1] == 2) {
                return "up";
            } else if (lastPoint[0] == 2 && lastPoint[1] == 1) {
                return "left";
            } else if (lastPoint[0] == 1 && lastPoint[1] == 0) {
                return "down";
            }
        }
        return "none";
    }
}
