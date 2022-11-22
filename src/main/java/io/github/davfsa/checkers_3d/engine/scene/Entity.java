package io.github.davfsa.checkers_3d.engine.scene;

import io.github.davfsa.checkers_3d.engine.graph.Model;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Entity {
    protected final String id;
    protected final String modelId;
    protected final Matrix4f modelMatrix;
    protected final Quaternionf rotation;
    protected final Vector3f position;
    protected final Vector3f colour;
    protected final Vector3f scale;

    public Entity(Scene scene, String id, String modelId, String modelPath, Vector3f position, Vector3f colour) {
        this.id = id;
        this.modelId = modelId;
        this.position = position;
        this.colour = colour;
        this.modelMatrix = new Matrix4f();
        this.rotation = new Quaternionf();
        this.scale = new Vector3f(1f, 1f, 1f);

        if (!scene.getModelMap().containsKey(modelId)) {
            Model model = ModelLoader.loadModel(modelId, modelPath);
            scene.addModel(model);
        }

        scene.addEntity(this);
        updateModelMatrix();
    }

    public String getId() {
        return id;
    }

    public String getModelId() {
        return modelId;
    }

    public Vector3f getModelColour() {
        return colour;
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale.set(scale);
        updateModelMatrix();
    }

    public void setScale(float scale) {
        this.scale.set(scale);
        updateModelMatrix();
    }

    public final void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        updateModelMatrix();
    }

    public void updateModelMatrix() {
        modelMatrix.translationRotateScale(position, rotation, scale);
    }
}
