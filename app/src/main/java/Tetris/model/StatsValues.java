package Tetris.model;

import static java.lang.Thread.sleep;

public class StatsValues {
    public int score = 0;
    public Level level = new Level(0);
    private int seconds = 0;
    public int lineDeleteCount = 0;
    public String lineClearDisplay = "";
    private final Runnable callView;
    private Thread resetScoreSkillLabel;
    private int BtBCounter = 0;

    StatsValues(Runnable callView) {
        this.callView = callView;
    }

    public String getTime() {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    public void incrementSeconds() {
        seconds++;
    }

    public void reset() {
        score = 0;
        level = new Level(0);
        seconds = 0;
        lineDeleteCount = 0;
    }

    public void execResetScoreSkillLabel() {
        if (callView != null) {
            if (resetScoreSkillLabel != null && resetScoreSkillLabel.isAlive()) {
                resetScoreSkillLabel.interrupt();
            }
            resetScoreSkillLabel = new Thread(() -> {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    // we don't care
                }
                lineClearDisplay = "";
                callView.run();
            });
            resetScoreSkillLabel.start();
        }
    }

    void calculateScore(int nbLinesCleared, boolean TSpin, boolean allClear) {
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
}
