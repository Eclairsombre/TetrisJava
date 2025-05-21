package Tetris.Model.Ai;

import Tetris.Model.Piece.Piece;
import Tetris.Model.Piece.PieceManager;
import Tetris.Utils.PieceColor;
import static Tetris.Utils.StaticGridFunction.*;

import java.util.List;


/**
 * Utility class for AI calculations in Tetris.
 *
 * @param width
 * @param height
 */
public record AiUtils(int width, int height) {

    /**
     * Calculates the moves needed to reach the targetX from startX.
     *
     * @param startX    x-coordinate of the starting position
     * @param targetX   x-coordinate of the target position
     * @param rotations number of rotations to be performed
     * @return an array of moves
     */
    public int[] calculateMoves(int startX, int targetX, int rotations) {
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

    /**
     * Evaluates the position of the piece on the grid.
     *
     * @param previousMaxHeight The maximum height of the grid before placing the piece.
     * @param shape             The shape of the piece.
     * @param grid              The current state of the grid.
     * @return A score representing the quality of the position.
     */
    public long evaluatePosition(int previousMaxHeight, int[][] shape, PieceColor[][] grid) {
        long score = 0;

        long maxHeight = getMaxHeightAfterPlacement(previousMaxHeight, shape, grid);
        long completeLines = getCompleteLinesAfterPlacement(grid);
        long holes = getHoleAfterPlacement(grid);
        long bumpiness = getBumpinessAfterPlacement(grid);

        score += maxHeight * -730;
        score += completeLines * 608;
        score += holes * -717;
        score += bumpiness * -224;

        return score;
    }


    /**
     * Calculates the maximum height of the grid after placing the piece.
     *
     * @param previousMaxHeight The maximum height of the grid before placing the piece.
     * @param shape             The shape of the piece.
     * @param grid              The current state of the grid.
     * @return The maximum height of the grid after placing the piece.
     */
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

    /**
     * Calculates the number of complete lines after placing the piece.
     *
     * @param grid The current state of the grid.
     * @return The number of complete lines after placing the piece.
     */
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

    /**
     * Calculates the number of holes after placing the piece.
     *
     * @param grid The current state of the grid.
     * @return The number of holes after placing the piece.
     */
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

    /**
     * Calculates the bumpiness of the grid after placing the piece.
     *
     * @param grid The current state of the grid.
     * @return The bumpiness of the grid after placing the piece.
     */
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

    /**
     * Method to get the best move for the current piece
     *
     * @return the combinaison of placement to go to the best position
     */
    public int[] getBestMove(PieceManager pieceManager, PieceColor[][] grid) {
        Piece currentPiece = pieceManager.getCurrentPiece();
        Piece nextPiece = pieceManager.getNextPiece().getFirst();
        Piece holdPiece = pieceManager.getHoldPiece();

        int[][] originalShape = currentPiece.getShape();
        int[][] originalHoldPieceShape = holdPiece != null ? holdPiece.getShape() : null;
        int[][] originalNextPieceShape = nextPiece != null ? nextPiece.getShape() : null;

        long[] values = findBestMoveForPiece(originalShape, currentPiece, grid);
        long bestScore = values[0];
        int bestX = (int) values[1];
        int bestRotation = (int) values[2];
        String whichPieceToUse = "current";

        values = findBestMoveForPiece(
                holdPiece != null ? originalHoldPieceShape : originalNextPieceShape,
                holdPiece != null ? holdPiece : nextPiece,
                grid
        );
        if (values[0] > bestScore) {
            bestX = (int) values[1];
            bestRotation = (int) values[2];
            whichPieceToUse = "hold";
        }

        currentPiece.setShape(originalShape);
        if (holdPiece != null) {
            holdPiece.setShape(originalHoldPieceShape);
        } else {
            if (nextPiece != null) {
                nextPiece.setShape(originalNextPieceShape);
            }
        }

        switch (whichPieceToUse) {
            case "hold" -> {
                int[] moves = calculateMoves(currentPiece.getX(), bestX, bestRotation);
                int[] temp = new int[moves.length + 1];
                temp[0] = 6;
                System.arraycopy(moves, 0, temp, 1, moves.length);
                return temp;
            }
            case "current" -> {
                return calculateMoves(currentPiece.getX(), bestX, bestRotation);
            }
        }

        return new int[0]; // should never happen
    }


    /**
     * Method to find the best move for a piece
     *
     * @param originalNextPieceShape the shape of the next piece
     * @param piece                  the piece to check
     * @return the best move for the piece
     */
    public long[] findBestMoveForPiece(int[][] originalNextPieceShape, Piece piece, PieceColor[][] grid) {
        long bestScore = Integer.MIN_VALUE;
        long bestX = -1;
        long bestRotation = 0;

        for (int rotation = 0; rotation < 4; rotation++) {
            int[][] rotatedShape = originalNextPieceShape;
            if (rotation > 0) {
                for (int r = 0; r < rotation; r++) {
                    rotatedShape = piece.getRotatedPosition(false);
                }
            }

            for (int x = -1; x < width; x++) {
                if (isValidPositionForShape(grid, rotatedShape, x, piece.getY(), width, height)) {
                    int maxY = findMaxYInGrid(grid, rotatedShape, x, width, height);
                    long score = evaluatePosition(maxY, rotatedShape, getTempGrid(grid, x, maxY, rotatedShape, piece.getColor()));
                    if (score > bestScore) {
                        bestScore = score;
                        bestX = x;
                        bestRotation = rotation;
                    }
                }
            }
        }
        return new long[]{bestScore, bestX, bestRotation};
    }

    /**
     * Method to get a copy of the grid with the current piece placed at the given position.
     *
     * @return the grid
     */
    private PieceColor[][] getTempGrid(PieceColor[][] grid, int x, int y, int[][] shape, PieceColor color) {
        PieceColor[][] tempGrid = new PieceColor[height][width];
        for (int r = 0; r < height; r++) {
            System.arraycopy(grid[r], 0, tempGrid[r], 0, width);
        }

        for (int[] point : shape) {
            int testX = point[0] + x;
            int testY = point[1] + y;
            if (testX >= 0 && testX < width && testY >= 0 && testY < height) {
                tempGrid[testY][testX] = color;
            }
        }
        return tempGrid;
    }
}
