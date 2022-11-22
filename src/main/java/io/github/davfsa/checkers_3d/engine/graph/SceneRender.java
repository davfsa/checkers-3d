package io.github.davfsa.checkers_3d.engine.graph;


import static org.lwjgl.opengl.GL30.*;

import io.github.davfsa.checkers_3d.engine.scene.Entity;
import io.github.davfsa.checkers_3d.engine.scene.Scene;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SceneRender {
    private final ShaderProgram shaderProgram;
    private UniformsMap uniformsMap;

    public SceneRender() {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(
            new ShaderProgram.ShaderModuleData("src/main/resources/shaders/scene.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(
            new ShaderProgram.ShaderModuleData("src/main/resources/shaders/scene.frag", GL_FRAGMENT_SHADER));

        this.shaderProgram = new ShaderProgram(shaderModuleDataList);
        createUniforms();
    }

    private void createUniforms() {
        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniform("projectionMatrix");
        uniformsMap.createUniform("modelMatrix");
        uniformsMap.createUniform("viewMatrix");
        uniformsMap.createUniform("modelColour");
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    public void render(Scene scene) {
        // Load in the shaders (bind). They will be added to the render pipeline until `unbind` is called
        shaderProgram.bind();

        // Setup uniforms for the shaders
        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
        uniformsMap.setUniform("viewMatrix", scene.getCamera().getViewMatrix());

        Collection<Model> models = scene.getModelMap().values();
        Map<String, Entity> entities = scene.getEntityMap();

        for (Model model : models) {
            List<String> entityIds = model.getEntitiesList();

            glActiveTexture(GL_TEXTURE0);

            for (Mesh mesh : model.getMeshes()) {
                glBindVertexArray(mesh.getVaoId());
                for (String entityId : entityIds) {
                    Entity entity = entities.get(entityId);
                    // Setup model specific uniforms for the shaders
                    uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                    uniformsMap.setUniform("modelColour", entity.getModelColour());

                    // Draw it!
                    glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
                }
            }
        }

        glBindVertexArray(0);

        shaderProgram.unbind();
    }
}
