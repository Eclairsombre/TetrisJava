package Tetris.Model.GridComponent;

/**
 * This class is used to create a thread that runs a runnable at a fixed interval
 */
public class Scheduler extends Thread {
    private final Runnable r;
    /// r the runnable to run
    private long pause;
    /// pause the time to wait between runs
    private boolean wait = false;
    /// wait if true, the thread will wait before running the runnable

    /**
     * Constructor for the Scheduler class
     *
     * @param pause the time to wait between runs
     * @param runnable the runnable to run
     */
    public Scheduler(long pause, Runnable runnable) {
        this.r = runnable;
        this.pause = pause;
    }

    /**
     * This method is called when the thread is started. It runs the runnable
     */
    @Override
    public void run() {
        while (true) {
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

    /**
     * Stops the thread
     */
    public void stopThread() {
        this.interrupt();
    }

    public void setPause(long pause) {
        this.pause = pause;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }
}
