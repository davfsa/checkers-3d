package io.github.davfsa.checkers_3d.game.entities;

import io.github.davfsa.checkers_3d.engine.scene.Entity;
import io.github.davfsa.checkers_3d.engine.scene.Scene;
import org.joml.Vector3f;

public class Crown extends Entity {
    private static final String MODEL_ID = "crown";
    private static final String MODEL_PATH = "src/main/resources/models/crown.obj";

    public Crown(Scene scene, String identifier, Vector3f position, Vector3f colour) {
        super(scene, identifier + "-crown", MODEL_ID, MODEL_PATH, position, colour);
    }
}
