package Tetris.model;

import Tetris.model.Piece.Piece;
import Tetris.model.Piece.PieceColor;
import Tetris.model.Piece.PieceManager;
import Tetris.model.Piece.PieceTemplate.PieceT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import static java.lang.Thread.sleep;

@SuppressWarnings("deprecation")
public class Grid extends Observable {
    private final int width;
    private final int height;
    private final PieceColor[][] grid;
    private final PieceManager pieceManager;
    private final StatsValues statsValues;
    private final int debugPos;
    private boolean isPaused = false;
    private boolean TSpin = false;
    private boolean isFixing = false;
    private final AiUtils aiUtils;

    public Grid(int width, int height, boolean debugMode, int debugPos) {
        this.statsValues = new StatsValues(() -> signalChange("stats"));
        this.height = height;
        this.width = width;
        this.grid = new PieceColor[height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[y][x] = PieceColor.NONE;
            }
        }
        this.pieceManager = new PieceManager(debugMode);
        this.debugPos = debugPos;
        this.aiUtils = new AiUtils(width, height);

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public FileWriterAndReader getFileWriterAndReader() {
        return statsValues.fileWriterAndReader;
    }

    public void signalChange(String type) {
        setChanged();
        notifyObservers(type);
    }

    public StatsValues getStatsValues() {
        return statsValues;
    }

    public void addToScore(int points) {
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

    private void clearLines() {
        int nbLinesCleared = 0;
        List<Integer> linesToDelete = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            if (isLineComplete(y, grid)) { // piece already added
                linesToDelete.add(y);
            }
        }
        for (int y : linesToDelete) {
            colorWhiteLine(y);
            nbLinesCleared++;
        }

        if (nbLinesCleared > 0) { // we have some points to add
            try {
                sleep(100);
            } catch (InterruptedException e) {
                // we don't care
            }

            for (int y : linesToDelete) {
                deleteLine(y);
            }

            statsValues.calculateScore(nbLinesCleared, TSpin, isAllClear());
            signalChange("stats");
            statsValues.execResetScoreSkillLabel();
        }
    }

    private boolean isAllClear() {
        for (PieceColor[] c : grid) {
            for (PieceColor i : c) {
                if (i != PieceColor.NONE) {
                    return false;
                }
            }
        }
        return true;
    }

    private void colorWhiteLine(int y) {
        for (int x = 0; x < width; x++) {
            setCell(x, y, PieceColor.WHITE);
        }
        signalChange("grid");
    }

    private PieceColor[][] getTempGrid(int x, int y, int[][] shape) {
        PieceColor[][] tempGrid = new PieceColor[height][width];
        for (int r = 0; r < height; r++) {
            System.arraycopy(grid[r], 0, tempGrid[r], 0, width);
        }

        for (int[] point : shape) {
            int testX = point[0] + x;
            int testY = point[1] + y;
            if (testX >= 0 && testX < width && testY >= 0 && testY < height) {
                tempGrid[testY][testX] = pieceManager.getCurrentPiece().getColor();
            }
        }
        return tempGrid;
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

    private boolean isLineComplete(int y, PieceColor[][] usePiece) {
        for (int x = 0; x < width; x++) {
            if (usePiece[y][x] == PieceColor.NONE) {
                return false;
            }
        }
        return true;
    }

    public int findMaxY(int[][] shape, int x) {
        int y = 0;
        while (y < height) {
            if (!isValidPositionForShape(shape, x, y + 1)) {
                break;
            }
            y++;
        }
        return y;
    }

    public void doRdrop(boolean incrementScore) {
        int move_to = findMaxY(pieceManager.getCurrentPiece().getShape(), pieceManager.getCurrentPiece().getX());
        for (int i = 0; i < move_to; i++) {
            movePiece(0, 1, false, incrementScore);
        }
        movePiece(0, 1, true, false);
    }

    public synchronized void movePiece(int x_move, int y_move, boolean fixPiece, boolean incrementScore) {
        if (isPaused) {
            return;
        }

        Piece currentPiece = this.pieceManager.getCurrentPiece();
        if (isValidPositionForShape(currentPiece.getShape(), currentPiece.getX() + x_move, currentPiece.getY() + y_move)) {
            if (incrementScore) {
                addToScore(1);
            }
            currentPiece.setPos(currentPiece.getX() + x_move, currentPiece.getY() + y_move);
            signalChange("grid");
            return;
        }
        if (!fixPiece) {
            return;
        }

        // check end game
        if (currentPiece.getY() == 0) {
            this.statsValues.saveScore();
            signalChange("gameOver");
            return;
        }

        if (!isFixing) { // to avoid double fixing (one by the user and one by the thread)
            signalChange("fixPiece"); // signal the thread
            fixPiece(currentPiece, currentPiece.getColor());
        }
    }

    public void fixPiece(Piece currentPiece, PieceColor color) {
        Thread fixPieceThread = new Thread(() -> {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                // we don't care
                System.out.println("Error sleeping");
            }
            if (!isValidPositionForShape(currentPiece.getShape(), currentPiece.getX(), currentPiece.getY() + 1)) { // we check if the piece must be fixed
                for (int[] c : currentPiece.getCoordinates(currentPiece.getX(), currentPiece.getY())) {
                    if (c[1] >= 0) {
                        setCell(c[0], c[1], color);
                    }
                }

                TSpin = false; // reset T-Spin flag
                checkTSpin(); // check if we have a T-Spin
                clearLines();
                this.pieceManager.changePiece();
                signalChange("nextPiece");
                signalChange("grid");
            }
            // if the piece is not fixed, we don't do anything and we restart the thread
            isFixing = false;
            signalChange("fixPiece");
        });

        fixPieceThread.start();
        isFixing = true;
    }

