package Tetris.model;

public class AIInputStrategy {
    private boolean enabled = false;
    private Thread aiThread;
    private int[] plannedMoves = null;
    private int currentMoveIndex = 0;

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
            case 0 -> grid.rotatePiece(true);
            case 1 -> grid.rotatePiece(false);
            case 2 -> grid.movePiece(-1, 0, false, false);
            case 3 -> grid.movePiece(1, 0, false, false);
            case 4 -> grid.movePiece(0, 1, true, false);
            case 5 -> grid.doRdrop(false);
            case 6 -> grid.exchangeHoldAndCurrent();
            default -> System.err.println("Error: Invalid move type " + moveType);
        }
    }

    public void enable(Grid grid) {
        if (!enabled) {
            enabled = true;
            plannedMoves = null;
            currentMoveIndex = 0;

            aiThread = new Thread(() -> {
                while (enabled) {
                    processInput(grid);
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

    public void disable() {
        if (enabled) {
            enabled = false;
            if (aiThread != null && aiThread.isAlive()) {
                aiThread.interrupt();
            }
        }
    }
}