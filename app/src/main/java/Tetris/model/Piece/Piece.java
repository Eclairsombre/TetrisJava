package Tetris.model.Piece;

public class Piece {
    private final String[][] shape;
    private String color;
    private int x;
    private int y;

    public Piece(String color) {
        this.color = color;
        this.x = 0;
        this.y = 0;
        this.shape = new String[4][4];
    }

    public String[][] getShape() {
        return shape;
    }

    public void setShape(String[][] shape) {
        if (shape.length == 4 && shape[0].length == 4) {
            for (int i = 0; i < 4; i++) {
                System.arraycopy(shape[i], 0, this.shape[i], 0, 4);
            }
        } else {
            throw new IllegalArgumentException("Shape must be 4x4.");
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
