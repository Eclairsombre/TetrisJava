package Tetris.Model.Piece;

import Tetris.Utils.PieceColor;

/**
 * Represents a Tetris piece with its shape, color, and position on the grid.
 */
public class Piece {
    private final int[][] shape = new int[4][2];
    /// shape The shape of the piece represented as a 4x2 array.
    private final PieceColor color;
    /// color The color of the piece.
    private final int[] center = new int[2];
    /// center The center of the piece for rotation.
    private int x = 0;
    /// x The x-coordinate of the piece on the grid.
    private int y = 0;
    /// y The y-coordinate of the piece on the grid.

    /**
     * Constructor for the Piece class.
     *
     * @param color The color of the piece.
     */
    public Piece(PieceColor color) {
        this.color = color;
        this.center[0] = 1;
        this.center[1] = 1;
    }

    public int[][] getShape() {
        return shape;
    }

    public void setShape(int[][] shape) {
        if (shape.length == 4 && shape[0].length == 2) {
            for (int i = 0; i < 4; i++) {
                this.shape[i][0] = shape[i][0];
                this.shape[i][1] = shape[i][1];
            }
        } else {
            throw new IllegalArgumentException("Shape must be 4x2.");
        }
    }

    /**
     * Returns the coordinates of the piece on the grid.
     *
     * @param x The x-coordinate of the piece on the grid.
     * @param y The y-coordinate of the piece on the grid.
     * @return An array of coordinates representing the piece's position on the grid.
     */
    public int[][] getCoordinates(int x, int y) {
        int[][] coordinates = new int[4][2];
        for (int i = 0; i < 4; i++) {
            coordinates[i][0] = this.shape[i][0] + x;
            coordinates[i][1] = this.shape[i][1] + y;
        }
        return coordinates;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public PieceColor getColor() {
        return color;
    }

    /**
     * Returns the rotated position of the piece based on the pivot point.
     *
     * @param isLeft True if rotating left, false if rotating right.
     * @return An array of coordinates representing the rotated position of the piece.
     */
    public int[][] getRotatedPosition(boolean isLeft) {
        int[][] rotatedShape = new int[4][2];
        int pivotX = center[0];
        int pivotY = center[1];

        for (int i = 0; i < 4; i++) {
            int x = shape[i][0] - pivotX;
            int y = shape[i][1] - pivotY;
            int rotatedX, rotatedY;

            if (isLeft) {
                rotatedX = y;
                rotatedY = -x;
            } else {
                rotatedX = -y;
                rotatedY = x;
            }

            rotatedShape[i][0] = rotatedX + pivotX;
            rotatedShape[i][1] = rotatedY + pivotY;
        }

        return rotatedShape;
    }

    /**
     * Returns the maximum Y coordinate of the piece.
     *
     * @return The maximum Y coordinate of the piece.
     */
    public int maxYCoord() {
        int maxY = shape[0][1];
        for (int i = 1; i < 4; i++) {
            if (shape[i][1] > maxY) {
                maxY = shape[i][1];
            }
        }
        return maxY + y;
    }
}
