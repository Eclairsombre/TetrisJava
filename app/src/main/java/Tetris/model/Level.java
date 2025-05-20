package Tetris.model;

public class Level {
    private final int level;

    public Level(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public long getSpeed() {
        return 1000 / level;
    }
}
