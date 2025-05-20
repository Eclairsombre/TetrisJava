package Tetris.vue;

import Tetris.controller.Game;
import Tetris.vue.BasicComponent.Button;
import Tetris.vue.BasicComponent.MusicPlayer;
import Tetris.vue.Page.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static Tetris.controller.Action.*;

/**
 * GeneralScreen is the main class that creates the GUI for the Tetris game.
 * It initializes the game pages, handles user input, and manages the game state.
 * This class allows JPanel change interface without creating a new JFrame.
 */
public class GeneralScreen extends JFrame {
    private String selectedPage = "homePage";
    private final HomePage homePage;
    /// Main menu of the app

    private final PopupPage[] gameOverPopup = new PopupPage[2];
    /// Game over popup for each player

    private final TetrisPage[] tetrisPage = new TetrisPage[2];
    /// Game page for each player

    private final Game[] games = new Game[2];
    /// Game instance for each player

    private final PopupPage pausePopup;
    /// Pause page in the game screen

    private final MusicChoosePopup musicChoosePopup;
    /// Music selection page in the main menu

    private MusicPlayer musicPlayer;
    /// Music player for the background music

    private final JPanel[] playersPages = new JPanel[2];
    /// Game page template for each player

    private final boolean[] isGameOver = new boolean[2];
    private final boolean[] isPreviousGameOver = new boolean[2];
    private boolean is2PlayerMode;
    /// Basic booleans to manage the game state


