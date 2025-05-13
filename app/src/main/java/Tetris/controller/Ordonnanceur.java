package Tetris.controller;

public class Ordonnanceur extends Thread {
    Runnable r;
    long pause;

    public Ordonnanceur(long pause, Runnable runnable) {
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
                // Handle the exception if needed
                System.err.println("Thread interrupted: " + e.getMessage());
            } catch (Exception e) {
                // Handle any other exceptions that may occur
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
    }
}
