package Tetris.model;

public class Game {
    private final Grid grid;
    private final int score;

    public Game() {
        this.grid = new Grid(10, 20);
        this.score = 0;
    }

    public Grid getGrid() {
        return grid;
    }

    public void movePieceDown() {
        grid.descendrePiece();
    }

}
