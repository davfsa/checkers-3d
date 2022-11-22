package io.github.davfsa.checkers_3d.game.entities;

import io.github.davfsa.checkers_3d.engine.interfaces.IInteractable;
import io.github.davfsa.checkers_3d.engine.scene.Entity;
import io.github.davfsa.checkers_3d.engine.scene.Scene;
import org.joml.Vector3f;

public class Border extends Entity implements IInteractable {
    private static final String MODEL_ID = "border";
    private static final String MODEL_PATH = "src/main/resources/models/cube.obj";
    private static final Vector3f BROWN_COLOUR = new Vector3f(0.4882f, 0.2941f, 0f);

    public Border(Scene scene, Vector3f position) {
        super(scene, "border", MODEL_ID, MODEL_PATH, position, BROWN_COLOUR);
    }
}
