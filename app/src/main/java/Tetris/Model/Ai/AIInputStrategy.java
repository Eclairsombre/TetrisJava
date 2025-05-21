package Tetris.Model.Ai;

import Tetris.Model.Grid;
import Tetris.Model.Piece.Piece;
import Tetris.Model.Utils.Scheduler;

import static Tetris.Model.Utils.Action.*;

/**
 * AIInputStrategy is a class that manages the AI input strategy for a Tetris game.
 */
public class AIInputStrategy {
    /// @param lastPieceY Y position of the last piece for which moves were calculated
    private static final int MAX_MOVES_BEFORE_RECALC = 3;
    private boolean enabled = false;
    /// @param enabled Indicates if the AI is enabled or not.
    private Thread aiThread;
    /// @param aiThread The thread that runs the AI input strategy.
    private int[] plannedMoves = null;
    /// @param plannedMoves The planned moves for the AI.
    private int currentMoveIndex = 0;
    /// @param currentMoveIndex The index of the current move in the planned moves.
    private int lastPieceId = -1;
    /// @param lastPieceId ID of the last piece for which moves were calculated
    private int lastPieceX = -1;
    /// @param lastPieceX X position of the last piece for which moves were calculated
    private int lastPieceY = -1;
    /// @param MAX_MOVES_BEFORE_RECALC Maximum number of moves before recalculating the best move

    /**
     * Processes the input for the AI.
     *
     * @param grid The game object representing the game state.
     */
    public void processInput(Grid grid) {
        if (!enabled || grid.isPaused()) {
            return;
        }

        // Get current piece information to check if we need to recalculate
        Piece currentPiece = grid.getPieceManager().getCurrentPiece();
        int currentPieceId = System.identityHashCode(currentPiece);
        int currentPieceX = currentPiece.getX();
        int currentPieceY = currentPiece.getY();

        //To avoid desyncronization between the AI and the game, we check if the piece has moved
        boolean shouldRecalculate =
                plannedMoves == null ||
                        currentMoveIndex >= plannedMoves.length ||
                        currentPieceId != lastPieceId ||
                        Math.abs(currentPieceX - lastPieceX) > 1 ||
                        Math.abs(currentPieceY - lastPieceY) > 1 ||
                        currentMoveIndex >= MAX_MOVES_BEFORE_RECALC;

        if (shouldRecalculate) {
            plannedMoves = grid.getBestMove();
            currentMoveIndex = 0;

            // Update tracking variables
            lastPieceId = currentPieceId;
            lastPieceX = currentPieceX;
            lastPieceY = currentPieceY;

            if (plannedMoves == null || plannedMoves.length == 0) {
                return;
            }
        }

        int moveType = plannedMoves[currentMoveIndex];
        currentMoveIndex++;

        switch (moveType) {
            case 0 -> ROTATE_LEFT.execute(grid);
            case 1 -> ROTATE_RIGHT.execute(grid);
            case 2 -> MOVE_LEFT.execute(grid);
            case 3 -> MOVE_RIGHT.execute(grid);
            case 4 -> MOVE_DOWN.execute(grid);
            case 5 -> RDROP.execute(grid);
            case 6 -> HOLD.execute(grid);
            default -> System.err.println("Error: Invalid move type " + moveType);
        }
    }

    /**
     * Enables the AI input strategy.
     *
     * @param grid The grid object representing the grid state.
     */
    public void enable(Grid grid) {
        if (!enabled) {
            enabled = true;
            plannedMoves = null;
            currentMoveIndex = 0;
            lastPieceId = -1;
            lastPieceX = -1;
            lastPieceY = -1;

            aiThread = new Scheduler(100, () -> processInput(grid));
            aiThread.start();
        }
    }

    /**
     * Disables the AI input strategy.
     */
    public void disable() {
        if (enabled) {
            enabled = false;
            if (aiThread != null && aiThread.isAlive()) {
                aiThread.interrupt();
            }
        }
    }
}