package Tetris.model;

import Tetris.model.Piece.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

@SuppressWarnings("deprecation")
public class Grid extends Observable {
    private final int width;
    private final int height;
    private final String[][] grid;
    private Piece currentPiece;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new String[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[y][x] = " ";
            }
        }
        this.currentPiece = initializePiece();
    }

    private Piece initializePiece() {
        Piece p = getNouvellePiece();
        p.setPos(3, 0);
        return p;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setCell(int x, int y, String value) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y][x] = value;
        }
    }

    public void signalVue() {
        setChanged();
        notifyObservers(grid);
    }

    public String getCell(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[y][x];
        }
        return null;
    }

    public Piece getNouvellePiece() {
        // make a semi-random choice of a piece to avoid too many duplicates
        java.security.SecureRandom random = new java.security.SecureRandom();
        int idx = random.nextInt(7);
        try {
            return switch (idx) {
                case 0 -> new PieceI("red");
                case 1 -> new PieceJ("orange");
                case 2 -> new PieceL("blue");
                case 3 -> new PieceO("yellow");
                case 4 -> new PieceS("green");
                case 5 -> new PieceT("cyan");
                case 6 -> new PieceZ("pink");
                default -> throw new IllegalArgumentException("Invalid piece index: " + idx);
            };
        } catch (Exception e) {
            System.err.println("Error creating piece: " + e.getMessage());
            return null;
        }
    }

    private boolean isInCurrentPiece(int x, int y) {
        int[][] coords = currentPiece.getCoordinates(currentPiece.getX(), currentPiece.getY());
        for (int[] c : coords) {
            if (c[0] == x && c[1] == y) {
                return true;
            }
        }
        return false;
    }

    public boolean checkCollision(int[][] rotatedShape) {
        List<int[]> pointsToCheck = new ArrayList<>();
        for (int[] pos : rotatedShape) {
            if (!isInCurrentPiece(pos[0], pos[1])) {
                pointsToCheck.add(pos);
            }
        }

        for (int[] c : pointsToCheck) {
            int x_next = c[0];
            int y_next = c[1];
            if (x_next < 0 || x_next >= width || y_next >= height || (y_next >= 0 && !grid[y_next][x_next].equals(" "))) {
                return false;
            }
        }

        return true;
    }

    public void movePiece(int x_move, int y_move, boolean fixPiece) {
        int[][] nextCoords = currentPiece.getCoordinates(currentPiece.getX() + x_move, currentPiece.getY() + y_move);

        if (checkCollision(nextCoords)) {
            for (int[] c : nextCoords) {
                int x = c[0] - x_move;
                int y = c[1] - y_move;
                setCell(x, y, " ");
            }
            for (int[] c : nextCoords) {
                int x_next = c[0];
                int y_next = c[1];
                setCell(x_next, y_next, currentPiece.getColor());
            }
        } else {
            if (!fixPiece) {
                return;
            }
            this.currentPiece = initializePiece();
        }

        currentPiece.setPos(currentPiece.getX() + x_move, currentPiece.getY() + y_move);
        signalVue();
    }

    public void rotatePiece(boolean isLeft) {
        int[][] originalRotatedShape = currentPiece.getRotatedPosition(isLeft);
        int[][] rotatedShape = new int[originalRotatedShape.length][originalRotatedShape[0].length];
        for (int i = 0; i < originalRotatedShape.length; i++) {
            System.arraycopy(originalRotatedShape[i], 0, rotatedShape[i], 0, originalRotatedShape[i].length);
        }
        
        for (int[] c : rotatedShape) {
            c[0] += currentPiece.getX();
            c[1] += currentPiece.getY();
        }

        if (checkCollision(rotatedShape)) {
            for (int[] c : currentPiece.getCoordinates(currentPiece.getX(), currentPiece.getY())) {
                setCell(c[0], c[1], " ");
            }
            currentPiece.setShape(originalRotatedShape);
            for (int[] c : currentPiece.getCoordinates(currentPiece.getX(), currentPiece.getY())) {
                setCell(c[0], c[1], currentPiece.getColor());
            }
            signalVue();
        }
    }
}
