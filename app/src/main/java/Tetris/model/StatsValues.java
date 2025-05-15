package Tetris.model;

public class StatsValues { // simple data storage class
    public int score = 0;
    public Level level = new Level(0);
    private int seconds = 0;
    public int lineDeleteCount = 0;

    public String getTime() {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    public void incrementSeconds() {
        seconds++;
    }

    public void reset() {
        score = 0;
        level = new Level(0);
        seconds = 0;
        lineDeleteCount = 0;
    }
}
