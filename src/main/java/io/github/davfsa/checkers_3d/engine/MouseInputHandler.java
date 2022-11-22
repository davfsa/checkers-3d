package io.github.davfsa.checkers_3d.engine;

import static org.lwjgl.glfw.GLFW.*;

import io.github.davfsa.checkers_3d.engine.interfaces.IInteractable;
import io.github.davfsa.checkers_3d.engine.scene.Camera;
import io.github.davfsa.checkers_3d.engine.scene.Entity;
import io.github.davfsa.checkers_3d.engine.scene.Scene;
import org.joml.*;

public class MouseInputHandler {
    private final Vector2f currentPos;
    private final Vector2f displVec;
    private final Vector2f previousPos;
    private boolean inWindow;
    private int leftButton;
    private int rightButton;

    public MouseInputHandler(long windowHandle) {
        this.previousPos = new Vector2f(-1, -1);
        this.currentPos = new Vector2f();
        this.displVec = new Vector2f();
        this.inWindow = false;
        this.leftButton = GLFW_RELEASE;
        this.rightButton = GLFW_RELEASE;

        glfwSetCursorPosCallback(windowHandle, (handle, xpos, ypos) -> {
            this.currentPos.x = (float) xpos;
            this.currentPos.y = (float) ypos;
        });
        glfwSetCursorEnterCallback(windowHandle, (handle, entered) -> this.inWindow = entered);
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public Vector2f getCurrentPos() {
        return currentPos;
    }

    public void input(Window window) {
        // Displacement
        displVec.x = 0;
        displVec.y = 0;
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double deltaX = currentPos.x - previousPos.x;
            double deltaY = currentPos.y - previousPos.y;

            if (deltaX != 0) {
                displVec.y = (float) deltaX;
            }
            if (deltaY != 0) {
                displVec.x = (float) deltaY;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;

        // Mouse buttons
        //
        // GLFW doesn't expose whether a key is pressed or held unless you subscribe to the
        // key callback. We can get around this by checking if the key has been in the pressed
        // state for 2 consecutive calls to this method, then we will treat it as being held.
        long windowHandle = window.getWindowHandle();
        int action;
        action = glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_RIGHT);
        if (action == GLFW_PRESS) {
            if (rightButton != GLFW_REPEAT) {
                rightButton = rightButton == GLFW_PRESS ? GLFW_REPEAT : GLFW_PRESS;
            }
        } else {
            rightButton = action;
        }

        action = glfwGetMouseButton(windowHandle, GLFW_MOUSE_BUTTON_LEFT);
        if (action == GLFW_PRESS) {
            if (leftButton != GLFW_REPEAT) {
                leftButton = leftButton == GLFW_PRESS ? GLFW_REPEAT : GLFW_PRESS;
            }
        } else {
            leftButton = action;
        }
    }

    public boolean isLeftButtonPressed() {
        return leftButton == GLFW_PRESS;
    }

    public boolean isLeftButtonHeld() {
        return leftButton == GLFW_REPEAT || leftButton == GLFW_PRESS;
    }

    public boolean isRightButtonHeld() {
        return rightButton == GLFW_REPEAT || rightButton == GLFW_PRESS;
    }

    // Suppressed warning is due to a cast to T being falsely considered as unsafe at runtime
    @SuppressWarnings("unchecked")
    public <T extends IInteractable> T castRay(Window window, Camera camera, Scene scene, Class<T> entityType) {
        T entityToReturn = null;
        float closestDistance = Float.POSITIVE_INFINITY;

        Matrix4f viewMatrix = camera.getViewMatrix();
        Vector2f cameraRotation = camera.getRotation();
        Vector3f cameraPosition = new Vector3f(camera.getPosition())
            .rotateX(-cameraRotation.x)
            .rotateY(-cameraRotation.y);

        // Normalize xy position in range [-1, 1]
        float x = (2 * currentPos.x) / (float) window.getWidth() - 1.0f;
        float y = 1.0f - (2 * currentPos.y) / (float) window.getHeight();

        Vector4f tmpVec = new Vector4f(x, y, -1.0f, 1.0f);

        // Projection
        Matrix4f invProjectionMatrix = new Matrix4f(scene.getProjection().getProjMatrix()).invert();
        tmpVec.mul(invProjectionMatrix);
        tmpVec.z = -1.0f;
        tmpVec.w = 0.0f;

        // Camera view
        Matrix4f invViewMatrix = new Matrix4f(viewMatrix).invert();
        tmpVec.mul(invViewMatrix);

        // And we are good to go!
        Vector3f dir = new Vector3f(tmpVec.x, tmpVec.y, tmpVec.z);
        Vector3f min = new Vector3f();
        Vector3f max = new Vector3f();
        Vector2f nearFar = new Vector2f();
        for (Entity entity : scene.getEntityMap().values()) {
            if (!(entity instanceof IInteractable)) {
                continue;
            }

            Vector3f position = entity.getPosition();
            // We have our measurements in .5
            Vector3f scale = new Vector3f(entity.getScale()).mul(0.5f);

            min.set(position);
            max.set(position);
            min.add(-scale.x, -scale.y, -scale.z);
            max.add(scale.x, scale.y, scale.z);

            if (Intersectionf.intersectRayAab(cameraPosition, dir, min, max, nearFar) && nearFar.x < closestDistance) {
                closestDistance = nearFar.x;
                entityToReturn = entityType.isInstance(entity) ? (T) entity : null;
            }
        }

        return entityToReturn;
    }
}
