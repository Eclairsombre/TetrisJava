package Tetris.controller;

public class Scheduler extends Thread {
    private final Runnable r;
    private long pause;
    private boolean isRunning = false;
    private boolean wait = false;

    public Scheduler(long pause, Runnable runnable) {
        this.r = runnable;
        this.pause = pause;
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                if (!wait) {
                    r.run();
                }
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

    public void setWait() {
        this.wait = !this.wait;
    }
}
