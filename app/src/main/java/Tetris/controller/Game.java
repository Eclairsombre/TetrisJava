package Tetris.controller;

import Tetris.model.Grid;

import java.util.Observable;

@SuppressWarnings("deprecation")
public class Game extends Observable {
    private final Grid grid;
    private Scheduler scheduler, timer;

    public Game(Grid grid) {
        this.grid = grid;
        this.scheduler = new Scheduler(grid.getLevel().getSpeed(), () -> movePieceDown(false));
        this.timer =  new Scheduler(1000, grid::incrementSeconds);
    }

    public void startGame() {
        this.scheduler.start();
        this.timer.start();
    }

    public void pauseGame() {
        if (scheduler.isAlive() && timer.isAlive()) {
            scheduler.stopThread();
            timer.stopThread();
        } else {
            scheduler = new Scheduler(grid.getLevel().getSpeed(), () -> movePieceDown(false));
            timer = new Scheduler(1000, grid::incrementSeconds);
            scheduler.start();
            timer.start();
        }
    }
    public Grid getGrid() {
        return grid;
    }

    public void movePieceDown(boolean increment_score) {
        if (grid.isGameOver()) {
            System.out.println("Game Over");
            scheduler.stopThread();
        } else {
            if (grid.getLevel().isNextLevel) {
                resetScheduler(grid.getLevel().getSpeed());
                grid.getLevel().isNextLevel = false;
            }
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

    public void resetScheduler(long pause) {
        scheduler.stopThread();
        scheduler = new Scheduler(pause, () -> movePieceDown(false));
        scheduler.start();

        timer.stopThread();
        timer = new Scheduler(1000, grid::incrementSeconds);
        timer.start();
    }

    public void reset() {
        grid.reset();
        resetScheduler(700);
        setChanged();
        notifyObservers();
    }

    public void doRdrop() {
        grid.doRdrop();
    }
}
