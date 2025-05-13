package Tetris.controller;

import java.util.Observable;

import Tetris.model.Game;
import Tetris.model.Grid;
import Tetris.model.Piece.Piece;
import Tetris.vue.Vue;

public class Controller extends Observable {
    private final Grid model;
    private final Vue vue;
    private final Ordennanceur ord;
    private final Game game;

    public Controller(Grid model, Vue vue) {
        this.model = model;
        this.vue = vue;
        this.game = new Game();
        this.ord = new Ordennanceur(game);
        this.ord.start();
    }

    public Piece getNewPiece() {
        Piece new_piece = model.getNouvellePiece();
        if (new_piece == null) {
            System.out.println("Error: Unable to create a new piece.");
            return null;
        }
        return new_piece;
    }

    public void placeNewPiece() {
        Piece new_piece = getNewPiece();
        if (new_piece != null) {
            // Assuming the piece has a method to get its coordinates
            int[][] coordinates = new_piece.getCoordinates(4, 0);
            for (int[] coordinate : coordinates) {
                int x = coordinate[0];
                int y = coordinate[1];
                model.setCell(x, y, new_piece.getColor());
            }
        }
    }

    /*
     * public void start() {
     * model.addObserver(view);
     * model.startGame();
     * }
     * 
     * public void moveLeft() {
     * model.moveLeft();
     * }
     * 
     * public void moveRight() {
     * model.moveRight();
     * }
     * 
     * public void rotate() {
     * model.rotate();
     * }
     * 
     * public void drop() {
     * model.drop();
     * }
     */
}
