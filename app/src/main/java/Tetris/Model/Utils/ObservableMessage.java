package Tetris.Model.Utils;

import Tetris.Model.Piece.PieceColor;
import Tetris.Model.Piece.PieceManager;

public record ObservableMessage(String message, PieceColor[][] grid, StatsValues statsValues, PieceManager pieceManager, int maxRDropY) {
    /// @param message The message to display
    /// @param value The value to display
    /// @return A new ObservableMessage object with the given message and value
    public static ObservableMessage of(String message, PieceColor[][] value, StatsValues statsValues, PieceManager pieceManager, Integer maxRDropY) {
        return new ObservableMessage(message, value, statsValues, pieceManager, maxRDropY);
    }
}
