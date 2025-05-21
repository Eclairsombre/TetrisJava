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
    RDROP(grid -> grid.doRdrop(true)),
    CHANGE_IA_STATE(grid -> grid.setAiMode(!grid.isAiMode())),
    STOP_IA(grid -> grid.setAiMode(false)),
    STOP_GAME(Grid::stopGame),
    PAUSE_GAME(Grid::pauseGame),
    RESUME_GAME(Grid::resumeGame),
    RESET(Grid::reset);

    private final GameAction action;

    Action(GameAction action) {
        this.action = action;
    }

    public void execute(Grid grid) {
        action.execute(grid);
    }

    @FunctionalInterface
    private interface GameAction {
        void execute(Grid grid);
    }
}