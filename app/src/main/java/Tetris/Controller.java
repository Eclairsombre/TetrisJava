package Tetris;

import Tetris.model.Grid;

import javax.swing.text.View;

public class Controller {
    private final Grid model;
    private final View view;

    public Controller(Grid model, View view) {
        this.model = model;
        this.view = view;
    }

    /*
    public void start() {
        model.addObserver(view);
        model.startGame();
    }

    public void moveLeft() {
        model.moveLeft();
    }

    public void moveRight() {
        model.moveRight();
    }

    public void rotate() {
        model.rotate();
    }

    public void drop() {
        model.drop();
    }
     */
}
