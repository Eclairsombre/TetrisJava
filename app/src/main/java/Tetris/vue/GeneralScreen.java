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

public class GeneralScreen extends JFrame {
    HomePage homePage;
    TetrisPage tetrisPage;
    PopupPage gameOverPopup;
    PopupPage pausePopup;
    String selectedPage = "homePage";
    Game game;
    String musicPath = "data/music/tetris.wav";
    MusicChoosePage musicChoosePage;
    MusicPlayer musicPlayer;

    public GeneralScreen() {
        setFocusable(true);

        game = new Game();
        musicPlayer = new MusicPlayer(musicPath);

        musicChoosePage = new MusicChoosePage(() -> {
            musicPlayer = new MusicPlayer(musicChoosePage.getPath());
            setPage("homePage");
        });

        homePage = new HomePage(
                new Tetris.vue.BasicComponent.Button("Jouer", () -> setPage("tetrisView")),
                new Tetris.vue.BasicComponent.Button("Choisir la musique", () -> setPage("musicChoosePage"))
        );

        Runnable changeToGameOver = () -> {
            gameOverPopup.updateStats(this.game.getStatsValues());
            this.game.stopGame();
            setPage("gameOver");
        };

        Runnable changeToPause = () -> {
            pausePopup.updateStats(this.game.getStatsValues());
            setPage("pause");
        };

        tetrisPage = new TetrisPage(game, changeToGameOver, changeToPause);
        tetrisPage.start();
        game.addGridObserver(tetrisPage);

        gameOverPopup = new PopupPage("GAME OVER", Color.RED, game,
                new Tetris.vue.BasicComponent.Button("Rejouer", () -> setPage("tetrisView")),
                new Tetris.vue.BasicComponent.Button("Retour au menu", () -> setPage("homePage"))
        );

        pausePopup = new PopupPage("PAUSE", Color.BLACK, game,
                new Tetris.vue.BasicComponent.Button("Revenir au jeu", () -> {
                    setPage("tetrisView");
                    game.resumeGame();
                }),
                new Button("Retour au menu", () -> setPage("homePage"))
        );

        createEventListener();

        setPage("homePage");
        setTitle("Tetris");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    public void setPage(String page) {
        String previousPage = selectedPage;
        this.getContentPane().removeAll();

        switch (page) {
            case "homePage" -> {
                musicPlayer.stop();
                add(homePage);
            }
            case "tetrisView" -> {
                if (previousPage.equals("gameOver") || previousPage.equals("homePage")) {
                    if (previousPage.equals("homePage")) {
                        musicPlayer.reset();
                    } else {
                        musicPlayer.stop();
                    }
                    game.reset();
                }
                add(tetrisPage);
                musicPlayer.play();
                tetrisPage.updateBoard();
                tetrisPage.updateNextPiece();
            }
            case "gameOver" -> add(getJLayeredPane(tetrisPage, gameOverPopup));
            case "musicChoosePage" -> add(getJLayeredPane(homePage, musicChoosePage));
            case "pause" -> {
                musicPlayer.stop();
                add(getJLayeredPane(tetrisPage, pausePopup));
            }
        }
        selectedPage = page;
        this.repaint();
        this.revalidate();
    }

    private JLayeredPane getJLayeredPane(JPanel background, JPanel foreground) {
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(getSize());
        layeredPane.setLayout(null);

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                prepareLayeredPane(background, foreground);
            }
        });
        prepareLayeredPane(background, foreground);

        layeredPane.add(background, Integer.valueOf(0));
        layeredPane.add(foreground, Integer.valueOf(1));
        return layeredPane;
    }

    private void prepareLayeredPane(JPanel background, JPanel foreground) {
        background.setBounds(0, 0, getWidth(), getHeight());
        foreground.setBounds(
                getWidth() / 2 - foreground.getWidth() / 2,
                getHeight() / 2 - foreground.getHeight() / 4,
                foreground.getWidth(),
                foreground.getHeight()
        );
    }

    public void createEventListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public synchronized void keyPressed(KeyEvent e) {
                switch (selectedPage) {
                    case "pause" -> {
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            setPage("tetrisView");
                            game.resumeGame();
                        }
                    }
                    case "tetrisView" -> {
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            game.pauseGame();
                        }
                    }
                    default -> {
                        return;
                    }
                }
                if (game.isPaused()) {
                    return;
                }
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN -> game.movePieceDown(true);
                    case KeyEvent.VK_LEFT -> game.movePieceLeft();
                    case KeyEvent.VK_RIGHT -> game.movePieceRight();
                    case KeyEvent.VK_UP -> game.doRdrop();
                    case KeyEvent.VK_Q -> game.rotatePieceLeft();
                    case KeyEvent.VK_D -> game.rotatePieceRight();
                    case KeyEvent.VK_SPACE -> game.exchangeHoldAndCurrent();
                    default -> {
                        // Do nothing
                    }
                }
            }
        });
    }
}
