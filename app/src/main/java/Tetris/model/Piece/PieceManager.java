package Tetris.model.Piece;

import java.util.List;

public class PieceManager {
    private Piece currentPiece;
    private List<Piece> nextPiece;
    private Piece holdPiece;

    public PieceManager() {
        this.currentPiece = null;
        this.nextPiece = null;
        this.holdPiece = null;
    }

    public Piece getCurrentPiece() {
        return currentPiece;
    }

    public void setCurrentPiece(Piece currentPiece) {
        this.currentPiece = currentPiece;
    }

    public List<Piece> getNextPiece() {
        return nextPiece;
    }

    public void setNextPiece(List<Piece> nextPiece) {
        this.nextPiece = nextPiece;
    }

    public Piece getHoldPiece() {
        return holdPiece;
    }

    public void setHoldPiece(Piece holdPiece) {
        this.holdPiece = holdPiece;
    }

}
