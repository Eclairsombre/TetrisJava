package Tetris.controller;

import java.util.Observable;

import Tetris.model.Grid;

@SuppressWarnings("deprecation")
public class Game extends Observable {
    private final Grid grid;
    private final Scheduler scheduler = new Scheduler(700, () -> movePieceDown(false));

    public Game(Grid grid) {
        this.grid = grid;
        this.scheduler.start();
    }

    public Grid getGrid() {
        return grid;
    }

    public void movePieceDown(boolean increment_score) {
        if (grid.isGameOver()) {
            System.out.println("Game Over");
            scheduler.stopThread();
        } else {
            grid.movePiece(0, 1, true);
            if (increment_score) {
                grid.updateScore(1);
            }
        }
    }

    public void movePieceLeft() {
        if (!grid.isGameOver()) {
            grid.movePiece(-1, 0, false);
        }
    }

    public void movePieceRight() {
        if (!grid.isGameOver()) {
            grid.movePiece(1, 0, false);
        }
    }

    public void rotatePieceLeft() {
        if (!grid.isGameOver()) {
            grid.rotatePiece(true);
        }
    }

    public void rotatePieceRight() {
        if (!grid.isGameOver()) {
            grid.rotatePiece(false);
        }
    }
}
