package Tetris.Model.Piece;

import Tetris.Model.Piece.PieceTemplate.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PieceManager {
    private final List<Piece> nextPiece = new ArrayList<>();
    private final SecureRandom random = new java.security.SecureRandom();
    private List<Integer> lastPiece = new ArrayList<>(List.of(-1, -1, -1)); // impossible piece index to start
    private boolean canHoldPiece = true;
    private Piece currentPiece;
    private Piece holdPiece;

    public PieceManager(boolean debugMode) {
        reset(debugMode);
    }

    public Piece getCurrentPiece() {
        return currentPiece;
    }

    public List<Piece> getNextPiece() {
        return nextPiece;
    }

    public Piece getHoldPiece() {
        return holdPiece;
    }

    private Piece initializePiece() {
        Piece p = getNouvellePiece();
        p.setPos(3, 0);
        return p;
    }

    /**
     * Method to exchange the hold piece with the current piece.
     *
     * @return true if the exchange was successful, false if the hold piece is not available.
     */
    public boolean exchangeHoldAndCurrent() {
        if (!canHoldPiece) {
            return false;
        }
        Piece previousPiece = currentPiece;
        previousPiece.setPos(3, 1);
        if (holdPiece == null) {
            changePiece();
        } else {
            currentPiece = holdPiece;
        }
        holdPiece = previousPiece;
        this.canHoldPiece = false;
        return true;
    }

    /**
     * Method to get a new piece.
     *
     * @return a new piece.
     */
    public Piece getNouvellePiece() {
        // make a semi-random choice of a piece to avoid too many duplicates
        int idx = random.nextInt(7);
        for (int i = 0; i < lastPiece.size(); i++) {
            if (!lastPiece.contains(idx)) {
                break;
            }
            idx = random.nextInt(7); // re-roll if the piece is already in the last 3
        } // if the piece is in the last 3 again, we select it anyway
        lastPiece.removeFirst();
        lastPiece.add(idx);
        try {
            return switch (idx) {
                case 0 -> new PieceI(PieceColor.RED);
                case 1 -> new PieceJ(PieceColor.ORANGE);
                case 2 -> new PieceL(PieceColor.BLUE);
                case 3 -> new PieceO(PieceColor.YELLOW);
                case 4 -> new PieceS(PieceColor.GREEN);
                case 5 -> new PieceT(PieceColor.CYAN);
                case 6 -> new PieceZ(PieceColor.PINK);
                default -> throw new IllegalArgumentException("Invalid piece index: " + idx);
            };
        } catch (Exception e) {
            System.err.println("Error creating piece: " + e.getMessage());
            return null;
        }
    }

    /**
     * Method to change the current piece.
     */
    public void changePiece() {
        currentPiece = nextPiece.removeFirst();
        nextPiece.addLast(initializePiece());
        canHoldPiece = true;
    }

    /**
     * Method to reset the piece manager.
     *
     * @param debugMode if true, the current piece is set to a specific piece for debugging.
     */
    public void reset(boolean debugMode) {
        if (debugMode) {
            currentPiece = new PieceT(PieceColor.CYAN);
            currentPiece.setPos(3, 0);
        } else {
            currentPiece = initializePiece();
        }
        holdPiece = null;
        lastPiece = new ArrayList<>(List.of(-1, -1, -1));
        nextPiece.clear();
        for (int i = 0; i < 3; i++) {
            nextPiece.add(initializePiece());
        }
    }
}
