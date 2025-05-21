package Tetris.VueController.Page.TetrisComponent;

import Tetris.Utils.ObservableMessage;
import Tetris.Model.TetrisInstanceComponent.StatsValues;
import Tetris.VueController.BasicComponent.TextView;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * DashBoardView shows the game statistics and information.
 */
@SuppressWarnings("deprecation")
public class DashBoardView extends JPanel implements Observer {
    private final JLabel AILabel, scoreLabel, levelLabel, timerLabel, lineDeleteCountLabel, scoreDisplayLabel;
    /// AILabel              Label for AI mode status
    /// scoreLabel           Label for score
    /// levelLabel           Label for level
    /// timerLabel           Label for timer
    /// lineDeleteCountLabel Label for deleted lines count
    /// scoreDisplayLabel    Label for score display

    /**
     * Constructor for DashBoardView.
     *
     * @param backgroundColor The background color of the dashboard.
     */
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
        TextView scoreDisplayPanel = new TextView(fontSize + 6, backgroundColor, scoreDisplayLabel);
        scoreDisplayPanel.setBounds(20, 300, 260, 50);
        add(scoreDisplayPanel);
    }

    public void updateStats(StatsValues statsValues) {
        scoreLabel.setText("Score : " + statsValues.score);
        levelLabel.setText("Niveau : " + (statsValues.level.level()));
        lineDeleteCountLabel.setText("Lignes supprimées : " + statsValues.lineDeleteCount);
        scoreDisplayLabel.setText(statsValues.lineClearDisplay);
    }

    public void updateAILabel(String text) {
        AILabel.setText(text);
    }

    public void updateTimerLabel(String time) {
        timerLabel.setText("Temps écoulé : " + time);
    }

    /**
     * Reception of the updates from the game, through the observer pattern.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof ObservableMessage OM)) {
            System.err.println("Error: arg is not a ObservableMessage");
            return;
        }
        switch (OM.message()) {
            case "timer" -> updateTimerLabel(OM.statsValues().getTime());
            case "stats" -> updateStats(OM.statsValues());
            case "gameOver" -> updateAILabel("IA Mode : OFF");
            case "AILabel" -> updateAILabel(OM.isAiMode() ? "AI Mode : ON" : "AI Mode : OFF");
        }
    }
}
