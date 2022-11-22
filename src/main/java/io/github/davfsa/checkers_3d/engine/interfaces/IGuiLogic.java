package io.github.davfsa.checkers_3d.engine.interfaces;

import io.github.davfsa.checkers_3d.engine.Window;
import io.github.davfsa.checkers_3d.engine.scene.Scene;

public interface IGuiLogic {

    void drawGui(Scene scene);

    boolean handleGuiInput(Window window);
}
