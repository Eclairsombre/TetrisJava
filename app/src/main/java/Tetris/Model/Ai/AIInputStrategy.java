package Tetris.Model.Ai;

import Tetris.Model.TetrisInstance;
import Tetris.Model.Piece.Piece;
import Tetris.Model.GridComponent.Scheduler;

import java.util.Arrays;

import static Tetris.Utils.Action.*;

/**
 * AIInputStrategy is a class that manages the AI input strategy for a Tetris game.
 */
public class AIInputStrategy {
    /// lastPieceY Y position of the last piece for which moves were calculated
    private static final int MAX_MOVES_BEFORE_RECALC = 3;
    private boolean enabled = false;
    /// enabled Indicates if the AI is enabled or not.
    private Thread aiThread;
    /// aiThread The thread that runs the AI input strategy.
    private int[] plannedMoves = null;
    /// plannedMoves The planned moves for the AI.
    private int currentMoveIndex = 0;
    /// currentMoveIndex The index of the current move in the planned moves.
    private int lastPieceId = -1;
    /// lastPieceId ID of the last piece for which moves were calculated
    private int lastPieceX = -1;
    /// lastPieceX X position of the last piece for which moves were calculated
    private int lastPieceY = -1;
    /// lastPieceY Y position of the last piece for which moves were calculated
    private final AiUtils aiUtils;


    public AIInputStrategy(int width, int height) {
        // Initialize the AIUtils with the grid dimensions
        aiUtils = new AiUtils(width, height);
    }
    /**
     * Processes the input for the AI.
     *
     * @param tetrisInstance The game object representing the game state.
     */
    public void processInput(TetrisInstance tetrisInstance) {
        if (!enabled || tetrisInstance.isPaused()) {
            return;
        }

        // Get current piece information to check if we need to recalculate
        Piece currentPiece = tetrisInstance.getPieceManager().getCurrentPiece();
        int currentPieceId = System.identityHashCode(currentPiece);
        int currentPieceX = currentPiece.getX();
        int currentPieceY = currentPiece.getY();

        //To avoid desynchronization between the AI and the game, we check if the piece has moved
        boolean shouldRecalculate =
                plannedMoves == null ||
                        currentMoveIndex >= plannedMoves.length ||
                        currentPieceId != lastPieceId ||
                        Math.abs(currentPieceX - lastPieceX) > 1 ||
                        Math.abs(currentPieceY - lastPieceY) > 1 ||
                        currentMoveIndex >= MAX_MOVES_BEFORE_RECALC;

        if (shouldRecalculate) {
            plannedMoves = aiUtils.getBestMove(tetrisInstance.getPieceManager(), tetrisInstance.getGrid());
            currentMoveIndex = 0;

            // Update tracking variables
            lastPieceId = currentPieceId;
            lastPieceX = currentPieceX;
            lastPieceY = currentPieceY;

            if (Arrays.equals(plannedMoves, new int[0]) || plannedMoves.length == 0) {
                return;
            }
        }

        int moveType = plannedMoves[currentMoveIndex];
        currentMoveIndex++;

        switch (moveType) {
            case 0 -> ROTATE_LEFT.execute(tetrisInstance);
            case 1 -> ROTATE_RIGHT.execute(tetrisInstance);
            case 2 -> MOVE_LEFT.execute(tetrisInstance);
            case 3 -> MOVE_RIGHT.execute(tetrisInstance);
            case 4 -> MOVE_DOWN.execute(tetrisInstance);
            case 5 -> RDROP.execute(tetrisInstance);
            case 6 -> HOLD.execute(tetrisInstance);
            default -> System.err.println("Error: Invalid move type " + moveType);
        }
    }

    /**
     * Enables the AI input strategy.
     *
     * @param tetrisInstance The grid object representing the grid state.
     */
    public void enable(TetrisInstance tetrisInstance) {
        if (!enabled) {
            enabled = true;
            plannedMoves = null;
            currentMoveIndex = 0;
            lastPieceId = -1;
            lastPieceX = -1;
            lastPieceY = -1;

            aiThread = new Scheduler(5, () -> processInput(tetrisInstance));
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