package Tetris.Model.Ai;

import Tetris.Model.Grid;
import Tetris.Model.Utils.Scheduler;

import static Tetris.Model.Utils.Action.*;

/**
 * AIInputStrategy is a class that manages the AI input strategy for a Tetris game.
 */
public class AIInputStrategy {
    private boolean enabled = false;
    /// @param enabled Indicates if the AI is enabled or not.
    private Thread aiThread;
    /// @param aiThread The thread that runs the AI input strategy.
    private int[] plannedMoves = null;
    /// @param plannedMoves The planned moves for the AI.
    private int currentMoveIndex = 0;
    /// @param currentMoveIndex The index of the current move in the planned moves.

    /**
     * Processes the input for the AI.
     *
     * @param grid The game object representing the game state.
     */
    public void processInput(Grid grid) {
        if (!enabled || grid.isPaused()) {
            return;
        }

        int[] currentPlannedMoves = this.plannedMoves;
        int index = this.currentMoveIndex;

        if (currentPlannedMoves == null || index >= currentPlannedMoves.length) {
            currentPlannedMoves = grid.getBestMove();
            index = 0;
            this.plannedMoves = currentPlannedMoves;
            this.currentMoveIndex = index;

            if (currentPlannedMoves == null || currentPlannedMoves.length == 0) {
                return;
            }
        }
        int moveType = currentPlannedMoves[index];
        this.currentMoveIndex = index + 1;

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

            aiThread = new Scheduler(5, () -> processInput(grid));
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