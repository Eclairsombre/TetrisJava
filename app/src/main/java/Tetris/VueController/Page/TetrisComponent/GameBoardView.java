package Tetris.VueController.Page.TetrisComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Displays the game board.
 */
public class GameBoardView extends JPanel {
    JPanel[][] cases;
    /// @param cases the cases of the game board
    int width;
    /// @param width the width of the game board
    int height;
    /// @param height the height of the game board
    JPanel boardPanel;
    /// @param boardPanel the panel that contains the game board
    int ratio = 13;
    /// @param ratio the ratio of the game board
    int offset = 20;
    /// @param offset the offset of the game board

    /**
     * Constructor for the GameBoardView class.
     *
     * @param width           The width of the game board.
     * @param height          The height of the game board.
     * @param cases           The cases of the game board.
     * @param backgroundColor The background color of the game board.
     */
    public GameBoardView(int width, int height, JPanel[][] cases, Color backgroundColor) {
        this.cases = cases;
        this.width = width;
        this.height = height;
        setLayout(new GridBagLayout());
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(height, width, 0, 0));
        for (int y = 0; y < cases.length; y++) {
            for (int x = 0; x < cases[y].length; x++) {
                cases[y][x] = new CustomJPanel(Color.BLACK);
                boardPanel.add(cases[y][x]);
            }
        }
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 3),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.DARK_GRAY, 8),
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3)
                )
        ));
        add(boardPanel);
        setBackground(backgroundColor);
        setFocusable(false);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                setSize(getSize().width / 2, getSize().height / 2); // Adjust the size of the boardPanel
            }
        });
    }

    @Override
    public void setSize(int width, int height) {
        int length = Math.min(getHeight(), height);
        int new_height;
        int new_width = length / ratio * this.width;
        if (new_width > getWidth()) {
            length = getWidth();
            new_width = length / ratio * this.width; // we change the length so we have to recalculate the width
        }
        new_height = length / ratio * this.height;
        boardPanel.setPreferredSize(new Dimension(
                new_width,
                new_height - offset
        ));
        revalidate();
    }
}
