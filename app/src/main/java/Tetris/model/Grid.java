package Tetris.model;

import Tetris.model.Piece.*;

import java.util.Observable;

public class Grid extends Observable {
    private final int width;
    private final int height;
    private final String[][] grid;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new String[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[y][x] = " ";
            }
        }
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
            grid[y][x] = value;
            setChanged();
            notifyObservers(grid[y][x]);
        }
    }

    public String getCell(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[y][x];
        }
        return null;
    }

    public Piece getNouvellePiece() {
        String[] pieceTypes = {"PieceI", "PieceJ", "PieceL", "PieceO", "PieceS", "PieceT", "PieceZ"};
        int idx = (int) (Math.random() * pieceTypes.length);
        try {
            return switch (pieceTypes[idx]) {
                case "PieceI" -> new PieceI("red");
                case "PieceJ" -> new PieceJ("red");
                case "PieceL" -> new PieceL("red");
                case "PieceO" -> new PieceO("red");
                case "PieceS" -> new PieceS("red");
                case "PieceT" -> new PieceT("red");
                case "PieceZ" -> new PieceZ("red");
                default -> throw new IllegalArgumentException("Unknown piece type: " + pieceTypes[idx]);
            };
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
