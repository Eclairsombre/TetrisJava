package Tetris.controller;

import Tetris.model.AIInputStrategy;
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
    private final boolean debugMode;
    private final Grid grid;
    private final Runnable runnable = () -> movePieceDown(false);
    private Scheduler scheduler, timer;
    private boolean aiMode = false;
    private final AIInputStrategy aiInputStrategy = new AIInputStrategy();

    public Game(boolean debugMode, int debugPos) {
        this.debugMode = debugMode;
        this.grid = new Grid(10, 25, this.debugMode, debugPos);
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

        grid.reset(debugMode);

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
        grid.movePiece(0, 1, !debugMode, increment_score);
    }

    public void movePieceLeft() {
        grid.movePiece(-1, 0, false, false);
    }

    public void movePieceRight() {
        grid.movePiece(1, 0, false, false);
    }

    public void rotatePieceLeft() {
        grid.rotatePiece(true);
    }

    public void rotatePieceRight() {
        grid.rotatePiece(false);
    }

    public void doRdrop() {
        grid.doRdrop(true);
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

    public int findMaxY(Piece piece) {
        return grid.findMaxY(piece.getShape(), piece.getX());
    }

    public void addGridObserver(Observer obj) {
        this.grid.addObserver(obj);
    }

    public void fixPiece() {
        scheduler.setWait();
    }

    public boolean isAiMode() {
        return aiMode;
    }

    public void setAiMode(boolean isAIMod) {
        this.aiMode = isAIMod;
        if (isAIMod) {
            aiInputStrategy.enable(grid);
        } else {
            aiInputStrategy.disable();
        }
    }
}
