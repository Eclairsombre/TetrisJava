package Tetris.model;

import Tetris.model.Piece.Piece;
import Tetris.model.Piece.PieceColor;
import Tetris.model.Piece.PieceManager;
import Tetris.model.Piece.PieceTemplate.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

@SuppressWarnings("deprecation")
public class Grid extends Observable {
    private final int width;
    private final int height;
    private int score;
    private int lineDeleteCount;
    private final PieceColor[][] grid;
    private final PieceManager pieceManager;
    private Level level;
    private int seconds;
    private boolean isNewNextPiece;
    public boolean isGameOver;
    private boolean canHoldPiece;
    java.util.Random random;

    public Grid(int width, int height) {
        random = new java.util.Random((int) (System.currentTimeMillis() % Integer.MAX_VALUE));
        this.width = width;
        this.height = height;
        this.score = 0;
        this.lineDeleteCount = 0;
        this.seconds = 0;
        this.canHoldPiece = false;
        this.grid = new PieceColor[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[y][x] = PieceColor.NONE;
            }
        }
        this.pieceManager = new PieceManager();
        this.pieceManager.setCurrentPiece(initializePiece());
        List<Piece> nextPiece = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            nextPiece.add(initializePiece());
        }
        this.pieceManager.setNextPiece(nextPiece);
        this.level = new Level(0);
    }

    public int getScore() {
        return score;
    }

    public String getTime() {
            return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
        }

    public void updateScore(int points) {
        score += points;
        setChanged();
        notifyObservers(score);
    }

    private Piece initializePiece() {
        Piece p = getNouvellePiece();
        this.isNewNextPiece = true;
        p.setPos(3, 0);
        return p;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setCell(int x, int y, PieceColor value) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y][x] = value;
        }
    }

    public void signalVue() {
        setChanged();
        notifyObservers(grid);
    }

    public PieceColor getCell(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[y][x];
        }
        return null;
    }

    public boolean isNewNextPiece() {
        return isNewNextPiece;
    }

    public void setNewNextPiece(boolean isNewNextPiece) {
        this.isNewNextPiece = isNewNextPiece;
    }

    public List<Piece> getNextPiece() {
        return this.pieceManager.getNextPiece();
    }

    public Piece getHoldPiece() {
        return this.pieceManager.getHoldPiece();
    }

    public Piece getNouvellePiece() {
        // make a semi-random choice of a piece to avoid too many duplicates
        java.security.SecureRandom random = new java.security.SecureRandom();
        int idx = random.nextInt(7);
        try {
            return switch (idx) {
                case 0 -> new PieceI(PieceColor.RED);
                case 1 -> new PieceJ(PieceColor.ORANGE);
                case 2 -> new PieceL(PieceColor.BLUE);
                case 3 -> new PieceO(PieceColor.YELLOW);
                case 4 -> new PieceS(PieceColor.GREEN);
                case 5 -> new PieceT(PieceColor.CYAN);
                case 6 -> new PieceZ(PieceColor.PINK);
                default -> throw new IllegalArgumentException("Invalid piece index: " + idx);
            };
        } catch (Exception e) {
            System.err.println("Error creating piece: " + e.getMessage());
            return null;
        }
    }

    private boolean isInCurrentPiece(int x, int y) {
        Piece currentPiece = this.pieceManager.getCurrentPiece();
        int[][] coords = currentPiece.getCoordinates(currentPiece.getX(), currentPiece.getY());
        for (int[] c : coords) {
            if (c[0] == x && c[1] == y) {
                return true;
            }
        }
        return false;
    }

    public boolean checkCollision(int[][] shape) {
        List<int[]> pointsToCheck = new ArrayList<>();
        for (int[] pos : shape) {
            if (!isInCurrentPiece(pos[0], pos[1])) {
                pointsToCheck.add(pos);
            }
        }

        for (int[] c : pointsToCheck) {
            int x_next = c[0];
            int y_next = c[1];
            if (x_next < 0 || x_next >= width || y_next >= height || (y_next >= 0 && !(grid[y_next][x_next] == PieceColor.NONE))) {
                return false;
            }
        }

        return true;
    }

    private void checkingLines() {
        int countPoints = 0;
        for (int y = 0; y < height; y++) {
            if (isLineComplete(y)) {
                deleteLine(y);
                countPoints += 10;
            }
        }
        if (countPoints > 0) {
            updateScore(countPoints * countPoints); // to favorize double, triple, etc.
            setChanged();
            notifyObservers(countPoints * countPoints);
        }
    }

    private void deleteLine(int y) {
        lineDeleteCount++;
        for (int x = 0; x < width; x++) {
            setCell(x, y, PieceColor.NONE);
        }
        for (int i = y; i > 0; i--) {
            if (width >= 0) System.arraycopy(grid[i - 1], 0, grid[i], 0, width);
        }
        for (int x = 0; x < width; x++) {
            grid[0][x] = PieceColor.NONE;
        }

        if (lineDeleteCount == 1) {
            nextLevel();
        }
    }

    private boolean isLineComplete(int y) {
        for (int x = 0; x < width; x++) {
            if (grid[y][x] == PieceColor.NONE) {
                return false;
            }
        }
        return true;
    }

    public boolean canHoldPiece() {
        return canHoldPiece;
    }


    public void movePiece(int x_move, int y_move, boolean fixPiece) {
        Piece currentPiece = this.pieceManager.getCurrentPiece();
        int[][] nextCoords = currentPiece.getCoordinates(currentPiece.getX() + x_move, currentPiece.getY() + y_move);

        if (!checkCollision(nextCoords)) {
            if (!fixPiece) {
                return;
            }

            for (int[] c : nextCoords) {
                int x = c[0] - x_move;
                int y = c[1] - y_move;
                setCell(x, y, currentPiece.getColor());
            }

            // check end game
            if (currentPiece.getY() == 0) {
                System.out.println("Game Over");
                this.isGameOver = true;
                return;
            }
            // we check if a line is complete
            checkingLines();
            // we create a new piece
            this.pieceManager.setCurrentPiece(this.pieceManager.getNextPiece().getFirst());
            this.pieceManager.getNextPiece().removeFirst();
            this.pieceManager.getNextPiece().addLast(initializePiece());
            this.setNewNextPiece(true);

            this.canHoldPiece = true;
        }


        currentPiece.setPos(currentPiece.getX() + x_move, currentPiece.getY() + y_move);
        signalVue();
    }

    public void rotatePiece(boolean isLeft) {
        Piece currentPiece = this.pieceManager.getCurrentPiece();
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
            currentPiece.setShape(originalRotatedShape);
            signalVue();
        }
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void reset() {
        this.score = 0;
        this.lineDeleteCount = 0;
        this.seconds = 0;
        this.level = new Level(0);
        this.isGameOver = false;
        this.canHoldPiece = false;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[y][x] = PieceColor.NONE;
            }
        }

        // we create a new piece
        this.pieceManager.setCurrentPiece(initializePiece());
        this.pieceManager.getNextPiece().clear();
        for (int i = 0; i < 3; i++) {
            this.pieceManager.getNextPiece().addLast(initializePiece());
        }
        setChanged();
        notifyObservers(grid);
    }

    public Piece getCurrentPiece() {
        return this.pieceManager.getCurrentPiece();
    }

    public void echangeHoldAndCurrent() {
        if (this.pieceManager.getHoldPiece() == null) {
            Piece piece = this.pieceManager.getCurrentPiece();
            piece.setPos(3,1);
            this.pieceManager.setCurrentPiece(piece);
            this.pieceManager.setHoldPiece(this.pieceManager.getCurrentPiece());

            this.pieceManager.setCurrentPiece(this.pieceManager.getNextPiece().getFirst());
            this.pieceManager.getNextPiece().removeFirst();
            this.pieceManager.getNextPiece().addLast(initializePiece());

        } else {
            Piece temp = this.pieceManager.getCurrentPiece();
            temp.setPos(3,1);
            this.pieceManager.setCurrentPiece(this.pieceManager.getHoldPiece());
            this.pieceManager.setHoldPiece(temp);

        }
        this.isNewNextPiece = true;
        this.canHoldPiece = false;
        setChanged();
        notifyObservers(grid);
    }

    public Level getLevel() {
        return level;
    }

    public void nextLevel() {
        this.level = new Level(this.level.getLevel() + 1);

        setChanged();
        notifyObservers(level);
    }

    public void incrementSeconds() {
        this.seconds++;
    }
}
