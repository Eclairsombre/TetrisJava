package Tetris.Utils;
import Tetris.Model.TetrisInstance;

/**
 * Enum representing the possible actions that can be performed on the Tetris grid.
 */
public enum Action {
    MOVE_LEFT(tetrisInstance -> tetrisInstance.movePiece(-1, 0, false, false)),
    MOVE_RIGHT(tetrisInstance -> tetrisInstance.movePiece(1, 0, false, false)),
    MOVE_DOWN(tetrisInstance -> tetrisInstance.movePiece(0, 1, true, true)),
    ROTATE_LEFT(tetrisInstance -> tetrisInstance.rotatePiece(true)),
    ROTATE_RIGHT(tetrisInstance -> tetrisInstance.rotatePiece(false)),
    HOLD(TetrisInstance::exchangeHoldAndCurrent),
    RDROP(tetrisInstance -> tetrisInstance.doRdrop(true)),
    CHANGE_IA_STATE(tetrisInstance -> tetrisInstance.setAiMode(!tetrisInstance.isAiMode())),
    STOP_IA(tetrisInstance -> tetrisInstance.setAiMode(false)),
    STOP_GAME(TetrisInstance::stopGame),
    PAUSE_GAME(TetrisInstance::pauseGame),
    RESUME_GAME(TetrisInstance::resumeGame),
    RESET(TetrisInstance::reset),
    CALL_STATS(tetrisInstance -> tetrisInstance.signalChange("initBestScores")); // Only used for the initialization of the game

    private final GameAction action;

    Action(GameAction action) {
        this.action = action;
    }

    public void execute(TetrisInstance tetrisInstance) {
        action.execute(tetrisInstance);
    }

    @FunctionalInterface
    private interface GameAction {
        void execute(TetrisInstance tetrisInstance);
    }
}