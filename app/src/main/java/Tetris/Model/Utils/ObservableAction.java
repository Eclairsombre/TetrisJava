package Tetris.Model.Utils;

import java.awt.event.KeyEvent;

import static Tetris.Model.Utils.Action.*;

public record ObservableAction(int idGrid, Action action) {
    /// @param idGrid The id of the grid
    /// @param action The action to perform
    /// @return A new ObservableAction object with the given id and action
    public static ObservableAction of(int idGrid, Action action) {
        return new ObservableAction(idGrid, action);
    }

    public static ObservableAction getAction(KeyEvent e) {
        ObservableAction toExecute = new ObservableAction(-1, null);
        switch (e.getKeyCode()) {
            case KeyEvent.VK_S -> toExecute = new ObservableAction(0, MOVE_DOWN);
            case KeyEvent.VK_Q -> toExecute = new ObservableAction(0, MOVE_LEFT);
            case KeyEvent.VK_D -> toExecute = new ObservableAction(0, MOVE_RIGHT);
            case KeyEvent.VK_Z -> toExecute = new ObservableAction(0, RDROP);
            case KeyEvent.VK_A -> toExecute = new ObservableAction(0, ROTATE_LEFT);
            case KeyEvent.VK_E -> toExecute = new ObservableAction(0, ROTATE_RIGHT);
            case KeyEvent.VK_SPACE -> toExecute = new ObservableAction(0, HOLD);
            case KeyEvent.VK_NUMPAD5 -> toExecute = new ObservableAction(1, MOVE_DOWN);
            case KeyEvent.VK_NUMPAD4 -> toExecute = new ObservableAction(1, MOVE_LEFT);
            case KeyEvent.VK_NUMPAD6 -> toExecute = new ObservableAction(1, MOVE_RIGHT);
            case KeyEvent.VK_NUMPAD8 -> toExecute = new ObservableAction(1, RDROP);
            case KeyEvent.VK_NUMPAD7 -> toExecute = new ObservableAction(1, ROTATE_LEFT);
            case KeyEvent.VK_NUMPAD9 -> toExecute = new ObservableAction(1, ROTATE_RIGHT);
            case KeyEvent.VK_NUMPAD1 -> toExecute = new ObservableAction(1, HOLD);
        }
        return toExecute;
    }
}
