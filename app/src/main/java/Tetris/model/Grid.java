package Tetris.model;

import java.util.Observable;

import Tetris.model.Piece.Piece;
import Tetris.model.Piece.PieceI;
import Tetris.model.Piece.PieceJ;
import Tetris.model.Piece.PieceL;
import Tetris.model.Piece.PieceO;
import Tetris.model.Piece.PieceS;
import Tetris.model.Piece.PieceT;
import Tetris.model.Piece.PieceZ;

public class Grid extends Observable {
    private final int width;
    private final int height;
    private final String[][] grid;
    private final Piece currentPiece;
    private int pieceX = 4; // position initiale X (centré)
    private int pieceY = 0; // position initiale Y (en haut)

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
        return getNouvellePiece();
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

    public void descendrePiece() {
        int[][] nextCoords = currentPiece.getCoordinates(pieceX, pieceY + 1);
        for (int[] c : nextCoords) {
            int x = c[0];
            int y = c[1] - 1;
            int y_next = c[1];
            if (y_next >= height || (y_next >= 0 && x >= 0 && x < width && !grid[y_next][x].equals(" "))) {
                break;
            }
            setCell(x, y, " ");
            setCell(x, y_next, currentPiece.getColor());
        }
        pieceY++;
        signalVue();
    }
}
