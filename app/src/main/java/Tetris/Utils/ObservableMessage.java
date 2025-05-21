package Tetris.Utils;

import Tetris.Model.Piece.PieceManager;
import Tetris.Model.TetrisInstanceComponent.StatsValues;

public record ObservableMessage(String message, PieceColor[][] grid, StatsValues statsValues, PieceManager pieceManager, int maxRDropY, boolean isAiMode) {
    /// @param message The message to display
    /// @param value The value to display
    /// @return A new ObservableMessage object with the given message and value
    public static ObservableMessage of(String message, PieceColor[][] value, StatsValues statsValues, PieceManager pieceManager, Integer maxRDropY, boolean isAiMode) {
        return new ObservableMessage(message, value, statsValues, pieceManager, maxRDropY, isAiMode);
    }
}
