package Tetris.model;

import Tetris.model.Piece.PieceColor;

import java.util.List;

public class AiUtils {
    public final int width;
    public final int height;

    AiUtils(int width, int height) {
        this.width = width;
        this.height = height;
    }

    int[] calculateMoves(int startX, int targetX, int rotations) {
        List<Integer> moves = new java.util.ArrayList<>();
        for (int i = 0; i < rotations; i++) {
            moves.add(1);
        }

        int dx = targetX - startX;
        while (dx != 0) {
            if (dx < 0) {
                moves.add(2);
                dx++;
            } else {
                moves.add(3);
                dx--;
            }
        }

        moves.add(5);

        int[] result = new int[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            result[i] = moves.get(i);
        }

        return result;
    }

    long evaluatePosition(int previousMaxHeight, int[][] shape, PieceColor[][] grid) {
        long score = 0;

        long maxHeight = getMaxHeightAfterPlacement(previousMaxHeight, shape, grid);
        long completeLines = getCompleteLinesAfterPlacement(grid);
        long holes = getHoleAfterPlacement(grid);
        long bumpiness = getBumpinessAfterPlacement(grid);

        score += maxHeight * -730;
        score += completeLines * 608;
        score += holes * -817;
        score += bumpiness * -224;

        return score;
    }


    private int getMaxHeightAfterPlacement(int previousMaxHeight, int[][] shape, PieceColor[][] grid) {
        int maxHeight = 0;

        for (int[] point : shape) {
            int testY = point[1] + previousMaxHeight;
            maxHeight = Math.max(maxHeight, height - testY);
        }

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                if (grid[row][col] != PieceColor.NONE) {
                    int colHeight = height - row;
                    maxHeight = Math.max(maxHeight, colHeight);
                    break;
                }
            }
        }

        return maxHeight;
    }

    public int getCompleteLinesAfterPlacement(PieceColor[][] grid) {
        int completeLines = 0;

        for (int row = 0; row < height; row++) {
            boolean isComplete = true;
            for (int col = 0; col < width; col++) {
                if (grid[row][col] == PieceColor.NONE) {
                    isComplete = false;
                    break;
                }
            }
            if (isComplete) {
                completeLines++;
            }
        }

        return completeLines;
    }

    public int getHoleAfterPlacement(PieceColor[][] grid) {
        int holeCount = 0;

        for (int col = 0; col < width; col++) {
            boolean blockFound = false;
            for (int row = 0; row < height; row++) {
                if (grid[row][col] != PieceColor.NONE) {
                    blockFound = true;
                } else if (blockFound) {
                    holeCount++;
                }
            }
        }

        return holeCount;
    }

    public int getBumpinessAfterPlacement(PieceColor[][] grid) {
        int bumpiness = 0;

        int[] heights = new int[width];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                if (grid[row][col] != PieceColor.NONE) {
                    heights[col] = height - row;
                    break;
                }
            }
        }

        for (int i = 1; i < heights.length; i++) {
            bumpiness += Math.abs(heights[i] - heights[i - 1]);
        }

        return bumpiness;
    }
}
