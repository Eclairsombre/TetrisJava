package Tetris.VueController.Page;

import Tetris.Model.Grid;
import Tetris.Model.Utils.ObservableMessage;
import Tetris.VueController.Page.TetrisComponent.DashBoardView;
import Tetris.VueController.Page.TetrisComponent.GameBoardView;
import Tetris.VueController.Page.TetrisComponent.PieceDisplayManager;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * TetrisPage class represents the main game page of the Tetris game.
 */
@SuppressWarnings("deprecation")
public class TetrisPage extends JPanel implements Observer {
    private final DashBoardView dashBoardVue;
    /// dashBoardVue the dashboard of the game
    private final PieceDisplayManager piecePanel;
    /// piecePanel the panel that displays the pieces
    private final GameBoardView boardView;
    /// heightGrid the height of the grid
    Runnable changeToGameOver;
    /// @param changeToGameOver the function to call when the game is over

    /**
     * Constructor for the TetrisPage class.
     * Initializes a Tetris game view
     *
     * @param grid                The grid instance.
     * @param changeToGameOver Runnable to change to the game over screen.
     */
    public TetrisPage(Grid grid, Runnable changeToGameOver) {
        Color backgroundColor = Color.LIGHT_GRAY;
        this.changeToGameOver = changeToGameOver;

        boardView = new GameBoardView(grid.getLengthGrid()[0], grid.getLengthGrid()[1], backgroundColor);
        dashBoardVue = new DashBoardView(backgroundColor);
        dashBoardVue.setPreferredSize(new Dimension(260, 200));
        piecePanel = new PieceDisplayManager(20, 20, backgroundColor, grid.getFileWriterAndReader());

        JPanel templatePanel = new JPanel();
        templatePanel.add(piecePanel);
        templatePanel.setBackground(backgroundColor);

        setLayout(new BorderLayout());
        add(boardView, BorderLayout.CENTER);
        add(dashBoardVue, BorderLayout.WEST);
        add(templatePanel, BorderLayout.EAST);

        SwingUtilities.invokeLater(() -> { // to fix or remove to solve the problem if no clue found
            piecePanel.updateNextPiece(grid.getPieceManager());
            boardView.updateBoard(grid.getGrid(), grid.getPieceManager().getCurrentPiece(), grid.findMaxY(grid.getPieceManager().getCurrentPiece()));
        });

        addObserverToTetrisPage(grid);
        setVisible(true);
    }

    public void addObserverToTetrisPage(Grid grid) {
        grid.addObserver(piecePanel);
        grid.addObserver(dashBoardVue);
        grid.addObserver(boardView);
        grid.addObserver(this);
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
        if (OM.message().equals("gameOver")) {
            changeToGameOver.run();
        }
    }
}
