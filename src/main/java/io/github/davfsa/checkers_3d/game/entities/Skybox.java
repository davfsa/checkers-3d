package io.github.davfsa.checkers_3d.game.entities;

import io.github.davfsa.checkers_3d.engine.scene.Entity;
import io.github.davfsa.checkers_3d.engine.scene.Scene;
import org.joml.Vector3f;

public class Skybox extends Entity {
    private static final String MODEL_ID = "skybox";
    private static final String MODEL_PATH = "src/main/resources/models/skybox.obj";
    private static final Vector3f BLUE_COLOUR = new Vector3f(0f, 0.5450f, 0.7686f);

    public Skybox(Scene scene, float scale) {
        super(scene, "skybox", MODEL_ID, MODEL_PATH, new Vector3f(0f, 0f, 0f), BLUE_COLOUR);
        setScale(scale);
    }
}
