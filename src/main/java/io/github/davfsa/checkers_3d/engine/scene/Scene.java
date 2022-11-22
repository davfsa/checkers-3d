package io.github.davfsa.checkers_3d.engine.scene;

import io.github.davfsa.checkers_3d.engine.graph.Model;
import io.github.davfsa.checkers_3d.engine.interfaces.IGuiLogic;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private final Map<String, Model> modelMap;
    private final Map<String, Entity> entityMap;
    private final Projection projection;
    private final Camera camera;
    private IGuiLogic guiLogic;

    public Scene(int width, int height) {
        this.modelMap = new HashMap<>();
        this.entityMap = new HashMap<>();
        this.projection = new Projection(width, height);
        this.camera = new Camera();
    }

    public void resize(int width, int height) {
        projection.updateProjMatrix(width, height);

    }

    public void cleanup() {
        modelMap.values().forEach(Model::cleanup);
        modelMap.clear();
        entityMap.clear();
    }

    public void addEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        if (model == null) {
            throw new RuntimeException("Could not find model [" + modelId + "]");
        }

        String entityId = entity.getId();
        if (entityMap.containsKey(entityId)) {
            throw new RuntimeException("Entity with same id already registered [" + entityId + "]");
        }

        entityMap.put(entityId, entity);
        model.getEntitiesList().add(entityId);
    }

    public void removeEntity(String entityId) {
        Entity entity = entityMap.get(entityId);

        entityMap.remove(entityId);

        Model model = modelMap.get(entity.getModelId());
        List<String> entitiesList = model.getEntitiesList();
        model.getEntitiesList().remove(entityId);

        if (entitiesList.isEmpty()) {
            modelMap.remove(model.getId());
            model.cleanup();
        }
    }

    public void addModel(Model model) {
        modelMap.put(model.getId(), model);
    }

    public Projection getProjection() {
        return projection;
    }

    public Map<String, Model> getModelMap() {
        return modelMap;
    }

    public Map<String, Entity> getEntityMap() {
        return entityMap;
    }

    public Camera getCamera() {
        return camera;
    }

    public IGuiLogic getGuiLogic() {
        return guiLogic;
    }

    public void setGuiLogic(IGuiLogic guiLogic) {
        this.guiLogic = guiLogic;
    }
}
