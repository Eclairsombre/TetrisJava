package Tetris.Model;

import Tetris.Model.Ai.AIInputStrategy;
import Tetris.Model.Ai.AiUtils;
import Tetris.Model.Piece.Piece;
import Tetris.Model.Piece.PieceColor;
import Tetris.Model.Piece.PieceManager;
import Tetris.Model.Piece.PieceTemplate.PieceT;
import Tetris.Model.Utils.*;

import java.util.*;

import static java.lang.Thread.sleep;

/**
 * Class to manage the grid
 */
@SuppressWarnings("deprecation")
public class Grid extends Observable implements Observer {
    private final boolean debugMode;
    /// debugMode if the game is in debug mode
    private final int width;
    /// width the width of the grid
    private final int height;
    /// height the height of the grid
    private final PieceColor[][] grid;
    /// grid the grid
    private final PieceManager pieceManager;
    /// pieceManager the piece manager
    private final StatsValues statsValues;
    /// statsValues the stats values
    private final int debugPos;
    /// debugPos the debug position
    private final AIInputStrategy aiInputStrategy = new AIInputStrategy();
    /// aiInputStrategy the AI input strategy
    private final AiUtils aiUtils;
    /// aiUtils the AI utils
    boolean isPaused = false;
    /// isPaused if the game is paused
    private boolean TSpin = false;
    /// TSpin if the game is in T-Spin mode
    private boolean isFixing = false;
    /// isFixing if the game is fixing the piece
    private Scheduler scheduler, timer;
    /// scheduler the scheduler
    private boolean aiMode = false;
    /// aiMode if the game is in AI mode
    private boolean isGameOver = false;
    /// isGameOver if the game is over
    private final int idGrid;
    /// idGrid the id of the grid (used for Action reception)


