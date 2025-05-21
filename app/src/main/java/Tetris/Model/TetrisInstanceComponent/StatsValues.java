package Tetris.Model.TetrisInstanceComponent;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Class to manage the score, level, and time of the game.
 * It also handles the display of the line clear message.
 */
public class StatsValues {
    private final Runnable callView;
    /// callView Runnable to call the view when needed
    public int score = 0;
    /// score Current score of the game
    public Level level = new Level(1);
    /// level Current level of the game
    public int lineDeleteCount = 0;
    /// lineDeleteCount Number of lines deleted
    public String lineClearDisplay = "";
    /// lineClearDisplay Message to display when lines are cleared
    public FileWriterAndReader fileWriterAndReader = new FileWriterAndReader(
            "app/src/main/resources/score.txt"
    );
    /// fileWriterAndReader File writer and reader to save the score
    private int seconds = 0;
    /// seconds Current time in seconds
    private Thread resetScoreSkillLabel;
    /// resetScoreSkillLabel Thread to reset the line clear display after a delay
    private int BtBCounter = 0;
    /// @param BtBCounter Counter for back-to-back lines cleared

    /**
     * Constructor for StatsValues.
     *
     * @param callView Runnable to call the view when needed
     */
    public StatsValues(Runnable callView) {
        this.callView = callView;
    }

    /**
     * Method to get the current time in the format HH:MM:SS.
     */
    public String getTime() {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    /**
     * Method to increment the time.
     */
    public void incrementSeconds() {
        seconds++;
    }

    /**
     * Method to reset the StatsValues Object.
     */
    public void reset() {
        score = 0;
        level = new Level(1);
        seconds = 0;
        lineDeleteCount = 0;
    }


    public void resetLineClearDisplayAfterDelay() {
        if (callView != null) {
            if (resetScoreSkillLabel != null && resetScoreSkillLabel.isAlive()) {
                resetScoreSkillLabel.interrupt();
            }
            resetScoreSkillLabel = new Thread(() -> {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    // Thread was interrupted, do nothing
                }
                lineClearDisplay = "";
                callView.run();
            });
            resetScoreSkillLabel.start();
        }
    }

    /**
     * Method to calculate the score.
     *
     * @param nbLinesCleared Number of lines cleared.
     * @param TSpin          Whether it was a T-Spin.
     * @param allClear       Whether it was an all clear.
     */
    public void calculateScore(int nbLinesCleared, boolean TSpin, boolean allClear) {
        int score = 0;
        String label = "";
        switch (nbLinesCleared) {
            case 1 -> {
                label = "Simple";
                score = 100;
                if (TSpin) {
                    label += " T-Spin";
                    score = 400;
                    BtBCounter += 1;
                } else {
                    BtBCounter = 0;
                }
                if (allClear) {
                    label += " All clear";
                    score = 800;
                }
            }
            case 2 -> {
                label = "Double";
                score = 300;
                if (TSpin) {
                    label += " T-Spin";
                    score = 1200;
                    BtBCounter += 1;
                } else {
                    BtBCounter = 0;
                }
                if (allClear) {
                    label += " All clear";
                    score = 1200;
                }
            }
            case 3 -> {
                label = "Triple";
                score = 500;
                if (TSpin) {
                    label += " T-Spin triple";
                    score = 1600;
                    BtBCounter += 1;
                } else {
                    BtBCounter = 0;
                }
                if (allClear) {
                    label += " All clear";
                    score = 1800;
                }
            }
            case 4 -> {
                label = "Tetris";
                if (allClear) {
                    label += " All clear";
                    score = 2000;
                } else {
                    score = 800;
                }
                BtBCounter += 1;
            }
        }

        if (BtBCounter >= 2) {
            score = (int) (score * 1.5);
            label += " BtB !";
        }

        lineClearDisplay = label;
        this.score += score;
    }

    /**
     * Method to save the score to a file.
     */
    public void saveScore() {
        String[] lines = fileWriterAndReader.readFromFile();
        String nouvelleLigne = "Level :" + (level.level()) + " , " + score + " , " + getTime();
        List<String> allScores = new ArrayList<>();

        for (String line : lines) {
            if (line != null && !line.trim().isEmpty() && line.split(" , ").length >= 2) {
                allScores.add(line);
            }
        }

        allScores.add(nouvelleLigne);
        allScores.sort((a, b) -> {
            try {
                String[] partsA = a.split(" , ");
                String[] partsB = b.split(" , ");

                if (partsA.length < 2 || partsB.length < 2) {
                    return 0;
                }

                int scoreA = Integer.parseInt(partsA[1].trim());
                int scoreB = Integer.parseInt(partsB[1].trim());
                return Integer.compare(scoreB, scoreA);
            } catch (NumberFormatException e) {
                return 0;
            }
        });

        fileWriterAndReader.writeToFile(allScores);
    }
}
