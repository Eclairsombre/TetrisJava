package Tetris.model;

import java.util.Observable;

import Tetris.controller.Ordonnanceur; // Import the Ordonnanceur class

public class Game extends Observable {
    private final Grid grid;
    private final int score;

    public Game(Grid grid) {
        this.grid = grid;
        this.score = 0;
        new Ordonnanceur(1000, this::movePieceDown).start();
    }

    public Grid getGrid() {
        return grid;
    }

    public void movePieceDown() {
        grid.descendrePiece();
    }

}
