package io.github.davfsa.checkers_3d.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import io.github.davfsa.checkers_3d.engine.InputHandler;
import io.github.davfsa.checkers_3d.engine.MouseInputHandler;
import io.github.davfsa.checkers_3d.engine.Window;
import io.github.davfsa.checkers_3d.engine.interfaces.IAppLogic;
import io.github.davfsa.checkers_3d.engine.scene.Camera;
import io.github.davfsa.checkers_3d.engine.scene.Scene;
import io.github.davfsa.checkers_3d.game.entities.Cell;
import io.github.davfsa.checkers_3d.game.entities.Skybox;
import io.github.davfsa.checkers_3d.game.enums.GameScreen;
import org.joml.Vector2f;

public class Game implements IAppLogic {
    public static final int DEFAULT_BOARD_SIZE = 8;
    private static final float MOUSE_SENSITIVITY = 0.05f;
    private Board board;
    private boolean wireframe;
    private GameState gameState;

    public void init(Scene scene) {
        this.wireframe = false;
        this.gameState = new GameState(this);

        scene.setGuiLogic(new Gui(this.gameState));

        setupBoard(scene, DEFAULT_BOARD_SIZE);
    }

    public void setupBoard(Scene scene, int boardSize) {
        boolean demo = gameState.getGameScreen() == GameScreen.MAIN_MENU;

        scene.cleanup();

        Camera camera = scene.getCamera();
        camera.setPosition(0, boardSize + 3, 0);
        camera.setRotation((float) Math.toRadians(40), 0);

        board = new Board(scene, gameState, boardSize, demo);
        new Skybox(scene, boardSize * 10);
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis, boolean mouseInputConsumed) {
        if (gameState.getGameScreen() == GameScreen.MAIN_MENU) {
            rotateCamera(scene, diffTimeMillis);
        } else {
            handleInput(window, scene, mouseInputConsumed);
        }
    }

    private void handleInput(Window window, Scene scene, boolean mouseInputConsumed) {
        Camera camera = scene.getCamera();
        MouseInputHandler mouseInputHandler = window.getMouseInputHandler();
        InputHandler inputHandler = window.getInputHandler();

        // Mouse
        if (mouseInputHandler.isLeftButtonPressed() && !mouseInputConsumed) {
            Cell clickedCell = mouseInputHandler.castRay(window, camera, scene, Cell.class);
            board.handleCellClick(clickedCell);
        }

        if (mouseInputHandler.isRightButtonHeld() && !mouseInputConsumed) {
            Vector2f displacementVec = mouseInputHandler.getDisplVec();

            camera.addRotation(
                -(float) Math.toRadians(displacementVec.x * MOUSE_SENSITIVITY),
                (float) Math.toRadians(displacementVec.y * MOUSE_SENSITIVITY)
            );
        }

        // Keyboard
        if (inputHandler.isPressed(GLFW_KEY_W)) {
            wireframe = !wireframe;
            glPolygonMode(GL_FRONT_AND_BACK, wireframe ? GL_LINE : GL_FILL);
        }

        if (inputHandler.isPressed(GLFW_KEY_0) || inputHandler.isPressed(GLFW_KEY_KP_0)) {
            camera.setRotation(0, 0);
        }
    }

    private void rotateCamera(Scene scene, long diffTimeMillis) {
        Camera camera = scene.getCamera();
        camera.addRotation(0, (float) Math.toRadians(0.3) * diffTimeMillis);
    }
}
