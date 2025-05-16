package Tetris.controller;

import Tetris.model.FileWriterAndReader;
import Tetris.model.Grid;
import Tetris.model.Piece.Piece;
import Tetris.model.Piece.PieceColor;
import Tetris.model.Piece.PieceManager;
import Tetris.model.StatsValues;

import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class Game extends Observable {
    private final Grid grid;
    private final Runnable runnable = () -> movePieceDown(false);
    private Scheduler scheduler, timer;

    public Game() {
        this.grid = new Grid(10, 25);
        reset();
    }

    public void reset() {
        if (scheduler != null && scheduler.isAlive()) {
            scheduler.stopThread();
        }

        if (timer != null && timer.isAlive()) {
            timer.stopThread();
        }

        scheduler = new Scheduler(700, () -> movePieceDown(false));
        timer = new Scheduler(1000, grid::incrementSeconds);

        grid.reset();

        scheduler.start();
        timer.start();
    }

    public boolean isPaused() {
        return grid.isPaused();
    }

    public void pauseGame() {
        if (scheduler.isAlive()) {
            scheduler.stopThread();
        }
        grid.setPaused(true);
    }

    public void resumeGame() {
        if (!scheduler.isAlive()) {
            scheduler = new Scheduler(grid.getStatsValues().level.getSpeed(), runnable);
            scheduler.start();
        }
        grid.setPaused(false);
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

    public void doRdrop() {
        grid.doRdrop();
    }

    public void updateLevel() {
        scheduler.setPause(grid.getStatsValues().level.getSpeed());
    }

    public void stopGame() {
        scheduler.stopThread();
        timer.stopThread();
    }

    public int[] getLengthGrid() {
        return new int[]{grid.getWidth(), grid.getHeight()};
    }

    public FileWriterAndReader getFileWriterAndReader() {
        return grid.getFileWriterAndReader();
    }

    public PieceColor getGridCell(int x, int y) {
        return grid.getCell(x, y);
    }

    public void exchangeHoldAndCurrent() {
        grid.exchangeHoldAndCurrent();
    }

    public StatsValues getStatsValues() {
        return grid.getStatsValues();
    }

    public PieceManager getPieceManager() {
        return grid.getPieceManager();
    }

    public int getMaxHeightEmpty(Piece piece) {
        return grid.getMaxHeightEmpty(piece);
    }

    public void addGridObserver(Observer obj) {
        this.grid.addObserver(obj);
    }
}
