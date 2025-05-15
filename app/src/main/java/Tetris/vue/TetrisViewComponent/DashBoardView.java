package Tetris.vue.TetrisViewComponent;

import Tetris.model.StatsValues;

import javax.swing.*;
import java.awt.*;

public class DashBoardView extends JPanel {
    private final JLabel scoreLabel, levelLabel, timerLabel, lineDeleteCountLabel;

    public DashBoardView(Color backgroundColor) {
        int fontSize = 16;
        setBackground(backgroundColor);
        setLayout(new GridLayout(2, 2, 0, 0));

        scoreLabel = new JLabel("Score : 0", SwingConstants.CENTER);
        TextView scorePanel = new TextView(fontSize, backgroundColor, scoreLabel);
        add(scorePanel);

        levelLabel = new JLabel("Niveau : 1", SwingConstants.CENTER);
        TextView levelPanel = new TextView(fontSize, backgroundColor, levelLabel);
        add(levelPanel);

        timerLabel = new JLabel("Temps écoulé : 00:00:00", SwingConstants.CENTER);
        TextView timerPanel = new TextView(fontSize, backgroundColor, timerLabel);
        add(timerPanel);

        lineDeleteCountLabel = new JLabel("Lignes supprimées : 0", SwingConstants.CENTER);
        TextView lineDeleteCountPanel = new TextView(fontSize, backgroundColor, lineDeleteCountLabel);
        add(lineDeleteCountPanel);
    }

    public void updateStats(StatsValues statsValues) {
        scoreLabel.setText("Score : " + statsValues.score);
        levelLabel.setText("Niveau : " + (statsValues.level.getLevel() + 1));
        lineDeleteCountLabel.setText("Lignes supprimées : " + statsValues.lineDeleteCount);
    }

    public void updateTimerLabel(String time) {
        timerLabel.setText("Temps écoulé : " + time);
    }
}
