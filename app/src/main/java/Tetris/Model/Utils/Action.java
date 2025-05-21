package Tetris.Model.Utils;

import Tetris.Model.Grid;

/**
 * Enum representing the possible actions that can be performed on the Tetris grid.
 */
public enum Action {
    MOVE_LEFT(grid -> grid.movePiece(-1, 0, false, false)),
    MOVE_RIGHT(grid -> grid.movePiece(1, 0, false, false)),
    MOVE_DOWN(grid -> grid.movePiece(0, 1, true, true)),
    ROTATE_LEFT(grid -> grid.rotatePiece(true)),
    ROTATE_RIGHT(grid -> grid.rotatePiece(false)),
    HOLD(Grid::exchangeHoldAndCurrent),
    RDROP(grid -> grid.doRdrop(true));

    private final GameAction action;

    Action(GameAction action) {
        this.action = action;
    }

    public void execute(Grid grid) {
        if (grid.isPaused() || grid.isGameOver()) {
            return;
        }
        action.execute(grid);
    }

    @FunctionalInterface
    private interface GameAction {
        void execute(Grid grid);
    }
}