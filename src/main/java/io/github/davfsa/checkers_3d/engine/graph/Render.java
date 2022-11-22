package io.github.davfsa.checkers_3d.engine.graph;

import static org.lwjgl.opengl.GL11.*;

import io.github.davfsa.checkers_3d.engine.Window;
import io.github.davfsa.checkers_3d.engine.scene.Scene;
import org.lwjgl.opengl.GL;


public class Render {
    private final SceneRender sceneRender;
    private final GuiRender guiRender;

    public Render(Window window) {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        this.sceneRender = new SceneRender();
        this.guiRender = new GuiRender(window);
    }

    public void cleanup() {
        sceneRender.cleanup();
        guiRender.cleanup();
    }

    public void render(Window window, Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, window.getWidth(), window.getHeight());

        sceneRender.render(scene);
        guiRender.render(scene);
    }

    public void resize(int width, int height) {
        guiRender.resize(width, height);
    }
}
