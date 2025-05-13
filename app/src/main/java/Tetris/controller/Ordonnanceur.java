package Tetris.controller;

import Tetris.model.Game;

public class Ordonnanceur extends Thread {
    Runnable r;
    long pause;

    public Ordonnanceur(long pause, Game tetris) {
        this.r = new Runnable() {
            @Override
            public void run() {
                tetris.movePieceDown();
            }
        };
        this.pause = pause;
    }

    @Override
    public void run() {
        while (true) {
            try {
                r.run();
                Thread.sleep(pause);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
