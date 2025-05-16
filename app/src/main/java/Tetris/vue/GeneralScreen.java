package Tetris.vue;

import Tetris.controller.Game;
import Tetris.model.Grid;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GeneralScreen extends JFrame {
    HomePage homePage;
    TetrisView tetrisView;
    GameOverPopup gameOverPopup;
    PausePopup pausePopup;
    String selectedPage = "homePage";
    Game game;
    String musicPath = "data/music/tetris.wav";
    MusicChoosePage musicChoosePage;
    MusicPlayer musicPlayer;

    public GeneralScreen() {
        setFocusable(true);

        game = new Game(new Grid(10, 25));
        musicPlayer = new MusicPlayer(musicPath);

        musicChoosePage = new MusicChoosePage(() -> {
            musicPlayer = new MusicPlayer(musicChoosePage.getPath());
            setPage("homePage");
        });

        homePage = new HomePage(
                new Button("Jouer", () -> setPage("tetrisView")),
                new Button("Choisir la musique", () -> setPage("musicChoosePage"))
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

        tetrisView = new TetrisView(game, changeToGameOver, changeToPause);
        tetrisView.start();
        game.addGridObserver(tetrisView);

        gameOverPopup = new GameOverPopup(game,
                new Button("Retour au menu", () -> setPage("homePage")),
                new Button("Rejouer", () -> setPage("tetrisView"))
        );

        pausePopup = new PausePopup(game,
                new Button("Revenir au jeu", () -> {
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
        System.out.println("Change page to " + page);
        switch (page) {
            case "homePage" -> {
                this.getContentPane().removeAll();
                musicPlayer.stop();
                add(homePage);
            }
            case "tetrisView" -> {
                this.getContentPane().removeAll();
                if (previousPage.equals("gameOver") || previousPage.equals("homePage")) {
                    musicPlayer.stop();
                    if (previousPage.equals("homePage")) {
                        musicPlayer.reset();
                    }
                    game.reset();
                }
                add(tetrisView);
                musicPlayer.play();
                tetrisView.updateBoard();
                tetrisView.updateNextPiece();
            }
            case "gameOver" -> {
                this.getContentPane().removeAll();
                add(gameOverPopup);
            }
            case "pause" -> {
                this.getContentPane().removeAll();
                musicPlayer.stop();
                add(pausePopup);
            }
            case "musicChoosePage" -> {
                this.getContentPane().removeAll();
                add(musicChoosePage);
            }
        }
        selectedPage = page;
        this.repaint();
        this.revalidate();
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
