package io.github.davfsa.checkers_3d.engine.graph;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private final String id;
    private final List<String> entitiesIdList;
    private final List<Mesh> meshes;

    public Model(String id, List<Mesh> meshes) {
        this.id = id;
        this.meshes = meshes;
        this.entitiesIdList = new ArrayList<>();
    }

    public void cleanup() {
        meshes.forEach(Mesh::cleanup);
    }

    public List<String> getEntitiesList() {
        return entitiesIdList;
    }

    public String getId() {
        return id;
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }
}
