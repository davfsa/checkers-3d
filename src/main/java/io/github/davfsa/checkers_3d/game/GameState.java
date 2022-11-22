package io.github.davfsa.checkers_3d.game;

import io.github.davfsa.checkers_3d.game.enums.GameMode;
import io.github.davfsa.checkers_3d.game.enums.GameScreen;

public class GameState {
    private final Game game;
    private GameScreen gameScreen;
    private GameMode gameMode;

    public GameState(Game game) {
        this.game = game;
        this.gameScreen = GameScreen.MAIN_MENU;
        this.gameMode = GameMode.BASIC;
    }

    public Game getGame() {
        return game;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }
}
