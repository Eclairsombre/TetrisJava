package Tetris.Model.TetrisInstanceComponent;

/**
 * Utility class to manage the level of the game.
 *
 * @param level the level of the game
 */
public record Level(int level) {
    public long getSpeed() {
        return level < 25 ? 1000 / level : 40;
    }
}
