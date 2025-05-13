package Tetris.controller;

import Tetris.model.Game;

public class Ordennanceur extends Thread {

    private final int DELAY = 1000; // Delay in milliseconds
    private final Game tetris;

    public Ordennanceur(Game tetris) {
        this.tetris = tetris;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(DELAY);
                tetris.movePieceDown();
            } catch (InterruptedException e) {
                // Gestion de l'interruption du thread
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
