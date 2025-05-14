package Tetris.controller;

public class Scheduler extends Thread {
    Runnable r;
    long pause;// Thread-safe scheduler
    boolean running = true;

    public Scheduler(long pause, Runnable runnable) {
        this.r = runnable;
        this.pause = pause;
    }

    @Override
    public void run() {
        while (running) {
            try {
                r.run();
                Thread.sleep(pause);

            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stopThread() {
        this.interrupt();
    }
}
