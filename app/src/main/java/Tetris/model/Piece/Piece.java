package Tetris.model.Piece;


public class Piece {
    private final int[][] shape = new int[4][2]; // General shape for the piece
    private final PieceColor color;
    private int x = 0;
    private int y = 0; // Position of the piece on the grid
    private final int[] center = new int[2]; // Center of the piece for rotation

    public Piece(PieceColor color) {
        this.color = color;
        this.center[0] = 1;
        this.center[1] = 1;
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
                this.shape[i][0] = shape[i][0];
                this.shape[i][1] = shape[i][1];
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

    public PieceColor getColor() {
        return color;
    }

    public int[][] getRotatedPosition(boolean isLeft) {
        int[][] rotatedShape = new int[4][2];
        int pivotX = center[0];
        int pivotY = center[1];

        for (int i = 0; i < 4; i++) {
            int x = shape[i][0] - pivotX;
            int y = shape[i][1] - pivotY;
            int rotatedX, rotatedY;

            if (isLeft) {
                rotatedX = -y;
                rotatedY = x;
            } else {
                rotatedX = y;
                rotatedY = -x;
            }

            rotatedShape[i][0] = rotatedX + pivotX;
            rotatedShape[i][1] = rotatedY + pivotY;
        }

        return rotatedShape;
    }
}
