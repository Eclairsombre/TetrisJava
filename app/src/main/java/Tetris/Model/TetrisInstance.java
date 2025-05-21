package Tetris.Model;

import Tetris.Utils.Action;
import Tetris.Utils.PieceColor;
import Tetris.Utils.ObservableMessage;
import Tetris.Utils.ObservableAction;
import Tetris.Model.Ai.AIInputStrategy;
import Tetris.Model.Piece.Piece;
import Tetris.Model.Piece.PieceManager;
import Tetris.Model.Piece.PieceTemplate.PieceT;
import Tetris.Model.TetrisInstanceComponent.*;

import java.util.*;

import static Tetris.Utils.StaticGridFunction.*;
import static java.lang.Thread.sleep;

/**
 * Class to manage the grid
 */
@SuppressWarnings("deprecation")
public class TetrisInstance extends Observable implements Observer {
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
    private final AIInputStrategy aiInputStrategy;
    /// aiInputStrategy the AI input strategy
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
    public TetrisInstance(int width, int height, boolean debugMode, int debugPos, int idGrid) {
        this.aiInputStrategy = new AIInputStrategy(width, height);
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
        reset();
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

    /**
     * Method to get the grid
     * @return a copy of the grid
     */
    public PieceColor[][] getGrid() {
        PieceColor[][] gridCopy = new PieceColor[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(grid[i], 0, gridCopy[i], 0, width);
        }
        return gridCopy;
    }

    public PieceManager getPieceManager() {
        return this.pieceManager;
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
     * Method to increment the seconds of the timer.
     */
    public void incrementSeconds() {
        if (!isPaused) {
            statsValues.incrementSeconds();
            signalChange("timer");
        }
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
        return Arrays.stream(grid).flatMap(Arrays::stream).noneMatch(i -> i != PieceColor.NONE);
    }

    /**
     * Method to color a line in white (for display purposes when clearing lines)
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
            statsValues.level = new Level(statsValues.level.level() + 1); // increase level
            scheduler.setPause(statsValues.level.getSpeed()); // update speed
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
        return findMaxYInGrid(grid, piece.getShape(), piece.getX(), width, height);
    }

    /**
     * Method to drop the piece to the bottom directly
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
        if (isValidPositionForShape(grid, currentPiece.getShape(), currentPiece.getX() + x_move, currentPiece.getY() + y_move, width, height)) {
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
            scheduler.setWait(true);
            fixPiece(currentPiece, currentPiece.getColor());
        }
    }

    /**
     * Method to fix the piece
     * When this method is called, we the piece can move while waiting for the thread to finish, and you can't call again the method
     * Once the thread is finished, we check if the piece must be fixed or not, and we really fix it if needed
     *
     * @param currentPiece the current piece
     * @param color        the color of the piece
     */
    public void fixPiece(Piece currentPiece, PieceColor color) {
        Thread fixPieceThread = new Thread(() -> {
            try {
                sleep(isAiMode() ? 20 : 300);
            } catch (InterruptedException e) {
                // The thread was interrupted, do nothing
                System.out.println("Error sleeping");
            }
            if (!isValidPositionForShape(grid, currentPiece.getShape(), currentPiece.getX(), currentPiece.getY() + 1, width, height)) { // we check if the piece must be fixed
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
            scheduler.setWait(false); // restart the scheduler
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
                if (!isValidPositionForShape(grid, new int[][]{{currentPiece.getX(), currentPiece.getY()}}, c[0], c[1], width, height)) {
                    count++;
                }
            }
            if (count >= 3) {
                TSpin = true; // We can take into consideration mini T-Spin by checking if the 2 upper corners are filled
            }
        }
    }

    /**
     * Method to rotate the piece. Handle the T-Spin case
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
        if (!isValidPositionForShape(grid, upperCorner, currentPiece.getX(), currentPiece.getY(), width, height)) {
            tryRotate(kicks, currentPiece, originalRotatedShape);
        }
    }

    /**
     * Method to try to rotate the piece
     *
     * @param kicks                the kicks to try
     * @param currentPiece         the current piece
     * @param originalRotatedShape the original rotated shape
     * @return true if the rotation is made, false otherwise
     */
    private boolean tryRotate(int[][] kicks, Piece currentPiece, int[][] originalRotatedShape) {
        for (int[] kick : kicks) {
            if (isValidPositionForShape(grid, originalRotatedShape, currentPiece.getX() + kick[0], currentPiece.getY() + kick[1], width, height)) {
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

            setDebugGrid(grid, debugPos);
        }
        scheduler.start();
        timer.start();

        signalChange("grid");
        signalChange("nextPiece");
        signalChange("stats");
    }

    /**
     * Method to signal a change in the grid.
     *
     * @param type which change appended
     */
    public void signalChange(String type) {
        setChanged();
        switch (type) {
            case "timer", "stats", "initBestScores", "gameOver" ->
                    notifyObservers(ObservableMessage.of(type, null, statsValues, null, 0, false));
            case "grid" ->
                    notifyObservers(ObservableMessage.of(type, grid, null, pieceManager, findMaxY(pieceManager.getCurrentPiece()), false));
            case "nextPiece" -> notifyObservers(ObservableMessage.of(type, null, null, pieceManager, 0, false));
            case "AILabel" -> notifyObservers(ObservableMessage.of(type, null, null, null, 0, isAiMode()));
            default -> System.out.println("Error: unknown type of signal");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof ObservableAction(int idGrid1, Action action)) {
            if (idGrid1 == idGrid || (action != Action.RESUME_GAME && isPaused())) {
                if (isGameOver) {
                    switch (action) {
                        case CHANGE_IA_STATE, STOP_GAME, STOP_IA, RESET, PAUSE_GAME, RESUME_GAME, CALL_STATS:
                            action.execute(this);
                    }
                } else {
                    action.execute(this);
                }
            }
        }
    }
}
