package Tetris.controller;

public class Scheduler extends Thread {
    private final Runnable r;
    private long pause;
    private boolean isRunning = false;

    public Scheduler(long pause, Runnable runnable) {
        this.r = runnable;
        this.pause = pause;
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                r.run();
                Thread.sleep(pause);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stopThread() {
        isRunning = false;
    }

    public void setPause(long pause) {
        this.pause = pause;
    }
}
