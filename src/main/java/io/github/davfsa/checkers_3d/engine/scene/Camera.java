package io.github.davfsa.checkers_3d.engine.scene;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private final float MAX_X_ROTATION = (float) Math.toRadians(60);
    private final float MIN_X_ROTATION = (float) Math.toRadians(0);
    private final Vector3f position;
    private final Vector2f rotation;
    private final Matrix4f viewMatrix;

    public Camera() {
        this.position = new Vector3f();
        this.viewMatrix = new Matrix4f();
        this.rotation = new Vector2f();
    }

    public void addRotation(float x, float y) {
        // Clamp rotation in the X-axis
        rotation.x = Math.min(MAX_X_ROTATION, rotation.x);
        rotation.x = Math.max(MIN_X_ROTATION, rotation.x);

        rotation.add(x, y);
        recalculate();
    }


    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    private void recalculate() {
        viewMatrix.identity()
            .lookAt(new Vector3f(position.x, position.y, position.z), new Vector3f(0, 0, 0), new Vector3f(0, 0, 1))
            .rotateX(rotation.x)
            .rotateY(rotation.y);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        recalculate();
    }

    public Vector2f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y) {
        rotation.set(x, y);
        recalculate();
    }
}