    private void checkTSpin() {
        if (pieceManager.getCurrentPiece() instanceof PieceT) {
            Piece currentPiece = pieceManager.getCurrentPiece();
            int[][] coords = { {0, 0}, {2, 0}, {0, 2}, {2, 2} };
            int count = 0;
            for (int[] c : coords) {
                if (!isValidPositionForShape(new int[][]{{currentPiece.getX(), currentPiece.getY()}}, c[0], c[1])) {
                    count++;
                }
            }
            if (count >= 3) {
                TSpin = true; // We can take into consideration mini T-Spin by checking is the 2 upper corners are filled
            }
        }
    }

    public void rotatePiece(boolean isLeft) {
        Piece currentPiece = this.pieceManager.getCurrentPiece();
        int[][] originalRotatedShape = currentPiece.getRotatedPosition(isLeft);
        int[][] kicks = {{0, 0}, {0, 1}, {1, 0}, {-1, 0}};

        if (tryRotate(kicks, currentPiece, originalRotatedShape)) {
            return;
        }

        if (!(currentPiece instanceof PieceT)) {
            return;
        }

        int[][] upperCorner;
        String direction = ((PieceT) currentPiece).getDirection();
        switch (direction) {
            case "up" ->
                    upperCorner = isLeft ? new int[][]{{2, 0}} : new int[][]{{0, 0}};
            case "down" ->
                    upperCorner = isLeft ? new int[][]{{0, 0}} : new int[][]{{2, 0}};
            case "left" ->
                    upperCorner = isLeft ? new int[][]{{0, 0}, {0, 1}, {0, 2}} : null;
            case "right" ->
                    upperCorner = isLeft ? null : new int[][]{{2, 0}, {2, 1}, {2, 2}};
            case null, default -> {
                System.out.println("Error: direction is null");
                return;
            }
        }

        // special case for T-Spin
        kicks = new int[][]{{1, 1}, {-1, 1}, {0, 2}, {-1, 2}, {1, 2}};
        if (!isValidPositionForShape(upperCorner, currentPiece.getX(), currentPiece.getY())) {
            tryRotate(kicks, currentPiece, originalRotatedShape);
        }
    }

    private boolean tryRotate(int[][] kicks, Piece currentPiece, int[][] originalRotatedShape) {
        for (int[] kick : kicks) {
            if (isValidPositionForShape(originalRotatedShape, currentPiece.getX() + kick[0], currentPiece.getY() + kick[1])) {
                currentPiece.setShape(originalRotatedShape);
                currentPiece.setPos(currentPiece.getX() + kick[0], currentPiece.getY() + kick[1]);
                signalChange("grid");
                return true;
            }
        }
        return false;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
        if (paused) {
            signalChange("pause");
        }
    }

