package Tetris.controller;

import java.util.Observable;

import Tetris.model.Grid;

public class Game extends Observable {
    private final Grid grid;
    private final int score;

    public Game(Grid grid) {
        this.grid = grid;
        this.score = 0;
        new Ordonnanceur(700, this::movePieceDown).start();
    }

    public Grid getGrid() {
        return grid;
    }

    public void movePieceDown() {
        grid.descendrePiece();
    }

}
