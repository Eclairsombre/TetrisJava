package Tetris.vue;

import Tetris.controller.Game;
import Tetris.vue.BasicComponent.Button;
import Tetris.vue.BasicComponent.MusicPlayer;
import Tetris.vue.Page.HomePage;
import Tetris.vue.Page.MusicChoosePage;
import Tetris.vue.Page.PopupPage;
import Tetris.vue.Page.TetrisPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static Tetris.controller.Action.*;

public class GeneralScreen extends JFrame {
    HomePage homePage;
    TetrisPage[] tetrisPage = new TetrisPage[2];
    PopupPage[] gameOverPopup = new PopupPage[2];
    PopupPage pausePopup;
    String selectedPage = "homePage";
    Game[] games = new Game[2];
    String musicPath = "data/music/TetrisOST.wav";
    MusicChoosePage musicChoosePage;
    MusicPlayer musicPlayer;
    boolean debugMode;
    JPanel[] playersPages = new JPanel[2];
    boolean[] isGameOver = new boolean[2];
    boolean[] isPreviousGameOver = new boolean[2];
    boolean is2PlayerMode;

    public GeneralScreen(boolean debugMode, int debugPos) {
        setFocusable(true);
        this.debugMode = debugMode;
        for (int i = 0; i < this.games.length; i++) {
            this.games[i] = new Game(debugMode, debugPos);
        }
        this.musicPlayer = new MusicPlayer(this.musicPath);

        this.isGameOver[0] = false;
        this.isGameOver[1] = false;
        this.isPreviousGameOver[0] = false;
        this.isPreviousGameOver[1] = false;

        this.musicChoosePage = new MusicChoosePage(() -> {
            this.musicPlayer = new MusicPlayer(this.musicChoosePage.getPath());
            this.homePage.setButtonsVisibility(true);
            setPage("homePage");
        });

        this.homePage = new HomePage(
                new Button("Mode 1 Joueur", () -> {
                    this.is2PlayerMode = false;
                    setPage("tetrisView");
                }),
                new Button("Mode 2 Joueurs", () -> {
                    this.is2PlayerMode = true;
                    setPage("tetrisView");
                }),
                new Button("Choisir la musique", () -> {
                    this.homePage.setButtonsVisibility(false);
                    setPage("musicChoosePage");
                })
        );

        this.pausePopup = new PopupPage("PAUSE", Color.BLACK, this.games[0],
                new Button("Revenir au jeu", () -> {
                    setPage("tetrisView");
                    this.games[0].resumeGame();
                    this.games[1].resumeGame();
                }),
                new Button("Retour au menu", () -> setPage("homePage"))
        );

        initPages(0);
        initPages(1);

        playersPages[0] = tetrisPage[0];
        playersPages[1] = tetrisPage[1];

        createEventListener();

        setPage("homePage");
        setTitle("Tetris");
        setSize(1600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    public void initPages(int i) {
        Runnable changeToGameOver = () -> {
            this.gameOverPopup[i].updateStats(this.games[i].getStatsValues());
            this.games[i].stopGame();
            this.isGameOver[i] = true;
            this.isPreviousGameOver[i] = true;
            setPage("gameOver");
        };

        Runnable changeToPause = () -> {
            this.pausePopup.updateStats(this.games[0].getStatsValues());
            setPage("pause");
        };

        this.tetrisPage[i] = new TetrisPage(this.games[i], changeToGameOver, changeToPause);
        this.tetrisPage[i].start();
        this.games[i].addGridObserver(this.tetrisPage[i]);

        this.gameOverPopup[i] = new PopupPage("GAME OVER", Color.RED, this.games[i],
                new Tetris.vue.BasicComponent.Button("Rejouer", () -> {
                    this.isGameOver[i] = false;
                    this.games[i].reset();
                    games[i].resumeGame();
                    setPage("tetrisView");
                }),
                new Tetris.vue.BasicComponent.Button("Retour au menu", () -> {
                    isGameOver[0] = false;
                    isGameOver[1] = false;
                    isPreviousGameOver[0] = false;
                    isPreviousGameOver[1] = false;
                    setPage("homePage");
                })
        );
    }

    public synchronized void setPage(String page) {
        String previousPage = this.selectedPage;
        getContentPane().removeAll();

        switch (page) {
            case "homePage" -> {
                this.musicPlayer.stop();
                this.homePage.setPreferredSize(getSize()); // to force resizing
                add(this.homePage);
            }
            case "tetrisView" -> {
                if (previousPage.equals("homePage")) {
                    this.musicPlayer.stop();
                    this.musicPlayer.reset();
                    this.musicPlayer.play();
                }
                if (!is2PlayerMode) {
                    if (previousPage.equals("homePage") || previousPage.equals("gameOver")) {
                        this.games[0].reset();
                    }
                    playersPages[0] = tetrisPage[0];
                    playersPages[0].setPreferredSize(getSize());
                    add(this.playersPages[0]);
                } else {
                    if (previousPage.equals("homePage")) {
                        this.games[0].reset();
                        playersPages[0] = tetrisPage[0];

                        this.games[1].reset();
                        playersPages[1] = tetrisPage[1];

                        this.playersPages[0].setPreferredSize(new Dimension( getSize().width / 2, getSize().height));
                        this.playersPages[1].setPreferredSize(new Dimension( getSize().width / 2, getSize().height));
                    }

                    if (this.isPreviousGameOver[0]) {
                        playersPages[0] = tetrisPage[0];
                        this.isPreviousGameOver[0] = false;
                    } else {
                        playersPages[1] = tetrisPage[1];
                        this.isPreviousGameOver[1] = false;
                    }

                    JPanel component = new JPanel();
                    component.setLayout(new BorderLayout());

                    tetrisPage[0].updateBoard();
                    tetrisPage[0].updateNextPiece();
                    component.add(this.playersPages[0], BorderLayout.WEST);

                    tetrisPage[1].updateBoard();
                    tetrisPage[1].updateNextPiece();
                    component.add(this.playersPages[1], BorderLayout.EAST);

                    component.setPreferredSize(getSize());
                    component.setFocusable(false);
                    add(component);
                }
            }
            case "gameOver" -> {
                if (!is2PlayerMode) {
                    playersPages[0] = new JPanel();
                    playersPages[0].add(getJLayeredPane(this.tetrisPage[0], this.gameOverPopup[0], 1));
                    add(playersPages[0]);
                } else {
                    if (this.isGameOver[0]) {
                        this.gameOverPopup[0].updateStats(this.games[0].getStatsValues());
                        playersPages[0] = new JPanel();
                        playersPages[0].add(getJLayeredPane(this.tetrisPage[0], this.gameOverPopup[0], 2));
                    }
                    if (this.isGameOver[1]) {
                        this.gameOverPopup[1].updateStats(this.games[1].getStatsValues());
                        playersPages[1] = new JPanel();
                        playersPages[1].add(getJLayeredPane(this.tetrisPage[1], this.gameOverPopup[1], 2));
                    }
                    JPanel component = new JPanel();
                    component.setLayout(new BorderLayout());

                    component.add(this.playersPages[0], BorderLayout.WEST);
                    component.add(this.playersPages[1], BorderLayout.EAST);

                    component.setPreferredSize(getSize());
                    component.setFocusable(false);
                    add(component);
                }
            }
            case "musicChoosePage" -> add(getJLayeredPane(this.homePage, this.musicChoosePage, 1));
            case "pause" -> {
                this.musicPlayer.stop();
                if (is2PlayerMode) {
                    JPanel container = new JPanel();
                    container.setLayout(new BorderLayout());
                    container.add(this.tetrisPage[0], BorderLayout.WEST);
                    container.add(this.tetrisPage[1], BorderLayout.EAST);
                    add(getJLayeredPane(container, this.pausePopup, 1));
                } else {
                    add(getJLayeredPane(tetrisPage[0], this.pausePopup, 1));
                }
            }
        }
        this.selectedPage = page;

        repaint();
        revalidate();
    }

    private JLayeredPane getJLayeredPane(JPanel background, JPanel foreground, int divideWidth) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(getSize().width / divideWidth, getSize().height));
        layeredPane.setLayout(null);

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                background.setPreferredSize(new Dimension(getSize().width / divideWidth, getSize().height));
                prepareLayeredPane(background, foreground, divideWidth);
            }
        });
        prepareLayeredPane(background, foreground, divideWidth);

        layeredPane.add(background, Integer.valueOf(0));
        layeredPane.add(foreground, Integer.valueOf(1));
        return layeredPane;
    }

    private void prepareLayeredPane(JPanel background, JPanel foreground, int divideWidth) {
        background.setBounds(0, 0, getWidth() / divideWidth, getHeight());
        foreground.setBounds(
                getWidth() / (2 * divideWidth) - foreground.getWidth() / 2,
                getHeight() / 2 - foreground.getHeight() / 4,
                foreground.getWidth(),
                foreground.getHeight()
        );
    }

    public void createEventListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (selectedPage) {
                    case "pause" -> {
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            setPage("tetrisView");
                            games[0].resumeGame();
                            games[1].resumeGame();
                        }
                    }
                    case "tetrisView" -> {
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            games[0].pauseGame();
                            games[1].pauseGame();
                        }
                        if (e.getKeyCode() == KeyEvent.VK_O) {
                            games[0].setAiMode(!games[0].isAiMode());
                            tetrisPage[0].updateAILabel();
                        }
                        if (e.getKeyCode() == KeyEvent.VK_P) {
                            games[1].setAiMode(!games[1].isAiMode());
                            tetrisPage[1].updateAILabel();
                        }
                    }
                    case "gameOver" -> {
                        // do nothing
                    }
                    default -> {
                        return;
                    }
                }
                if (games[0].isPaused()) {
                    return;
                }

                if (!(games[0].isAiMode()) && !isGameOver[0]) {
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

                if (is2PlayerMode && !(games[1].isAiMode()) && !isGameOver[1]) {
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
            public void componentResized(java.awt.event.ComponentEvent e) {
                for (JPanel page : playersPages) {
                    page.setPreferredSize(new Dimension(getSize().width / 2, getSize().height));
                    page.revalidate();
                    page.setFocusable(false);
                }
            }
        });
    }
}
