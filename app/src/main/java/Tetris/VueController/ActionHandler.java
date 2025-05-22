package Tetris.VueController;

import Tetris.Utils.ObservableAction;

import java.util.Observable;

@SuppressWarnings("deprecation")
public class ActionHandler extends Observable {
    public void handleAction(ObservableAction OA) {
        setChanged();
        notifyObservers(OA);
    }
}
