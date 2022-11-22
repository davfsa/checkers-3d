package io.github.davfsa.checkers_3d.game;

import io.github.davfsa.checkers_3d.engine.Engine;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        Engine engine = new Engine("Checkers!", game);
        engine.start();
    }
}
