package Tetris.model;

import Tetris.model.Piece.Piece;
import Tetris.model.Piece.PieceColor;
import Tetris.model.Piece.PieceManager;
import Tetris.model.Piece.PieceTemplate.PieceT;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static java.lang.Thread.sleep;

@SuppressWarnings("deprecation")
public class Grid extends Observable {
    private final int width;
    private final int height;
    private final PieceColor[][] grid;
    private final PieceManager pieceManager;
    private final StatsValues statsValues = new StatsValues();
    private final int debugPos;
    public FileWriterAndReader fileWriterAndReader = new FileWriterAndReader(
            "app/src/main/resources/score.txt"
    );
    private boolean isPaused = false;
    private boolean TSpin = false;
    private int BtBCounter = 0;
    private boolean isFixing = false;
    private Thread resetScoreSkillLabel;

    public Grid(int width, int height, boolean debugMode, int debugPos) {
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
        resetScoreSkillLabel = new Thread(() -> { // used for delete line clear displaying
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                // we don't care
            }
            statsValues.lineClearDisplay = "";
            signalChange("stats");
        });
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

            calculateScore(nbLinesCleared);
        }
    }

    private void calculateScore(int nbLinesCleared) {
        int score = 0;
        String label = "";
        switch (nbLinesCleared) {
            case 1 -> {
                label = "Single";
                score = 100;
                if (TSpin) {
                    label += " T-Spin";
                    score = 400;
                    BtBCounter += 1;
                } else {
                    BtBCounter = 0;
                }
                if (allClear()) {
                    label += " All clear";
                    score = 800;
                }
            }
            case 2 -> {
                label = "Double";
                score = 300;
                if (TSpin) {
                    label += " T-Spin";
                    score = 1200;
                    BtBCounter += 1;
                } else {
                    BtBCounter = 0;
                }
                if (allClear()) {
                    label += " All clear";
                    score = 1200;
                }
            }
            case 3 -> {
                label = "Triple";
                score = 500;
                if (TSpin) {
                    label += " T-Spin triple";
                    score = 1600;
                    BtBCounter += 1;
                } else {
                    BtBCounter = 0;
                }
                if (allClear()) {
                    label += " All clear";
                    score = 1800;
                }
            }
            case 4 -> {
                label = "Tetris";
                if (allClear()) {
                    label += " All clear";
                    score = 2000;
                } else {
                    score = 800;
                }
                BtBCounter += 1;
            }
        }

        if (BtBCounter >= 2) {
            score = (int) (score * 1.5);
            label += " BtB !";
        }

        statsValues.lineClearDisplay = label;

        addToScore(score);
        signalChange("stats");
        if (resetScoreSkillLabel.isAlive()) {
            resetScoreSkillLabel.interrupt();
        }
        resetScoreSkillLabel = new Thread(() -> {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                // we don't care
            }
            statsValues.lineClearDisplay = "";
            signalChange("stats");
        });
        resetScoreSkillLabel.start();
    }

    private boolean allClear() {
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
        int[][] nextCoords = currentPiece.getCoordinates(currentPiece.getX() + x_move, currentPiece.getY() + y_move);

        if (isValidPosition(nextCoords)) {
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
            this.saveScore();
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
                sleep(200);
            } catch (InterruptedException e) {
                // we don't care
                System.out.println("Error sleeping");
            }
            int[][] pieceCoords = currentPiece.getCoordinates(currentPiece.getX(), currentPiece.getY() + 1);

            if (!isValidPosition(pieceCoords)) { // we check if the piece must be fixed
                for (int[] c : pieceCoords) {
                    if (c[1] >= 0) {
                        setCell(c[0], c[1] - 1, color);
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
            int[][] coords = {
                    {currentPiece.getX(), currentPiece.getY()},
                    {currentPiece.getX() + 2, currentPiece.getY()},
                    {currentPiece.getX(), currentPiece.getY() + 2},
                    {currentPiece.getX() + 2, currentPiece.getY() + 2}
            };
            int count = 0;
            for (int[] c : coords) {
                if (!isValidPosition(new int[][]{c})) {
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
        int[][] rotatedShape = new int[originalRotatedShape.length][originalRotatedShape[0].length];
        for (int i = 0; i < originalRotatedShape.length; i++) {
            System.arraycopy(originalRotatedShape[i], 0, rotatedShape[i], 0, originalRotatedShape[i].length);
        }

        for (int[] c : rotatedShape) {
            c[0] += currentPiece.getX();
            c[1] += currentPiece.getY();
        }

        int[][] kicks = {{0, 0}, {0, 1}, {1, 0}, {-1, 0}};
        if (tryRotate(rotatedShape, kicks, currentPiece, originalRotatedShape)) {
            return;
        }

        if (!(currentPiece instanceof PieceT)) {
            return;
        }

        int[] upperCorner;
        String direction = ((PieceT) currentPiece).getDirection();
        switch (direction) {
            case "up" ->
                    upperCorner = isLeft ? new int[]{currentPiece.getX() + 2, currentPiece.getY()} : new int[]{currentPiece.getX(), currentPiece.getY()};
            case "down" ->
                    upperCorner = isLeft ? new int[]{currentPiece.getX(), currentPiece.getY()} : new int[]{currentPiece.getX() + 2, currentPiece.getY()};
            case "left" ->
                    upperCorner = isLeft ? new int[]{currentPiece.getX() + 2, currentPiece.getY()} : new int[]{currentPiece.getX() + 2, currentPiece.getY() + 2};
            case "right" ->
                    upperCorner = isLeft ? new int[]{currentPiece.getX(), currentPiece.getY() + 2} : new int[]{currentPiece.getX(), currentPiece.getY()};
            case null, default -> {
                System.out.println("Error: direction is null");
                return;
            }
        }

        // special case for T-Spin
        kicks = new int[][]{{1, 1}, {-1, 1}, {0, 2}, {-1, 2}, {1, 2}};
        if (!isValidPosition(new int[][]{upperCorner})) {
            tryRotate(rotatedShape, kicks, currentPiece, originalRotatedShape);
        }
    }

    private boolean tryRotate(int[][] coords, int[][] kicks, Piece currentPiece, int[][] originalRotatedShape) {
        for (int[] kick : kicks) {
            if (isValidPositionForShape(coords, kick[0], kick[1])) {
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
                    grid[18][2] = PieceColor.NONE;
                    grid[18][3] = PieceColor.NONE;
                    grid[19][1] = PieceColor.NONE;
                    grid[19][2] = PieceColor.NONE;
                    grid[19][3] = PieceColor.NONE;
                    grid[20][1] = PieceColor.NONE;
                    grid[21][1] = PieceColor.NONE;
                    grid[21][2] = PieceColor.NONE;
                    grid[22][1] = PieceColor.NONE;
                    grid[22][2] = PieceColor.NONE;
                    grid[22][3] = PieceColor.NONE;
                    grid[23][3] = PieceColor.NONE;
                    grid[23][4] = PieceColor.NONE;
                    grid[24][3] = PieceColor.NONE;
                }
            }
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
        int bestRotation = 0;

        int[][] originalShape = currentPiece.getShape();

        for (int rotation = 0; rotation < 4; rotation++) {

            int[][] rotatedShape = currentPiece.getShape();
            if (rotation > 0) {

                for (int r = 0; r < rotation; r++) {
                    rotatedShape = applyRotation(rotatedShape, false); // false pour rotation à droite
                } // TODO : why right rotation not used anywhere ?
            }

            for (int x = -1; x < width; x++) {
                if (isValidPositionForShape(rotatedShape, x, currentPiece.getY())) {
                    int maxY = findMaxY(rotatedShape, x);
                    int score = evaluatePosition(x, maxY, rotatedShape);
                    if (score > bestScore) {
                        bestScore = score;
                        bestX = x;
                        bestRotation = rotation;
                    }
                }
            }
        }

        currentPiece.setShape(originalShape);
        return calculateMoves(currentPiece.getX(), bestX, bestRotation);
    }


    private boolean isValidPositionForShape(int[][] shape, int x, int y) {
        int[][] newPosition = new int[shape.length][2];
        for (int i = 0; i < shape.length; i++) {
            newPosition[i][0] = shape[i][0] + x;
            newPosition[i][1] = shape[i][1] + y;
        }
        return isValidPosition(newPosition);
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


    private int[] calculateMoves(int startX, int targetX, int rotations) {

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


    private int evaluatePosition(int x, int y, int[][] shape) {
        int score = 0;

        int maxHeight = getMaxHeightAfterPlacement(x, y, shape);
        int completeLines = getCompleteLinesAfterPlacement(x, y, shape);
        int holes = getHoleAfterPlacement(x, y, shape);
        int bumpiness = getBumpinessAfterPlacement(x, y, shape);

        score -= maxHeight * 730;
        score += completeLines * 608;
        score -= holes * 214;
        score -= bumpiness * 224;


        return score;
    }


    private int getMaxHeightAfterPlacement(int x, int y, int[][] shape) {
        int maxHeight = 0;

        for (int[] point : shape) {
            int testY = point[1] + y;
            maxHeight = Math.max(maxHeight, height - testY);
        }

        PieceColor[][] tempGrid = getTempGrid(x, y, shape);

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                if (tempGrid[row][col] != PieceColor.NONE) {
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

        PieceColor[][] tempGrid = getTempGrid(x, y, shape);

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

        PieceColor[][] tempGrid = getTempGrid(x, y, shape);

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

    public int getBumpinessAfterPlacement(int x, int y, int[][] shape) {
        int bumpiness = 0;

        PieceColor[][] tempGrid = getTempGrid(x, y, shape);

        int[] heights = new int[width];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
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
