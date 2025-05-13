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
        grid.movePiece(0, 1, true);
    }

    public void movePieceLeft() {
        grid.movePiece(-1, 0, false);
    }

    public void movePieceRight() {
        grid.movePiece(1, 0, false);
    }

    public void rotatePieceLeft() {
        grid.rotatePiece(true);
    }

    public void rotatePieceRight() {
        grid.rotatePiece(false);
    }
}