    /**
     * Constructor for the grid
     *
     * @param width     the width of the grid
     * @param height    the height of the grid
     * @param debugMode if the game is in debug mode
     * @param debugPos  the debug position
     */
    public Grid(int width, int height, boolean debugMode, int debugPos, int idGrid) {
        this.debugMode = debugMode;
        this.statsValues = new StatsValues(() -> signalChange("stats"));
        this.height = height;
        this.width = width;
        this.grid = new PieceColor[height][width];
        this.idGrid = idGrid;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[y][x] = PieceColor.NONE;
            }
        }
        this.pieceManager = new PieceManager(debugMode);
        this.debugPos = debugPos;
        this.aiUtils = new AiUtils(width, height);
        reset();
    }

    public int[] getLengthGrid() {
        return new int[]{width, height};
    }

    public PieceColor[][] getGrid() {
        return grid;
    }

    public FileWriterAndReader getFileWriterAndReader() {
        return statsValues.fileWriterAndReader;
    }

    public boolean isAiMode() {
        return aiMode;
    }

    public void setAiMode(boolean isAiMode) {
        this.aiMode = isAiMode;
        if (isAiMode) {
            aiInputStrategy.enable(this);
        } else {
            aiInputStrategy.disable();
        }
        signalChange("AILabel");
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Method to signal a change in the grid.
     *
     * @param type which change appended
     */
    public void signalChange(String type) {
        setChanged();
        switch (type) {
            case "timer" -> notifyObservers(ObservableMessage.of("timer", null, statsValues, null, 0, false));
            case "stats" -> notifyObservers(ObservableMessage.of("stats", null, statsValues, null, 0, false));
            case "grid" ->
                    notifyObservers(ObservableMessage.of("grid", grid, null, pieceManager, findMaxY(pieceManager.getCurrentPiece()), false));
            case "gameOver" -> notifyObservers(ObservableMessage.of("gameOver", null, null, null, 0, false));
            case "nextPiece" -> notifyObservers(ObservableMessage.of("nextPiece", null, null, pieceManager, 0, false));
            case "AILabel" -> notifyObservers(ObservableMessage.of("AILabel", null, null, null, 0, isAiMode()));
            default -> System.out.println("Error: unknown type of signal");
        }
    }

    public StatsValues getStatsValues() {
        return statsValues;
    }

    /**
     * Method to add points to the score.
     *
     * @param points the points to add
     */
    public void addToScore(int points) {
        statsValues.score += points;
        signalChange("stats");
    }

    /**
     * Method to go to the next level.
     */
    public void nextLevel() {
        statsValues.level = new Level(statsValues.level.level() + 1);
        updateLevel();
    }

    /**
     * Method to increment the seconds of the timer.
     */
    public void incrementSeconds() {
        if (!isPaused) {
            statsValues.incrementSeconds();
            signalChange("timer");
        }
    }

    public PieceManager getPieceManager() {
        return this.pieceManager;
    }

    /**
     * Method to exchange the hold and current piece.
     */
    public void exchangeHoldAndCurrent() {
        if (pieceManager.exchangeHoldAndCurrent()) { // the exchange is possible and done
            signalChange("grid");
            signalChange("nextPiece");
        }
    }

    public void setCell(int x, int y, PieceColor value) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y][x] = value;
        }
    }

    /**
     * Method to clean the lines when needed
     */
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
            statsValues.resetLineClearDisplayAfterDelay();
        }
    }

    /**
     * Method to check if the grid is empty
     *
     * @return true if the grid is empty, false otherwise
     */
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

    /**
     * Method to color the line white
     *
     * @param y the line to color
     */
    private void colorWhiteLine(int y) {
        for (int x = 0; x < width; x++) {
            setCell(x, y, PieceColor.WHITE);
        }
        signalChange("grid");
    }

    /**
     * Method to get a copy of the grid with the current piece placed at the given position.
     *
     * @return the grid
     */
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

    /**
     * Method to delete a line
     *
     * @param y the line to delete
     */
    private void deleteLine(int y) {
        statsValues.lineDeleteCount++;
        for (int x = 0; x < width; x++) {
            setCell(x, y, PieceColor.NONE);
        }
        for (int i = y; i > 0; i--) {
            if (width >= 0) System.arraycopy(grid[i - 1], 0, grid[i], 0, width);
        }
        if (statsValues.lineDeleteCount % 10 == 0) {
            nextLevel();
        }
        signalChange("stats");
        // signalChange("grid"); after this function because we call it in the movePiece method
    }

    /**
     * Method to check if the line is complete
     *
     * @param y    the line to check
     * @param grid the grid to check
     * @return true if the line is complete, false otherwise
     */
    private boolean isLineComplete(int y, PieceColor[][] grid) {
        for (int x = 0; x < width; x++) {
            if (grid[y][x] == PieceColor.NONE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to find the maximum Y position for a piece
     *
     * @param piece the piece to check
     * @return the maximum Y position for this piece
     */
    public int findMaxY(Piece piece) {
        return findMaxY(piece.getShape(), piece.getX());
    }

    /**
     * Method to find the maximum Y position for a shape
     *
     * @param shape the shape to check
     * @param x     the horizontal position in the grid
     * @return the maximum Y position for this shape
     */
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

    /**
     * Method to drop the piece to the bottom
     *
     * @param incrementScore if true, increment the score
     */
    public void doRdrop(boolean incrementScore) {
        int move_to = findMaxY(pieceManager.getCurrentPiece());
        for (int i = 0; i < move_to; i++) {
            movePiece(0, 1, false, incrementScore);
        }
        movePiece(0, 1, true, false);
    }

    /**
     * Method to move the piece
     *
     * @param x_move         the x movement
     * @param y_move         the y movement
     * @param fixPiece       if true, fix the piece
     * @param incrementScore if true, increment the score
     */
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
            setAiMode(false);
            isGameOver = true;
            signalChange("gameOver");
            return;
        }

        if (!isFixing) { // to avoid double fixing (one by the user and one by the thread)
            scheduler.setWait();
            fixPiece(currentPiece, currentPiece.getColor());
        }
    }

    /**
     * Method to fix the piece
     *
     * @param currentPiece the current piece
     * @param color        the color of the piece
     */
    public void fixPiece(Piece currentPiece, PieceColor color) {

        Thread fixPieceThread = new Thread(() -> {
            try {
                sleep(aiMode ? 20 : 300);
            } catch (InterruptedException e) {
                // The thread was interrupted, do nothing
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
            scheduler.setWait();
        });

        fixPieceThread.start();
        isFixing = true;
    }

    /**
     * Method to check if the current piece can do a T-Spin
     */
    private void checkTSpin() {
        if (pieceManager.getCurrentPiece() instanceof PieceT) {
            Piece currentPiece = pieceManager.getCurrentPiece();
            int[][] coords = {{0, 0}, {2, 0}, {0, 2}, {2, 2}};
            int count = 0;
            for (int[] c : coords) {
                if (!isValidPositionForShape(new int[][]{{currentPiece.getX(), currentPiece.getY()}}, c[0], c[1])) {
                    count++;
                }
            }
            if (count >= 3) {
                TSpin = true; // We can take into consideration mini T-Spin by checking if the 2 upper corners are filled
            }
        }
    }

    /**
     * Method to rotate the piece
     *
     * @param isLeft true if the piece must be rotated to the left, false otherwise
     */
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
            case "up" -> upperCorner = isLeft ? new int[][]{{2, 0}} : new int[][]{{0, 0}};
            case "down" -> upperCorner = isLeft ? new int[][]{{0, 0}} : new int[][]{{2, 0}};
            case "left" -> upperCorner = isLeft ? new int[][]{{0, 0}, {0, 1}, {0, 2}} : null;
            case "right" -> upperCorner = isLeft ? null : new int[][]{{2, 0}, {2, 1}, {2, 2}};
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

    /**
     * Method to try to rotate the piece
     *
     * @param kicks                the kicks to try
     * @param currentPiece         the current piece
     * @param originalRotatedShape the original rotated shape
     * @return true if the rotation is possible, false otherwise
     */
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

    public void pauseGame() {
        if (scheduler.isAlive()) {
            scheduler.stopThread();
        }
        isPaused = true;
    }

    public void resumeGame() {
        if (!scheduler.isAlive()) {
            scheduler = new Scheduler(statsValues.level.getSpeed(), () -> movePiece(0, 1, !debugMode, false)); // go down
            scheduler.start();
        }
        isPaused = false;
    }

    public void updateLevel() {
        scheduler.setPause(statsValues.level.getSpeed());
    }

    public void stopGame() {
        scheduler.stopThread();
        timer.stopThread();
    }

    public void reset() {
        isGameOver = false;
        if (scheduler != null && scheduler.isAlive()) {
            scheduler.stopThread();
        }

        if (timer != null && timer.isAlive()) {
            timer.stopThread();
        }

        scheduler = new Scheduler(700, () -> movePiece(0, 1, !debugMode, false)); // go down
        timer = new Scheduler(1000, this::incrementSeconds);

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

            // debug mode configuration
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
        scheduler.start();
        timer.start();

        signalChange("grid");
        signalChange("nextPiece");
        signalChange("stats");
    }

    /**
     * Method to get the best move for the current piece
     *
     * @return the combinaison of placement to go to the best position
     */
    public int[] getBestMove() {
        Piece currentPiece = pieceManager.getCurrentPiece();
        Piece nextPiece = pieceManager.getNextPiece().getFirst();
        Piece holdPiece = pieceManager.getHoldPiece();

        int[][] originalShape = currentPiece.getShape();
        int[][] originalHoldPieceShape = holdPiece != null ? holdPiece.getShape() : null;
        int[][] originalNextPieceShape = nextPiece != null ? nextPiece.getShape() : null;

        long[] values = findBestMoveForPiece(originalShape, currentPiece);
        long bestScore = values[0];
        int bestX = (int) values[1];
        int bestRotation = (int) values[2];
        String whichPieceToUse = "current";

        values = findBestMoveForPiece(
                holdPiece != null ? originalHoldPieceShape : originalNextPieceShape,
                holdPiece != null ? holdPiece : nextPiece
        );
        if (values[0] > bestScore) {
            bestX = (int) values[1];
            bestRotation = (int) values[2];
            whichPieceToUse = "hold";
        }

        currentPiece.setShape(originalShape);
        if (holdPiece != null) {
            holdPiece.setShape(originalHoldPieceShape);
        } else {
            if (nextPiece != null) {
                nextPiece.setShape(originalNextPieceShape);
            }
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

    /**
     * Method to find the best move for a piece
     *
     * @param originalNextPieceShape the shape of the next piece
     * @param piece                  the piece to check
     * @return the best move for the piece
     */
    public long[] findBestMoveForPiece(int[][] originalNextPieceShape, Piece piece) {
        long bestScore = Integer.MIN_VALUE;
        long bestX = -1;
        long bestRotation = 0;

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
                    long score = aiUtils.evaluatePosition(maxY, rotatedShape, getTempGrid(x, maxY, rotatedShape));
                    if (score > bestScore) {
                        bestScore = score;
                        bestX = x;
                        bestRotation = rotation;
                    }
                }
            }
        }
        return new long[]{bestScore, bestX, bestRotation};
    }

    /**
     * Method to check if the position is valid for a shape
     *
     * @param shape the shape to check
     * @param x     the x position of the shape
     * @param y     the y position of the shape
     * @return true if the position is valid, false otherwise
     */
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

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof ObservableAction(int idGrid1, Action action)) {
            if (idGrid1 == idGrid || (action != Action.RESUME_GAME && isPaused())) {
                if (isGameOver()) {
                    switch (action) {
                        case CHANGE_IA_STATE, STOP_GAME, STOP_IA, RESET, PAUSE_GAME, RESUME_GAME:
                            action.execute(this);
                        default:
                            return;
                    }
                } else {
                    action.execute(this);
                }
            }
        }
    }
}
