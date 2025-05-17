package Tetris.model;

import Tetris.controller.Game;

public enum Action {
    MOVE_LEFT(game -> game.movePieceLeft()),
    MOVE_RIGHT(game -> game.movePieceRight()),
    MOVE_DOWN(game -> game.movePieceDown(true)),
    ROTATE_LEFT(game -> game.rotatePieceLeft()),
    ROTATE_RIGHT(game -> game.rotatePieceRight()),
    HOLD(game -> game.exchangeHoldAndCurrent()),
    RDROP(game -> game.doRdrop()),
    PAUSE(game -> game.pauseGame());

    private final GameAction action;

    Action(GameAction action) {
        this.action = action;
    }

    public void execute(Game game) {
        action.execute(game);
    }

    @FunctionalInterface
    private interface GameAction {
        void execute(Game game);
    }
}