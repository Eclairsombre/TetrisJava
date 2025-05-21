package Tetris.VueController;

import Tetris.Model.Grid;
import Tetris.VueController.BasicComponent.Button;
import Tetris.VueController.BasicComponent.MusicPlayer;
import Tetris.VueController.Page.HomePage;
import Tetris.VueController.Page.MusicChoosePopup;
import Tetris.VueController.Page.PopupPage;
import Tetris.VueController.Page.TetrisPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static Tetris.Model.Utils.Action.*;


/**
 * GeneralScreen is the main class that creates the GUI for the Tetris game.
 * It initializes the game pages, handles user input, and manages the game state.
 * This class allows JPanel change interface without creating a new JFrame.
 */
public class GeneralScreen extends JFrame {
    private final HomePage homePage;
    /// Main menu of the app

    private final PopupPage[] gameOverPopup = new PopupPage[2];
    /// Game over popup for each player

    private final TetrisPage[] tetrisPage = new TetrisPage[2];
    /// Game page for each player

    private final Grid[] grids = new Grid[2];
    /// Game instance for each player

    private final PopupPage pausePopup;
    /// Pause page in the game screen

    private final MusicChoosePopup musicChoosePopup;
    /// Music player for the background music

    private final JPanel[] playersPages = new JPanel[2];
    /// Game page template for each player

    private final boolean[] isGameOver = new boolean[2];
    private String selectedPage = "homePage";
    /// Music selection page in the main menu

    private MusicPlayer musicPlayer;
    private boolean is2PlayerMode;
    /// Basic booleans to manage the game state


