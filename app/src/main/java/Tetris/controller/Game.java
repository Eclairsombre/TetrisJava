package Tetris.controller;

import Tetris.model.Grid;

import java.util.Observable;

@SuppressWarnings("deprecation")
public class Game extends Observable {
    private final Grid grid;
    private Scheduler scheduler, timer;
    private final Runnable runnable = () -> movePieceDown(true);

    public Game(Grid grid) {
        this.grid = grid;
        this.scheduler = new Scheduler(grid.getStatsValues().level.getSpeed(), () -> movePieceDown(false));
        this.timer = new Scheduler(1000, grid::incrementSeconds);
    }

    public void startGame() {
        this.scheduler.start();
        this.timer.start();
    }

    public boolean isPaused() {
        return grid.isPaused();
    }

    public void pauseGame() {
        if (scheduler.isAlive()) {
            scheduler.stopThread();
        } else {
            scheduler = new Scheduler(grid.getStatsValues().level.getSpeed(), runnable);
            scheduler.start();
        }
        grid.setPaused(!grid.isPaused());
    }

    public Grid getGrid() {
        return grid;
    }

    public void movePieceDown(boolean increment_score) {
        grid.movePiece(0, 1, true);
        if (increment_score) {
            grid.updateScore(1);
        }
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

    public void resetScheduler(long pause) {
        scheduler.stopThread();
        scheduler = new Scheduler(pause, runnable);
        scheduler.start();

        timer.stopThread();
        timer = new Scheduler(1000, grid::incrementSeconds);
        timer.start();
    }

    public void reset() {
        resetScheduler(700);
        grid.reset();
    }

    public void doRdrop() {
        grid.doRdrop();
    }

    public void updateLevel() {
        resetScheduler(grid.getStatsValues().level.getSpeed());
    }

    public void stopGame() {
        scheduler.stopThread();
        timer.stopThread();
        grid.setPaused(true);
    }
}
