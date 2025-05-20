package Tetris.model;

import Tetris.controller.Game;

import static Tetris.controller.Action.*;

public class AIInputStrategy {
    private boolean enabled = false;
    private Thread aiThread;
    private int[] plannedMoves = null;
    private int currentMoveIndex = 0;

    /**
     * Processes the input for the AI.
     *
     * @param game The game object representing the game state.
     */
    public void processInput(Game game) {
        if (!enabled || game.isPaused()) {
            return;
        }

        int[] currentPlannedMoves = this.plannedMoves;
        int index = this.currentMoveIndex;

        if (currentPlannedMoves == null || index >= currentPlannedMoves.length) {
            currentPlannedMoves = game.getBestMove();
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
            case 0 -> ROTATE_LEFT.execute(game);
            case 1 -> ROTATE_RIGHT.execute(game);
            case 2 -> MOVE_LEFT.execute(game);
            case 3 -> MOVE_RIGHT.execute(game);
            case 4 -> MOVE_DOWN.execute(game);
            case 5 -> RDROP.execute(game);
            case 6 -> HOLD.execute(game);
            default -> System.err.println("Error: Invalid move type " + moveType);
        }
    }

    /**
     * Enables the AI input strategy.
     *
     * @param grid The grid object representing the game state.
     */
    public void enable(Game game) {
        if (!enabled) {
            enabled = true;
            plannedMoves = null;
            currentMoveIndex = 0;

            aiThread = new Thread(() -> {
                while (enabled) {
                    processInput(game);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
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