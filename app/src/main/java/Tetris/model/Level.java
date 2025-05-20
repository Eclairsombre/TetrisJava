package Tetris.model;

public record Level(int level) {
    public long getSpeed() {
        return level < 25 ? 1000 / level : 1000 / 25;
    }
}
