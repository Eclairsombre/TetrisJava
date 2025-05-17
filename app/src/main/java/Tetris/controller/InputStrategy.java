package Tetris.controller;

public interface InputStrategy {
    void processInput(Game game);

    void enable();

    void disable();
}