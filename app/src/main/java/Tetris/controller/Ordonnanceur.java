package Tetris.controller;

import java.util.concurrent.ScheduledExecutorService;

public class Ordonnanceur extends Thread {
    Runnable r;
    long pause;
    boolean running;
    private final ScheduledExecutorService scheduler; // Thread-safe scheduler

    public Ordonnanceur(long pause, Runnable runnable) {
        this.r = runnable;
        this.pause = pause;
        this.running = true;
        this.scheduler = java.util.concurrent.Executors.newScheduledThreadPool(1);
    }

    @Override
    public void run() {
        scheduler.scheduleAtFixedRate(r, 0, pause, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
}
