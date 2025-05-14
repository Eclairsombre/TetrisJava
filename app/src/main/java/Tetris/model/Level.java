package Tetris.model;

public class Level {
    private int level;
    private long speed;
    public boolean isNextLevel = false;

    public Level(int level) {
        this.level = level;
        if (level < 6) {
            this.speed = 700 - (level * 100L);
        } else if (level < 15) {
            this.speed = 100 - ((level - 6) * 10);
        } else {
            this.speed = 10;
        }
    }

    public int getLevel() {
        return level;
    }

    public long getSpeed() {
        return speed;
    }

}
