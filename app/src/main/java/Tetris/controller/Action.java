package Tetris.controller;

public enum Action {
    MOVE_LEFT(Game::movePieceLeft),
    MOVE_RIGHT(Game::movePieceRight),
    MOVE_DOWN(game -> game.movePieceDown(true)),
    ROTATE_LEFT(Game::rotatePieceLeft),
    ROTATE_RIGHT(Game::rotatePieceRight),
    HOLD(Game::exchangeHoldAndCurrent),
    RDROP(Game::doRdrop);

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