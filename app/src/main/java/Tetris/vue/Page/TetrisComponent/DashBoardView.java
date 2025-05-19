package Tetris.vue.Page.TetrisComponent;

import Tetris.model.StatsValues;
import Tetris.vue.BasicComponent.TextView;

import javax.swing.*;
import java.awt.*;

public class DashBoardView extends JPanel {
    private final JLabel AILabel, scoreLabel, levelLabel, timerLabel, lineDeleteCountLabel, scoreDisplayLabel;

    public DashBoardView(Color backgroundColor) {
        int fontSize = 16;
        setBackground(backgroundColor);
        setLayout(null);

        scoreLabel = new JLabel("Score : 0", SwingConstants.CENTER);
        TextView scorePanel = new TextView(fontSize, backgroundColor, scoreLabel);
        scorePanel.setBounds(0, 0, 200, 50);
        add(scorePanel);

        levelLabel = new JLabel("Niveau : 1", SwingConstants.CENTER);
        TextView levelPanel = new TextView(fontSize, backgroundColor, levelLabel);
        levelPanel.setBounds(0, 50, 200, 50);
        add(levelPanel);

        timerLabel = new JLabel("Temps écoulé : 00:00:00", SwingConstants.CENTER);
        TextView timerPanel = new TextView(fontSize, backgroundColor, timerLabel);
        timerPanel.setBounds(0, 100, 200, 50);
        add(timerPanel);

        AILabel = new JLabel("IA Mode : OFF", SwingConstants.CENTER);
        TextView AIPanel = new TextView(fontSize, backgroundColor, AILabel);
        AIPanel.setBounds(0, 150, 200, 50);
        add(AIPanel);

        lineDeleteCountLabel = new JLabel("Lignes supprimées : 0", SwingConstants.CENTER);
        TextView lineDeleteCountPanel = new TextView(fontSize, backgroundColor, lineDeleteCountLabel);
        lineDeleteCountPanel.setBounds(0, 200, 200, 50);
        add(lineDeleteCountPanel);

        scoreDisplayLabel = new JLabel("", SwingConstants.CENTER);
        scoreDisplayLabel.setForeground(Color.RED);
        TextView scoreDisplayPanel = new TextView(fontSize * 2, backgroundColor, scoreDisplayLabel);
        scoreDisplayPanel.setBounds(20, 300, 260, 50);
        add(scoreDisplayPanel);
    }

    public void updateStats(StatsValues statsValues) {
        scoreLabel.setText("Score : " + statsValues.score);
        levelLabel.setText("Niveau : " + (statsValues.level.getLevel() + 1));
        lineDeleteCountLabel.setText("Lignes supprimées : " + statsValues.lineDeleteCount);
        scoreDisplayLabel.setText(statsValues.lineClearDisplay);
    }

    public void updateAILabel(String text) {
        AILabel.setText(text);
    }

    public void updateTimerLabel(String time) {
        timerLabel.setText("Temps écoulé : " + time);
    }
}
