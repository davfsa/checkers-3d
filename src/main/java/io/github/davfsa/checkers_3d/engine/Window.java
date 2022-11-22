package io.github.davfsa.checkers_3d.engine;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;
import org.tinylog.Logger;

public class Window {
    private final long windowHandle;
    private final Runnable resizeFunc;
    private final MouseInputHandler mouseInputHandler;
    private final InputHandler inputHandler;
    private int height;
    private int width;

    public Window(String title, Runnable resizeFunc) {
        this.resizeFunc = resizeFunc;

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        // MSAA - Antialiasing
        glfwWindowHint(GLFW_SAMPLES, 16);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        // Hopefully this will prevent incompatibilities
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);

        // Full screen
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        this.width = vidMode.width();
        this.height = vidMode.height();

        // Create the window
        this.windowHandle = glfwCreateWindow(this.width, this.height, title, NULL, NULL);
        if (this.windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Register Callbacks
        glfwSetFramebufferSizeCallback(this.windowHandle, (window, w, h) -> resized(w, h));
        glfwSetErrorCallback((errorCode, msgPtr) ->
            Logger.error("Error code [{}], msg [{}]", errorCode, MemoryUtil.memUTF8(msgPtr))
        );
        glfwSetKeyCallback(this.windowHandle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        // Show screen
        glfwMakeContextCurrent(this.windowHandle);
        glfwShowWindow(this.windowHandle);

        glfwSwapInterval(0); // Disable V-Sync, we do it manually by limiting the frame-rate

        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetFramebufferSize(this.windowHandle, arrWidth, arrHeight);
        this.width = arrWidth[0];
        this.height = arrHeight[0];

        // Input handlers
        this.inputHandler = new InputHandler(this.windowHandle);
        this.mouseInputHandler = new MouseInputHandler(this.windowHandle);
    }

    public void cleanup() {
        glfwFreeCallbacks(this.windowHandle);
        glfwDestroyWindow(this.windowHandle);
        glfwTerminate();
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null) {
            callback.free();
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public MouseInputHandler getMouseInputHandler() {
        return mouseInputHandler;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public void pollEvents() {
        glfwPollEvents();
        mouseInputHandler.input(this);
    }

    protected void resized(int width, int height) {
        this.width = width;
        this.height = height;
        try {
            resizeFunc.run();
        } catch (Exception e) {
            Logger.error("Error calling resize callback", e);
        }
    }

    public void update() {
        glfwSwapBuffers(windowHandle);
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }
}
