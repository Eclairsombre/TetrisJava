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
        if (paused) {
            signalChange("pause");
        }
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

    public int[] getBestMove() {
        Piece currentPiece = pieceManager.getCurrentPiece();
        int bestScore = Integer.MIN_VALUE;
        int bestX = currentPiece.getX();
        int bestY = currentPiece.getY();
        int bestRotation = 0;

        int[][] originalShape = currentPiece.getShape();

        for (int rotation = 0; rotation < 4; rotation++) {

            int[][] rotatedShape = currentPiece.getShape();
            if (rotation > 0) {

                for (int r = 0; r < rotation; r++) {
                    rotatedShape = applyRotation(rotatedShape, false); // false pour rotation à droite
                }
            }


            for (int x = -1; x < width; x++) {

                if (isValidPositionForShape(rotatedShape, x, currentPiece.getY())) {

                    int maxY = findMaxY(rotatedShape, x);


                    int score = evaluatePosition(currentPiece, x, maxY, rotatedShape);

                    if (score > bestScore) {
                        bestScore = score;
                        bestX = x;
                        bestY = maxY;
                        bestRotation = rotation;
                    }
                }
            }
        }


        currentPiece.setShape(originalShape);


        return calculateMoves(currentPiece.getX(), currentPiece.getY(), bestX, bestY, bestRotation);
    }


    private boolean isValidPositionForShape(int[][] shape, int x, int y) {
        for (int[] point : shape) {
            int testX = point[0] + x;
            int testY = point[1] + y;
            if (testX < 0 || testX >= width || testY < 0 || testY >= height ||
                    (testY >= 0 && testX >= 0 && testX < width && testY < height &&
                            grid[testY][testX] != PieceColor.NONE)) {
                return false;
            }
        }
        return true;
    }


    private int findMaxY(int[][] shape, int x) {
        int y = 0;
        while (y < height) {
            if (!isValidPositionForShape(shape, x, y + 1)) {
                break;
            }
            y++;
        }
        return y;
    }


    private int[][] applyRotation(int[][] shape, boolean isLeft) {

        int[][] rotated = new int[shape.length][2];
        for (int i = 0; i < shape.length; i++) {
            if (isLeft) {

                rotated[i][0] = -shape[i][1];
                rotated[i][1] = shape[i][0];
            } else {

                rotated[i][0] = shape[i][1];
                rotated[i][1] = -shape[i][0];
            }
        }
        return rotated;
    }


    private int[] calculateMoves(int startX, int startY, int targetX, int targetY, int rotations) {

        java.util.List<Integer> moves = new java.util.ArrayList<>();


        for (int i = 0; i < rotations; i++) {
            moves.add(1);
        }


        int dx = targetX - startX;
        while (dx != 0) {
            if (dx < 0) {
                moves.add(2);
                dx++;
            } else {
                moves.add(3);
                dx--;
            }
        }


        moves.add(5);


        int[] result = new int[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            result[i] = moves.get(i);
        }

        return result;
    }


    private int evaluatePosition(Piece piece, int x, int y, int[][] shape) {
        int score = 0;


        int maxHeight = getMaxHeightAfterPlacement(x, y, shape);
        int completeLines = getCompleteLinesAfterPlacement(x, y, shape);
        int holes = getHoleAfterPlacement(x, y, shape);
        int bumpiness = getBumpinessAfterPlacement(x, y, shape);


        score -= maxHeight * 500;
        System.out.println(maxHeight);
        score += completeLines * 1000;

        score -= holes * 500;
        score -= bumpiness * 300;
        return score;
    }


    private int getMaxHeightAfterPlacement(int x, int y, int[][] shape) {
        int maxHeight = 0;

        for (int[] point : shape) {
            int testY = point[1] + y;
            maxHeight = Math.max(maxHeight, height - testY);
        }

        boolean[][] tempGrid = new boolean[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                tempGrid[r][c] = grid[r][c] != PieceColor.NONE;
            }
        }

        for (int[] point : shape) {
            int testX = point[0] + x;
            int testY = point[1] + y;
            if (testX >= 0 && testX < width && testY >= 0 && testY < height) {
                tempGrid[testY][testX] = true;
            }
        }

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                if (tempGrid[row][col]) {
                    int colHeight = height - row;
                    maxHeight = Math.max(maxHeight, colHeight);
                    break;
                }
            }
        }

        return maxHeight;
    }

    public int getCompleteLinesAfterPlacement(int x, int y, int[][] shape) {
        int completeLines = 0;


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

        for (int row = 0; row < height; row++) {
            boolean isComplete = true;
            for (int col = 0; col < width; col++) {
                if (tempGrid[row][col] == PieceColor.NONE) {
                    isComplete = false;
                    break;
                }
            }
            if (isComplete) {
                completeLines++;
            }
        }

        return completeLines;
    }

    public int getHoleAfterPlacement(int x, int y, int[][] shape) {
        int holeCount = 0;

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


        for (int col = 0; col < width; col++) {
            boolean blockFound = false;
            for (int row = 0; row < height; row++) {
                if (tempGrid[row][col] != PieceColor.NONE) {
                    blockFound = true;
                } else if (blockFound) {
                    holeCount++;
                }
            }
        }

        return holeCount;
    }

    public int getBumpinessAfterPlacement(int x, int y, int[][] shape) {
        int bumpiness = 0;


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


        int[] heights = new int[width];
        for (int col = 0; col < width; col++) {
            for (int row = height - 1; row >= 0; row--) {
                if (tempGrid[row][col] != PieceColor.NONE) {
                    heights[col] = height - row;
                    break;
                }
            }
        }


        for (int i = 1; i < heights.length; i++) {
            bumpiness += Math.abs(heights[i] - heights[i - 1]);
        }

        return bumpiness;
    }

}

