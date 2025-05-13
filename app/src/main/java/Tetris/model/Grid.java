package Tetris.model;

import Tetris.model.Piece.*;

import java.util.Observable;

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
        p.setX(3);
        p.setY(-2); // TODO ?????
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
        String[] pieceTypes = {"PieceI", "PieceJ", "PieceL", "PieceO", "PieceS", "PieceT", "PieceZ"};
        // TODO : use a better random generator
        java.security.SecureRandom random = new java.security.SecureRandom();
        int idx = random.nextInt(pieceTypes.length);
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
            System.err.println("Error creating piece: " + e.getMessage());
            return null;
        }
    }

    private boolean inCurrentPiece(int x, int y) {
        int[][] coords = currentPiece.getCoordinates(currentPiece.getX(), currentPiece.getY());
        for (int[] c : coords) {
            if (c[0] == x && c[1] == y) {
                return true;
            }
        }
        return false;
    }

    public void movePiece(int x_move, int y_move, boolean fixPiece) {
        int[][] nextCoords = currentPiece.getCoordinates(currentPiece.getX() + x_move, currentPiece.getY() + y_move);
        boolean canMoveDown = true;
        for (int[] c : nextCoords) {
            int x_next = c[0];
            int y_next = c[1];
            if (inCurrentPiece(x_next, y_next)) {
                continue;
            }
            if (y_next >= height || (y_next >= 0 && x_next >= 0 && x_next < width && !grid[y_next][x_next].equals(" "))) {
                if (!fixPiece) {
                    return;
                }
                canMoveDown = false;
                break;
            }
        }
        if (canMoveDown) {
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
            this.currentPiece = initializePiece();
        }

        currentPiece.setX(currentPiece.getX() + x_move);
        currentPiece.setY(currentPiece.getY() + y_move);
        signalVue();
    }
}
