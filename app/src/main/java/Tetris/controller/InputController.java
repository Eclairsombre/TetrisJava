package Tetris.controller;

import javax.swing.*;

public class InputController {
    private static Game currentGame;
    private final HumanInputStrategy humanStrategy;
    private final AIInputStrategy aiStrategy;
    private InputStrategy currentStrategy;

    public InputController(Game game, JPanel panel) {
        InputController.currentGame = game;
        this.humanStrategy = new HumanInputStrategy(panel);
        this.aiStrategy = new AIInputStrategy();

        setHumanControlled();
    }

    public static Game getCurrentGame() {
        return currentGame;
    }

    public void setHumanControlled() {
        if (currentStrategy != null) {
            currentStrategy.disable();
        }
        currentStrategy = humanStrategy;
        currentStrategy.enable();
    }

    public void setAIControlled() {
        if (currentStrategy != null) {
            currentStrategy.disable();
        }
        currentStrategy = aiStrategy;
        currentStrategy.enable();
    }

    public boolean isHumanControlled() {
        return currentStrategy == humanStrategy;
    }

    public boolean isAIControlled() {
        return currentStrategy == aiStrategy;
    }

    public void disable() {
        if (currentStrategy != null) {
            currentStrategy.disable();
        }
    }
}