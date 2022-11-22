package io.github.davfsa.checkers_3d.engine;

import io.github.davfsa.checkers_3d.engine.graph.Render;
import io.github.davfsa.checkers_3d.engine.interfaces.IAppLogic;
import io.github.davfsa.checkers_3d.engine.interfaces.IGuiLogic;
import io.github.davfsa.checkers_3d.engine.scene.Scene;


public class Engine {
    private static final int TARGET_FPS = 60;
    private final IAppLogic appLogic;
    private final Window window;
    private final Render render;
    private final Scene scene;
    private boolean running;

    public Engine(String windowTitle, IAppLogic appLogic) {
        this.appLogic = appLogic;
        this.window = new Window(windowTitle, this::resize);

        this.render = new Render(window);
        this.scene = new Scene(window.getWidth(), window.getHeight());

        appLogic.init(scene);
        this.running = true;
    }

    private void cleanup() {
        render.cleanup();
        scene.cleanup();
        window.cleanup();
    }

    private void resize() {
        int width = window.getWidth();
        int height = window.getHeight();

        scene.resize(width, height);
        render.resize(width, height);
    }

    private void run() {
        IGuiLogic iGuiInstance = scene.getGuiLogic();
        long initialTime = System.currentTimeMillis();
        float timeR = 1000.0f / TARGET_FPS;
        float deltaFps = 0;

        while (running && !window.windowShouldClose()) {
            long now = System.currentTimeMillis();
            deltaFps += (now - initialTime) / timeR;

            // Only draw a frame if it is time.
            // For a 60fps, this will be called once every ~16.66ms
            if (deltaFps >= 1) {
                window.pollEvents();
                boolean mouseInputConsumed = iGuiInstance != null && iGuiInstance.handleGuiInput(window);
                appLogic.update(window, scene, now - initialTime, mouseInputConsumed);

                render.render(window, scene);
                deltaFps--;
                window.update();
            }

            initialTime = now;
        }

        cleanup();
    }

    public void start() {
        running = true;
        run();
    }
}
