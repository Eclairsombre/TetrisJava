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

        if (plannedMoves == null || currentMoveIndex >= plannedMoves.length) {
            plannedMoves = grid.getBestMove();
            currentMoveIndex = 0;

            if (plannedMoves == null || plannedMoves.length == 0) {
                return;
            }
        }

        int moveType = plannedMoves[currentMoveIndex++];

        switch (moveType) {
            case 0 -> grid.rotatePiece(true);
            case 1 -> grid.rotatePiece(false);
            case 2 -> grid.movePiece(-1, 0, false, false);
            case 3 -> grid.movePiece(1, 0, false, false);
            case 4 -> grid.movePiece(0, 1, true, false);
            case 5 -> grid.doRdrop(false);
            default -> {
            }
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
                        Thread.sleep(20);
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