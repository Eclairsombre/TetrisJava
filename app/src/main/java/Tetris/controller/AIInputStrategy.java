package Tetris.controller;

public class AIInputStrategy implements InputStrategy {
    private boolean enabled = false;
    private Thread aiThread;
    private int[] plannedMoves = null;
    private int currentMoveIndex = 0;

    @Override
    public void processInput(Game game) {
        if (!enabled || game.isPaused()) {
            return;
        }

        if (plannedMoves == null || currentMoveIndex >= plannedMoves.length) {
            plannedMoves = game.getBestMove();
            currentMoveIndex = 0;

            if (plannedMoves == null || plannedMoves.length == 0) {
                return;
            }
        }

        int moveType = plannedMoves[currentMoveIndex++];

        switch (moveType) {
            case 0 -> game.rotatePieceLeft();
            case 1 -> game.rotatePieceRight();
            case 2 -> game.movePieceLeft();
            case 3 -> game.movePieceRight();
            case 4 -> game.movePieceDown(true);
            case 5 -> game.doRdrop();
            default -> {
            }
        }
    }

    @Override
    public void enable() {
        if (!enabled) {
            enabled = true;
            plannedMoves = null;
            currentMoveIndex = 0;

            aiThread = new Thread(() -> {
                while (enabled) {
                    processInput(InputController.getCurrentGame());
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            aiThread.start();
        }
    }

    @Override
    public void disable() {
        if (enabled) {
            enabled = false;
            if (aiThread != null && aiThread.isAlive()) {
                aiThread.interrupt();
            }
        }
    }
}