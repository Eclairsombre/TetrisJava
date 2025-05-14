package Tetris.model;

public class Level {
    private int level;
    private long speed;
    public boolean isNextLevel = false;

    public Level(int level) {
        this.level = level;
        this.speed = 1000 - (level * 100);
        if (this.speed < 100) {
            this.speed = 100;
        }
    }

    public int getLevel() {
        return level;
    }

    public long getSpeed() {
        return speed;
    }

}
