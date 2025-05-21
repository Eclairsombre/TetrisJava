package Tetris.VueController;

import Tetris.Utils.ObservableAction;

import java.util.Observable;

@SuppressWarnings("deprecation")
public class KeybindHandler extends Observable {
    public void handleKeybind(ObservableAction OA) {
        setChanged();
        notifyObservers(OA);
    }
}
