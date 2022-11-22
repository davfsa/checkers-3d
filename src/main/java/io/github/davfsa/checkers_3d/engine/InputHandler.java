package io.github.davfsa.checkers_3d.engine;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;
import java.util.Map;

public class InputHandler {
    private final Map<Integer, Integer> inputs;
    private final long windowHandle;

    public InputHandler(long windowHandle) {
        this.inputs = new HashMap<>();
        this.windowHandle = windowHandle;
    }

    private void updateMapping(int keyCode) {
        int status = glfwGetKey(windowHandle, keyCode);

        // GLFW doesn't expose whether a key is pressed or held unless you subscribe to the
        // key callback. We can get around this by keeping a mapping of what keys are pressed
        // and which ones aren't. If the key has been in the pressed state for 2 consecutive
        // calls to this method, then we will treat it as being held.
        if (status == GLFW_RELEASE) {
            inputs.remove(keyCode);
        } else {
            inputs.put(keyCode, inputs.containsKey(keyCode) ? GLFW_REPEAT : GLFW_PRESS);
        }
    }

    public boolean isPressed(int keyCode) {
        updateMapping(keyCode);
        return inputs.getOrDefault(keyCode, GLFW_RELEASE) == GLFW_PRESS;
    }
}
