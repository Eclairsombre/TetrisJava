package Tetris.model;

import Tetris.model.Piece.Piece;
import Tetris.model.Piece.PieceColor;
import Tetris.model.Piece.PieceManager;

import java.util.Observable;

@SuppressWarnings("deprecation")
public class Grid extends Observable {
    private final int width;
    private final int height;
    private final PieceColor[][] grid;
    private final PieceManager pieceManager;
    private final StatsValues statsValues = new StatsValues();
    public FileWriterAndReader fileWriterAndReader = new FileWriterAndReader(
            "app/src/main/resources/score.txt"
    );
    private boolean isPaused = false;

    public Grid(int width, int height) {
        this.height = height;
        this.width = width;
        this.grid = new PieceColor[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[y][x] = PieceColor.NONE;
            }
        }
        this.pieceManager = new PieceManager();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public FileWriterAndReader getFileWriterAndReader() {
        return fileWriterAndReader;
    }

    public void signalChange(String type) {
        setChanged();
        notifyObservers(type);
    }

    public StatsValues getStatsValues() {
        return statsValues;
    }

    public void updateScore(int points) {
        statsValues.score += points;
        signalChange("stats");
    }

    public void nextLevel() {
        statsValues.level = new Level(statsValues.level.getLevel() + 1);
        signalChange("level");
    }

    public void incrementSeconds() {
        if (!isPaused) {
            statsValues.incrementSeconds();
            signalChange("timer");
        }
    }

    public PieceManager getPieceManager() {
        return this.pieceManager;
    }

    public void exchangeHoldAndCurrent() {
        if (pieceManager.exchangeHoldAndCurrent()) { // the exchange is possible and done
            signalChange("grid");
            signalChange("nextPiece");
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

    public PieceColor getCell(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[y][x];
        }
        return null;
    }

    public void setCell(int x, int y, PieceColor value) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y][x] = value;
        }
    }

    public boolean isValidPosition(int[][] pieceCoords) {
        for (int[] c : pieceCoords) {
            int x_next = c[0];
            int y_next = c[1];
            if (isInCurrentPiece(x_next, y_next)) {
                continue;
            }
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
            updateScore(countPoints * countPoints); // pow to favorize double, triple, etc.
            signalChange("stats");
        }
    }

    private void deleteLine(int y) {
        statsValues.lineDeleteCount++;
        for (int x = 0; x < width; x++) {
            setCell(x, y, PieceColor.NONE);
        }
        for (int i = y; i > 0; i--) {
            if (width >= 0) System.arraycopy(grid[i - 1], 0, grid[i], 0, width);
        }
        if (statsValues.lineDeleteCount % 10 == 0) {
            nextLevel(); // signalChange("level");
        }
        signalChange("stats");
        // signalChange("grid"); after this function because we call it in the movePiece method
    }

    private boolean isLineComplete(int y) {
        for (int x = 0; x < width; x++) {
            if (grid[y][x] == PieceColor.NONE) {
                return false;
            }
        }
        return true;
    }

    public int getMaxHeightEmpty(Piece piece) {
        int maxHeight = height - 1;
        for (int i = 0; i < height; i++) {
            if (!isValidPosition(piece.getCoordinates(piece.getX(), i))) {
                maxHeight = i - 1;
                break;
            }
        }
        return maxHeight;
    }

    public void doRdrop() {
        int move_to = getMaxHeightEmpty(pieceManager.getCurrentPiece());
        for (int i = 0; i < move_to; i++) {
            movePiece(0, 1, false);
        }
        movePiece(0, 1, true);
        updateScore(30);
    }

    public void movePiece(int x_move, int y_move, boolean fixPiece) {
        if (isPaused) {
            return;
        }

        Piece currentPiece = this.pieceManager.getCurrentPiece();
        int[][] nextCoords = currentPiece.getCoordinates(currentPiece.getX() + x_move, currentPiece.getY() + y_move);

        if (isValidPosition(nextCoords)) {
            currentPiece.setPos(currentPiece.getX() + x_move, currentPiece.getY() + y_move);
            signalChange("grid");
            return;
        }
        if (!fixPiece) {
            return;
        }

        // fix the piece
        for (int[] c : nextCoords) {
            int x = c[0] - x_move;
            int y = c[1] - y_move;
            setCell(x, y, currentPiece.getColor());
        }

        // check end game
        if (currentPiece.getY() == 0) {
            this.saveScore();
            signalChange("gameOver");
            return;
        }
        checkingLines();
        this.pieceManager.changePiece();
        signalChange("nextPiece");
        signalChange("grid");
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

        if (isValidPosition(rotatedShape)) {
            currentPiece.setShape(originalRotatedShape);
            signalChange("grid");
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void reset() {
        this.isPaused = false;
        statsValues.reset();
        this.pieceManager.reset();
        for (PieceColor[] row : grid) {
            java.util.Arrays.fill(row, PieceColor.NONE);
        }
        signalChange("grid");
        signalChange("nextPiece");
        signalChange("stats");
    }

    public void saveScore() {
        String[] lines = fileWriterAndReader.readFromFile();
        // TODO : Check if it really works for the level, I have a doubt (see score.txt)
        String nouvelleLigne = "Level :" + (statsValues.level.getLevel() + 1) + " , " + statsValues.score + " , " + statsValues.getTime();
        java.util.List<String> allScores = new java.util.ArrayList<>();

        for (String line : lines) {
            if (line != null && !line.trim().isEmpty() && line.split(" , ").length >= 2) {
                allScores.add(line);
            }
        }

        allScores.add(nouvelleLigne);
        allScores.sort((a, b) -> {
            try {
                String[] partsA = a.split(" , ");
                String[] partsB = b.split(" , ");

                if (partsA.length < 2 || partsB.length < 2) {
                    return 0;
                }

                int scoreA = Integer.parseInt(partsA[1].trim());
                int scoreB = Integer.parseInt(partsB[1].trim());
                return Integer.compare(scoreB, scoreA);
            } catch (NumberFormatException e) {
                return 0;
            }
        });

        fileWriterAndReader.writeToFile(allScores);
    }
}