    /**
     * Constructor for the GeneralScreen class. Initialize the app and all the pages.
     *
     * @param debugMode boolean indicating if the game is in debug mode
     * @param debugPos  integer indicating the index of the debug position
     */
    public GeneralScreen(boolean debugMode, int debugPos) {
        // Initialize all the pages and the game instances
        for (int i = 0; i < grids.length; i++) {
            grids[i] = new Grid(10, 25, debugMode, debugPos);
        }
        musicPlayer = new MusicPlayer("data/music/TetrisOST.wav");
        musicChoosePopup = new MusicChoosePopup(() -> setPage("homePage"));

        homePage = new HomePage(new Button("Mode 1 Joueur", () -> {
            is2PlayerMode = false;
            setPage("tetrisView");
        }), new Button("Mode 2 Joueurs", () -> {
            is2PlayerMode = true;
            setPage("tetrisView");
        }), new Button("Choisir la musique", () -> setPage("musicChoosePage")));

        pausePopup = new PopupPage("PAUSE", Color.BLACK, grids[0].getStatsValues(),
                new Button("Revenir au jeu", () -> setPage("tetrisView")),
                new Button("Retour au menu", () -> setPage("homePage"))
        );

        initPages();

        for (int i = 0; i < grids.length; i++) {
            isGameOver[i] = false;
            playersPages[i] = tetrisPage[i];
        }

        // Set global parameters and initialize the JFrame
        createEventListener();
        setPage("homePage");
        setTitle("Tetris");
        setSize(1600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // to center the window
        setVisible(true);
        setFocusable(true);
    }

    /**
     * Initialize the game pages for each player.
     */
    public void initPages() {
        for (int i = 0; i < grids.length; i++) {
            int finalI = i; // to use in the lambda expression
            tetrisPage[i] = new TetrisPage(grids[i],
                    () -> {
                        isGameOver[finalI] = true;
                        setPage("gameOver");
                    }
            );
            grids[i].addObserver(tetrisPage[i]);

            gameOverPopup[i] = new PopupPage("GAME OVER", Color.RED, grids[i].getStatsValues(),
                    new Button("Rejouer", () -> setPage("tetrisView")),
                    new Button("Retour au menu", () -> setPage("homePage"))
            );
        }
    }

    /**
     * Set the current page of the JFrame and apply the necessary changes.
     *
     * @param page the name of the page to set
     */
    public synchronized void setPage(String page) {
        String previousPage = selectedPage;
        getContentPane().removeAll();

        switch (page) {
            case "homePage" -> {
                musicPlayer.stop();
                musicPlayer.reset();
                musicPlayer = new MusicPlayer(musicChoosePopup.getPath());
                homePage.setButtonsVisibility(true);
                for (int i = 0; i < grids.length; i++) { // reset the game instances
                    isGameOver[i] = false;
                    grids[i].setAiMode(false);
                    grids[i].stopGame();
                }
                homePage.setPreferredSize(getSize()); // to force resizing
                add(homePage);
            }
            case "tetrisView" -> {
                if (!musicPlayer.isPlaying()) {
                    musicPlayer.play();
                }

                for (int i = 0; i < (is2PlayerMode ? grids.length : 1); i++) {
                    if ((isGameOver[i] && !previousPage.equals("pause")) || previousPage.equals("homePage")) {
                        grids[i].reset();
                        playersPages[i] = tetrisPage[i];
                        isGameOver[i] = false;
                    }
                }

                grids[0].resumeGame();
                if (is2PlayerMode) {
                    grids[1].resumeGame();
                }

                if (previousPage.equals("homePage")) {
                    if (is2PlayerMode) {
                        playersPages[0].setPreferredSize(new Dimension(getSize().width / 2, getSize().height));
                        playersPages[1].setPreferredSize(new Dimension(getSize().width / 2, getSize().height));
                    } else {
                        playersPages[0].setPreferredSize(getSize());
                    }
                }

                if (is2PlayerMode) {
                    addTwinPanel(playersPages[0], playersPages[1]);
                } else {
                    add(playersPages[0]);
                }
            }
            case "gameOver" -> {
                if (is2PlayerMode) {
                    for (int i = 0; i < grids.length; i++) {
                        if (!isGameOver[i]) {
                            continue;
                        }
                        gameOverPopup[i].updateStats(grids[i].getStatsValues());
                        grids[i].stopGame();
                        playersPages[i] = new JPanel();
                        int divideWidth = 2;
                        JLayeredPane layeredPane = getJLayeredPane(tetrisPage[i], gameOverPopup[i], divideWidth);
                        playersPages[i].add(layeredPane);
                        // because of the container, we need to force the resizing
                        playersPages[i].addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                layeredPane.setPreferredSize(new Dimension(getSize().width / divideWidth, getSize().height));
                            }
                        });
                    }
                    addTwinPanel(playersPages[0], playersPages[1]);
                } else if (isGameOver[0]) { // if only one player is playing
                    gameOverPopup[0].updateStats(grids[0].getStatsValues());
                    grids[0].stopGame();
                    playersPages[0] = new JPanel();
                    add(getJLayeredPane(tetrisPage[0], gameOverPopup[0], 1));
                }
            }
            case "musicChoosePage" -> {
                homePage.setButtonsVisibility(false);
                add(getJLayeredPane(homePage, musicChoosePopup, 1));
            }
            case "pause" -> {
                musicPlayer.stop();
                pausePopup.updateStats(grids[0].getStatsValues());
                if (is2PlayerMode) {
                    JPanel container = new JPanel();
                    container.setLayout(new BorderLayout());
                    container.add(tetrisPage[0], BorderLayout.WEST);
                    container.add(tetrisPage[1], BorderLayout.EAST);
                    add(getJLayeredPane(container, pausePopup, 1));
                } else {
                    add(getJLayeredPane(tetrisPage[0], pausePopup, 1));
                }
            }
        }
        selectedPage = page;
        repaint();
        revalidate();
    }

    /**
     * Add two panels side by side in a container. (Used for 2 players mode)
     *
     * @param panel1 the first panel to add
     * @param panel2 the second panel to add
     */
    private void addTwinPanel(JPanel panel1, JPanel panel2) {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.add(panel1, BorderLayout.WEST);
        container.add(panel2, BorderLayout.EAST);
        container.setPreferredSize(getSize());
        container.setFocusable(false);
        add(container);
    }


    /**
     * Create a JLayeredPane with two panels (background and foreground) and set their bounds.
     * Usually used for the game over popup and the pause popup.
     *
     * @param background  the background panel
     * @param foreground  the foreground panel
     * @param divideWidth the width to divide the screen
     * @return the JLayeredPane with the two panels
     */
    private JLayeredPane getJLayeredPane(JPanel background, JPanel foreground, int divideWidth) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(getSize().width / divideWidth, getSize().height));
        layeredPane.setLayout(null);

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                background.setPreferredSize(new Dimension(getSize().width / divideWidth, getSize().height));
                prepareLayeredPane(background, foreground, divideWidth);
            }
        });
        prepareLayeredPane(background, foreground, divideWidth);
        layeredPane.add(background, Integer.valueOf(0));
        layeredPane.add(foreground, Integer.valueOf(1));
        return layeredPane;
    }

    /**
     * Prepare the layered pane by setting the bounds of the background and foreground panels.
     *
     * @param background  the background panel
     * @param foreground  the foreground panel
     * @param divideWidth the width to divide the screen
     */
    private void prepareLayeredPane(JPanel background, JPanel foreground, int divideWidth) {
        background.setBounds(0, 0, getWidth() / divideWidth, getHeight());
        foreground.setBounds(
                getWidth() / (2 * divideWidth) - foreground.getWidth() / 2,
                getHeight() / 2 - foreground.getHeight() / 4,
                foreground.getWidth(),
                foreground.getHeight()
        );
    }

    /**
     * Global event listener for the JFrame, to handle key events and component resizing.
     */
    public void createEventListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    switch (selectedPage) {
                        case "pause" -> setPage("tetrisView");
                        case "tetrisView", "gameOver" -> {
                            grids[0].pauseGame();
                            if (is2PlayerMode) {
                                grids[1].pauseGame();
                            }
                            setPage("pause");
                        }
                        case "homePage" -> System.exit(0);
                    }
                } else if (selectedPage.equals("tetrisView") || selectedPage.equals("gameOver")) {
                    if (e.getKeyCode() == KeyEvent.VK_O) {
                        grids[0].setAiMode(!grids[0].isAiMode());
                        tetrisPage[0].updateAILabel(grids[0]);
                    }
                    if (e.getKeyCode() == KeyEvent.VK_P && is2PlayerMode) {
                        grids[1].setAiMode(!grids[1].isAiMode());
                        tetrisPage[1].updateAILabel(grids[1]);
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (!(selectedPage.equals("tetrisView") || selectedPage.equals("gameOver"))) {
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_S -> MOVE_DOWN.execute(grids[0]);
                    case KeyEvent.VK_Q -> MOVE_LEFT.execute(grids[0]);
                    case KeyEvent.VK_D -> MOVE_RIGHT.execute(grids[0]);
                    case KeyEvent.VK_Z -> RDROP.execute(grids[0]);
                    case KeyEvent.VK_A -> ROTATE_LEFT.execute(grids[0]);
                    case KeyEvent.VK_E -> ROTATE_RIGHT.execute(grids[0]);
                    case KeyEvent.VK_SPACE -> HOLD.execute(grids[0]);

                }

                if (is2PlayerMode) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_NUMPAD5 -> MOVE_DOWN.execute(grids[1]);
                        case KeyEvent.VK_NUMPAD4 -> MOVE_LEFT.execute(grids[1]);
                        case KeyEvent.VK_NUMPAD6 -> MOVE_RIGHT.execute(grids[1]);
                        case KeyEvent.VK_NUMPAD8 -> RDROP.execute(grids[1]);
                        case KeyEvent.VK_NUMPAD7 -> ROTATE_LEFT.execute(grids[1]);
                        case KeyEvent.VK_NUMPAD9 -> ROTATE_RIGHT.execute(grids[1]);
                        case KeyEvent.VK_NUMPAD1 -> HOLD.execute(grids[1]);
                    }
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                for (JPanel page : playersPages) {
                    page.setPreferredSize(new Dimension(getSize().width / 2, getSize().height));
                    page.revalidate();
                    page.setFocusable(false);
                }
            }
        });
    }
}