    public void reset(boolean debugMode) {
        this.isPaused = false;
        statsValues.reset();
        this.pieceManager.reset(debugMode);
        for (PieceColor[] row : grid) {
            java.util.Arrays.fill(row, PieceColor.NONE);
        }
        if (debugMode) {
            for (int i = 18; i < 25; i++) {
                for (int j = 0; j < 10; j++) {
                    grid[i][j] = PieceColor.RED;
                }
            }

            switch (debugPos) {
                case 0, 3 -> {
                    if (debugPos == 3) {
                        grid[16][2] = PieceColor.RED;
                        grid[16][6] = PieceColor.RED;
                    }
                    grid[17][1] = PieceColor.RED;
                    grid[17][7] = PieceColor.RED;
                    grid[18][2] = PieceColor.NONE;
                    grid[18][6] = PieceColor.NONE;
                    grid[19][1] = PieceColor.NONE;
                    grid[19][2] = PieceColor.NONE;
                    grid[19][3] = PieceColor.NONE;
                    grid[19][5] = PieceColor.NONE;
                    grid[19][6] = PieceColor.NONE;
                    grid[19][7] = PieceColor.NONE;
                    grid[20][2] = PieceColor.NONE;
                    grid[20][6] = PieceColor.NONE;
                }
                case 1 -> {
                    grid[18][3] = PieceColor.NONE;
                    grid[18][4] = PieceColor.NONE;
                    grid[19][2] = PieceColor.NONE;
                    grid[19][3] = PieceColor.NONE;
                    grid[19][4] = PieceColor.NONE;
                    grid[20][4] = PieceColor.NONE;
                    grid[21][4] = PieceColor.NONE;
                }
                case 2 -> {
                    grid[18][3] = PieceColor.NONE;
                    grid[18][4] = PieceColor.NONE;
                    grid[19][2] = PieceColor.NONE;
                    grid[19][3] = PieceColor.NONE;
                    grid[19][4] = PieceColor.NONE;
                    grid[20][2] = PieceColor.NONE;
                    grid[21][2] = PieceColor.NONE;
                    grid[21][3] = PieceColor.NONE;
                    grid[22][2] = PieceColor.NONE;
                    grid[22][3] = PieceColor.NONE;
                    grid[22][4] = PieceColor.NONE;
                    grid[23][4] = PieceColor.NONE;
                    grid[23][5] = PieceColor.NONE;
                    grid[24][4] = PieceColor.NONE;
                }
            }
        }
        signalChange("grid");
        signalChange("nextPiece");
        signalChange("stats");
    }

    public int[] getBestMove() {
        Piece currentPiece = pieceManager.getCurrentPiece();
        Piece nextPiece = pieceManager.getNextPiece().getFirst();
        Piece holdPiece = pieceManager.getHoldPiece();

        int bestScore = Integer.MIN_VALUE;
        int bestX = -1;
        int bestRotation = -1;

        int[][] originalShape = currentPiece.getShape();
        int[][] originalHoldPieceShape = holdPiece != null ? holdPiece.getShape() : null;
        int[][] originalNextPieceShape = nextPiece.getShape();

        int[] values = findBestMoveForPiece(originalShape, currentPiece, bestScore, bestX, bestRotation);
        bestScore = values[0];
        bestX = values[1];
        bestRotation = values[2];
        String whichPieceToUse = "current";

        values = findBestMoveForPiece(
                holdPiece != null ? originalHoldPieceShape : originalNextPieceShape,
                holdPiece != null ? holdPiece : nextPiece,
                bestScore,
                bestX,
                bestRotation
        );
        if (values[0] != bestScore || values[1] != bestX || values[2] != bestRotation) {
            bestX = values[1];
            bestRotation = values[2];
            whichPieceToUse = "hold";
        }

        currentPiece.setShape(originalShape);
        if (holdPiece != null) {
            holdPiece.setShape(originalHoldPieceShape);
        } else {
            nextPiece.setShape(originalNextPieceShape);
        }

        switch (whichPieceToUse) {
            case "hold" -> {
                int[] moves = aiUtils.calculateMoves(currentPiece.getX(), bestX, bestRotation);
                int[] temp = new int[moves.length + 1];
                temp[0] = 6;
                System.arraycopy(moves, 0, temp, 1, moves.length);
                return temp;
            }
            case "current" -> {
                return aiUtils.calculateMoves(currentPiece.getX(), bestX, bestRotation);
            }
        }

        return new int[0]; // should never happen
    }

    public int[] findBestMoveForPiece(int[][] originalNextPieceShape, Piece piece, int bestScore, int bestX, int bestRotation) {
        for (int rotation = 0; rotation < 4; rotation++) {
            int[][] rotatedShape = originalNextPieceShape;
            if (rotation > 0) {
                for (int r = 0; r < rotation; r++) {
                    rotatedShape = piece.getRotatedPosition(false);
                }
            }

            for (int x = -1; x < width; x++) {
                if (isValidPositionForShape(rotatedShape, x, piece.getY())) {
                    int maxY = findMaxY(rotatedShape, x);
                    int score = aiUtils.evaluatePosition(maxY, rotatedShape, getTempGrid(x, maxY, rotatedShape));
                    if (score > bestScore) {
                        bestScore = score;
                        bestX = x;
                        bestRotation = rotation;
                    }
                }
            }
        }
        return new int[]{bestScore, bestX, bestRotation};
    }

    private boolean isValidPositionForShape(int[][] shape, int x, int y) {
        if (shape == null) {
            return false;
        }
        for (int[] pos : shape) {
            int x_next = pos[0] + x;
            int y_next = pos[1] + y;
            if (Arrays.stream(shape).anyMatch(c -> c[0] == x && c[1] == y)) { // case where the piece is already in the grid
                continue;
            }
            if (x_next < 0 || x_next >= width || y_next >= height || (y_next >= 0 && !(grid[y_next][x_next] == PieceColor.NONE))) {
                return false;
            }
        }
        return true;
    }
}