    /**
     * Constructor for the GeneralScreen class. Initialize the app and all the pages.
     *
     * @param debugMode  boolean indicating if the game is in debug mode
     * @param debugPos   integer indicating the index of the debug position
     */
    public GeneralScreen(boolean debugMode, int debugPos) {
        // Initialize all the pages and the game instances
        for (int i = 0; i < this.games.length; i++) {
            this.games[i] = new Game(debugMode, debugPos);
        }
        this.musicPlayer = new MusicPlayer("data/music/TetrisOST.wav");
        this.musicChoosePopup = new MusicChoosePopup(() -> setPage("homePage"));

        this.homePage = new HomePage(new Button("Mode 1 Joueur", () -> {
            this.is2PlayerMode = false;
            setPage("tetrisView");
        }), new Button("Mode 2 Joueurs", () -> {
            this.is2PlayerMode = true;
            setPage("tetrisView");
        }), new Button("Choisir la musique", () -> setPage("musicChoosePage")));

        this.pausePopup = new PopupPage("PAUSE", Color.BLACK, this.games[0],
                new Button("Revenir au jeu", () -> setPage("tetrisView")),
                new Button("Retour au menu", () -> setPage("homePage"))
        );

        initPages();

        for (int i = 0; i < this.games.length; i++) {
            this.isGameOver[i] = false;
            this.isPreviousGameOver[i] = false;
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
        for (int i = 0; i < this.games.length; i++) {
            int finalI = i; // to use in the lambda expression
            this.tetrisPage[i] = new TetrisPage(this.games[i],
                    () -> {
                        this.isGameOver[finalI] = true;
                        setPage("gameOver");
                    }
            );
            this.tetrisPage[i].start();
            this.games[i].addGridObserver(this.tetrisPage[i]);

            this.gameOverPopup[i] = new PopupPage("GAME OVER", Color.RED, this.games[i],
                    new Button("Rejouer", () -> {
                        this.isGameOver[finalI] = false;
                        setPage("tetrisView");
                    }),
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
        String previousPage = this.selectedPage;
        getContentPane().removeAll();

        switch (page) {
            case "homePage" -> {
                this.musicPlayer.stop();
                this.musicPlayer.reset();
                this.musicPlayer = new MusicPlayer(this.musicChoosePopup.getPath());
                this.homePage.setButtonsVisibility(true);
                for (int i = 0; i < this.games.length; i++) { // reset the game instances
                    this.isGameOver[i] = false;
                    this.isPreviousGameOver[i] = false;
                    this.games[i].setAiMode(false);
                    this.games[i].stopGame();
                }
                this.homePage.setPreferredSize(getSize()); // to force resizing
                add(this.homePage);
            }
            case "tetrisView" -> {
                if (!this.musicPlayer.isPlaying()) {
                    this.musicPlayer.play();
                }

                for (int i = 0; i < this.games.length; i++) {
                    if ((this.isPreviousGameOver[i] && !previousPage.equals("pause")) || previousPage.equals("homePage")) {
                        this.games[i].reset();
                        this.playersPages[i] = this.tetrisPage[i];
                        this.isPreviousGameOver[i] = false;
                    }
                }

                this.games[0].resumeGame();
                if (this.is2PlayerMode) {
                    this.games[1].resumeGame();
                }

                if (previousPage.equals("homePage")) {
                    if (is2PlayerMode) {
                        this.playersPages[0].setPreferredSize(new Dimension(getSize().width / 2, getSize().height));
                        this.playersPages[1].setPreferredSize(new Dimension(getSize().width / 2, getSize().height));
                    } else {
                        this.playersPages[0].setPreferredSize(getSize());
                    }
                }

                if (this.is2PlayerMode) {
                    addTwinPanel(this.playersPages[0], this.playersPages[1]);
                } else {
                    add(this.playersPages[0]);
                }
            }
            case "gameOver" -> {
                if (this.is2PlayerMode) {
                    for (int i = 0; i < this.games.length; i++) {
                        if (!this.isGameOver[i]) {
                            continue;
                        }
                        this.gameOverPopup[i].updateStats(this.games[i].getStatsValues());
                        this.games[i].stopGame();
                        this.isPreviousGameOver[i] = true;
                        this.playersPages[i] = new JPanel();
                        int divideWidth = 2;
                        JLayeredPane layeredPane = getJLayeredPane(this.tetrisPage[i], this.gameOverPopup[i], divideWidth);
                        this.playersPages[i].add(layeredPane);
                        // because of the container, we need to force the resizing
                        this.playersPages[i].addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                layeredPane.setPreferredSize(new Dimension(getSize().width / divideWidth, getSize().height));
                            }
                        });
                    }
                    addTwinPanel(this.playersPages[0], this.playersPages[1]);
                } else if (this.isGameOver[0]) { // if only one player is playing
                    this.gameOverPopup[0].updateStats(this.games[0].getStatsValues());
                    this.games[0].stopGame();
                    this.isPreviousGameOver[0] = true;
                    this.playersPages[0] = new JPanel();
                    add(getJLayeredPane(this.tetrisPage[0], this.gameOverPopup[0], 1));
                }
            }
            case "musicChoosePage" -> {
                this.homePage.setButtonsVisibility(false);
                add(getJLayeredPane(this.homePage, this.musicChoosePopup, 1));
            }
            case "pause" -> {
                this.musicPlayer.stop();
                this.pausePopup.updateStats(this.games[0].getStatsValues());
                if (this.is2PlayerMode) {
                    JPanel container = new JPanel();
                    container.setLayout(new BorderLayout());
                    container.add(this.tetrisPage[0], BorderLayout.WEST);
                    container.add(this.tetrisPage[1], BorderLayout.EAST);
                    add(getJLayeredPane(container, this.pausePopup, 1));
                } else {
                    add(getJLayeredPane(this.tetrisPage[0], this.pausePopup, 1));
                }
            }
        }
        this.selectedPage = page;
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
     * @param background the background panel
     * @param foreground the foreground panel
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
     * @param background the background panel
     * @param foreground the foreground panel
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
                            games[0].pauseGame();
                            if (is2PlayerMode) {
                                games[1].pauseGame();
                            }
                            setPage("pause");
                        }
                        case "homePage" -> System.exit(0);
                    }
                } else if (selectedPage.equals("tetrisView")) {
                    if (e.getKeyCode() == KeyEvent.VK_O) {
                        games[0].setAiMode(!games[0].isAiMode());
                        tetrisPage[0].updateAILabel();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_P && is2PlayerMode) {
                        games[1].setAiMode(!games[1].isAiMode());
                        tetrisPage[1].updateAILabel();
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (!selectedPage.equals("tetrisView")) {
                    return;
                }
                if (!(games[0].isAiMode()) && !isGameOver[0] && !games[0].isPaused()) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_S -> MOVE_DOWN.execute(games[0]);
                        case KeyEvent.VK_Q -> MOVE_LEFT.execute(games[0]);
                        case KeyEvent.VK_D -> MOVE_RIGHT.execute(games[0]);
                        case KeyEvent.VK_Z -> RDROP.execute(games[0]);
                        case KeyEvent.VK_A -> ROTATE_LEFT.execute(games[0]);
                        case KeyEvent.VK_E -> ROTATE_RIGHT.execute(games[0]);
                        case KeyEvent.VK_SPACE -> HOLD.execute(games[0]);
                    }
                }

                if (is2PlayerMode && !(games[1].isAiMode()) && !isGameOver[1] && !games[1].isPaused()) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_NUMPAD5 -> MOVE_DOWN.execute(games[1]);
                        case KeyEvent.VK_NUMPAD4 -> MOVE_LEFT.execute(games[1]);
                        case KeyEvent.VK_NUMPAD6 -> MOVE_RIGHT.execute(games[1]);
                        case KeyEvent.VK_NUMPAD8 -> RDROP.execute(games[1]);
                        case KeyEvent.VK_NUMPAD7 -> ROTATE_LEFT.execute(games[1]);
                        case KeyEvent.VK_NUMPAD9 -> ROTATE_RIGHT.execute(games[1]);
                        case KeyEvent.VK_NUMPAD1 -> HOLD.execute(games[1]);
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
