package Tetris.Utils;

import java.util.Arrays;

public class StaticGridFunction {
    /**
     * Method to find the maximum Y position for a shape
     *
     * @param shape the shape to check
     * @param x     the horizontal position in the grid
     * @return the maximum Y position for this shape
     */
    public static int findMaxYInGrid(PieceColor[][] grid, int[][] shape, int x, int width, int height) {
        int y = 0;
        while (y < height) {
            if (!isValidPositionForShape(grid, shape, x, y + 1, width, height)) {
                break;
            }
            y++;
        }
        return y;
    }

    /**
     * Method to check if the position is valid for a shape
     *
     * @param shape the shape to check
     * @param x     the x position of the shape
     * @param y     the y position of the shape
     * @return true if the position is valid, false otherwise
     */
    public static boolean isValidPositionForShape(PieceColor[][] grid, int[][] shape, int x, int y, int width, int height) {
        if (shape == null) {
            return false;
        }
        for (int[] pos : shape) {
            int x_next = pos[0] + x;
            int y_next = pos[1] + y;
            if (Arrays.stream(shape).anyMatch(c -> c[0] == x && c[1] == y)) { // case where the piece is already in the grid
                continue;
            }
            if (x_next < 0 || x_next >= width || y_next >= height || (y_next >= 0 && !(grid[y_next][x_next] == PieceColor.NONE))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to set the grid initialization in debug mode.
     */
    public static void setDebugGrid(PieceColor[][] grid, int debugPos) {
        // debug mode configuration
        for (int i = 18; i < 25; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = PieceColor.RED;
            }
        }
        switch (debugPos) {
            case 0, 3 -> {
                if (debugPos == 3) {
                    grid[16][2] = PieceColor.RED;
                    grid[16][6] = PieceColor.RED;
                }
                grid[17][1] = PieceColor.RED;
                grid[17][7] = PieceColor.RED;
                grid[18][2] = PieceColor.NONE;
                grid[18][6] = PieceColor.NONE;
                grid[19][1] = PieceColor.NONE;
                grid[19][2] = PieceColor.NONE;
                grid[19][3] = PieceColor.NONE;
                grid[19][5] = PieceColor.NONE;
                grid[19][6] = PieceColor.NONE;
                grid[19][7] = PieceColor.NONE;
                grid[20][2] = PieceColor.NONE;
                grid[20][6] = PieceColor.NONE;
            }
            case 1 -> {
                grid[18][3] = PieceColor.NONE;
                grid[18][4] = PieceColor.NONE;
                grid[19][2] = PieceColor.NONE;
                grid[19][3] = PieceColor.NONE;
                grid[19][4] = PieceColor.NONE;
                grid[20][4] = PieceColor.NONE;
                grid[21][4] = PieceColor.NONE;
            }
            case 2 -> {
                grid[18][3] = PieceColor.NONE;
                grid[18][4] = PieceColor.NONE;
                grid[19][2] = PieceColor.NONE;
                grid[19][3] = PieceColor.NONE;
                grid[19][4] = PieceColor.NONE;
                grid[20][2] = PieceColor.NONE;
                grid[21][2] = PieceColor.NONE;
                grid[21][3] = PieceColor.NONE;
                grid[22][2] = PieceColor.NONE;
                grid[22][3] = PieceColor.NONE;
                grid[22][4] = PieceColor.NONE;
                grid[23][4] = PieceColor.NONE;
                grid[23][5] = PieceColor.NONE;
                grid[24][4] = PieceColor.NONE;
            }
            case 4 -> {
                grid[18][0] = PieceColor.NONE;
                grid[18][1] = PieceColor.NONE;
                grid[19][0] = PieceColor.NONE;
                grid[19][1] = PieceColor.NONE;
                grid[19][2] = PieceColor.NONE;
                grid[20][0] = PieceColor.NONE;
                grid[20][1] = PieceColor.NONE;
                grid[21][0] = PieceColor.NONE;
            }
        }
    }
}
