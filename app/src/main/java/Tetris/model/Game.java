package Tetris.model;

import java.util.Observable;

import Tetris.controller.Ordonnanceur; // Import the Ordonnanceur class

public class Game extends Observable {
    private final Grid grid;
    private final int score;

    public Game() {
        this.grid = new Grid(10, 20);
        this.score = 0;
        start();
    }

    public void start() {
        new Ordonnanceur(1000, this).start();

    }

    public Grid getGrid() {
        return grid;
    }

    public void movePieceDown() {
        grid.descendrePiece();
    }

}
