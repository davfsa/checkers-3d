package io.github.davfsa.checkers_3d.engine.interfaces;

import io.github.davfsa.checkers_3d.engine.Window;
import io.github.davfsa.checkers_3d.engine.scene.Scene;

public interface IAppLogic {
    void init(Scene scene);

    void update(Window window, Scene scene, long diffTimeMillis, boolean mouseInputConsumed);
}
