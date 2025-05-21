package Tetris.Model.Piece;

import java.awt.*;

/**
 * Enum representing the colors of the pieces in Tetris.
 * Each color corresponds to a specific piece type.
 */
public enum PieceColor {
    RED,
    ORANGE,
    BLUE,
    YELLOW,
    GREEN,
    CYAN,
    PINK,
    WHITE,
    NONE;

    public static Color getColorCell(PieceColor color) {
        return switch (color) {
            case PieceColor.RED -> new Color(0xFF0000);
            case PieceColor.GREEN -> new Color(0x00FF00);
            case PieceColor.BLUE -> new Color(0x0000FF);
            case PieceColor.YELLOW -> new Color(0xFFFF00);
            case PieceColor.PINK -> new Color(0x800080);
            case PieceColor.ORANGE -> new Color(0xFF7F00);
            case PieceColor.CYAN -> new Color(0x00FFFF);
            case PieceColor.WHITE -> Color.WHITE;
            default -> Color.BLACK;
        };
    }
}