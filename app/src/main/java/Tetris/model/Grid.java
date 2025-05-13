package Tetris.model;

import java.util.Observable;

public class Grid extends Observable {
    private final int width;
    private final int height;
    private final String[][] grid;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new String[width][height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String[][] getGrid() {
        return grid;
    }

    public void setCell(int x, int y, String value) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[x][y] = value;
            setChanged();
            notifyObservers(grid[x][y]);
        }
    }

    public String getCell(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[x][y];
        }
        return null;
    }

}
