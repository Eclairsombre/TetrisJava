package Tetris.controller;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static Tetris.model.Action.*;

public class HumanInputStrategy implements InputStrategy {
    private final KeyAdapter keyAdapter;
    private final JPanel panel;
    private boolean enabled = false;

    public HumanInputStrategy(JPanel panel) {
        this.panel = panel;
        this.keyAdapter = new KeyAdapter() {
            @Override
            public synchronized void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    PAUSE.execute(InputController.getCurrentGame());
                    return;
                }

                if (InputController.getCurrentGame().isPaused()) {
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN -> MOVE_DOWN.execute(InputController.getCurrentGame());
                    case KeyEvent.VK_LEFT -> MOVE_LEFT.execute(InputController.getCurrentGame());
                    case KeyEvent.VK_RIGHT -> MOVE_RIGHT.execute(InputController.getCurrentGame());
                    case KeyEvent.VK_UP -> RDROP.execute(InputController.getCurrentGame());
                    case KeyEvent.VK_Q -> ROTATE_LEFT.execute(InputController.getCurrentGame());
                    case KeyEvent.VK_D -> ROTATE_RIGHT.execute(InputController.getCurrentGame());
                    case KeyEvent.VK_SPACE -> HOLD.execute(InputController.getCurrentGame());
                    default -> {
                    }
                }
            }
        };
    }

    @Override
    public void processInput(Game game) {
        // nothing to do here, input is handled by keyAdapter
    }

    @Override
    public void enable() {
        if (!enabled) {
            panel.addKeyListener(keyAdapter);
            panel.requestFocus();
            enabled = true;
        }
    }

    @Override
    public void disable() {
        if (enabled) {
            panel.removeKeyListener(keyAdapter);
            enabled = false;
        }
    }
}