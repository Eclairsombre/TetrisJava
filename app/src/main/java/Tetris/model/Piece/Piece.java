package Tetris.model.Piece;

public class Piece {
    private final int[][] shape = new int[4][2]; // General shape for the piece
    private final String color;
    private int x = 0;
    private int y = 0; // Position of the piece on the grid

    public Piece(String color) {
        this.color = color;
    }

    public int[][] getShape() {
        return shape;
    }

    public int[][] getCoordinates(int x, int y) {
        int[][] coordinates = new int[4][2];
        for (int i = 0; i < 4; i++) {
            coordinates[i][0] = this.shape[i][0] + x;
            coordinates[i][1] = this.shape[i][1] + y;
        }
        return coordinates;
    }

    public void setShape(int[][] shape) {
        if (shape.length == 4 && shape[0].length == 2) {
            for (int i = 0; i < 4; i++) {
                System.arraycopy(shape[i], 0, this.shape[i], 0, 2);
            }
        } else {
            throw new IllegalArgumentException("Shape must be 4x2.");
        }
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

    public String getColor() {
        return color;
    }

    public int[][] getRotatedPosition(boolean isLeft) {
        int[][] rotatedShape = new int[4][2];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (isLeft) {
                    rotatedShape[i][j] = this.shape[3 - j][i];
                } else {
                    rotatedShape[i][j] = this.shape[j][3 - i];
                }
            }
        }
        return rotatedShape;
    }
}
