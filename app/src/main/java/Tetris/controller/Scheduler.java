package Tetris.controller;

public class Scheduler extends Thread {
    private final Runnable r;
    private final long pause;

    public Scheduler(long pause, Runnable runnable) {
        this.r = runnable;
        this.pause = pause;
    }

    @Override
    public void run() {
        while (true) {
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
