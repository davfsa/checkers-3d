package io.github.davfsa.checkers_3d.game;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import io.github.davfsa.checkers_3d.engine.MouseInputHandler;
import io.github.davfsa.checkers_3d.engine.Window;
import io.github.davfsa.checkers_3d.engine.interfaces.IGuiLogic;
import io.github.davfsa.checkers_3d.engine.scene.Scene;
import io.github.davfsa.checkers_3d.game.enums.GameMode;
import io.github.davfsa.checkers_3d.game.enums.GameScreen;
import java.util.Arrays;
import org.joml.Vector2f;

public class Gui implements IGuiLogic {
    private final GameState gameState;
    private final ImInt selectedGameMode;
    private final int[] selectedBoardSize;

    public Gui(GameState gameState) {
        this.gameState = gameState;
        this.selectedGameMode = new ImInt();
        this.selectedBoardSize = new int[]{Game.DEFAULT_BOARD_SIZE};
    }

    @Override
    public boolean handleGuiInput(Window window) {
        ImGuiIO imGuiIO = ImGui.getIO();
        MouseInputHandler mouseInput = window.getMouseInputHandler();
        Vector2f mousePos = mouseInput.getCurrentPos();

        // Set inputs
        imGuiIO.setMousePos(mousePos.x, mousePos.y);
        imGuiIO.setMouseDown(0, mouseInput.isLeftButtonHeld());

        return imGuiIO.getWantCaptureMouse();
    }

    @Override
    public void drawGui(Scene scene) {
        setupNextWindow();

        switch (gameState.getGameScreen()) {
            case MAIN_MENU -> addMainMenu(scene);
            case PLAYER_1_WIN -> addEndGame(scene, "Player 1 wins!");
            case PLAYER_2_WIN -> addEndGame(scene, "Player 2 wins!");
            case TIE -> addEndGame(scene, "Tie!");
        }
    }

    private void setupNextWindow() {
        ImGuiIO imGuiIO = ImGui.getIO();
        ImVec2 displaySize = imGuiIO.getDisplaySize();
        ImGui.setNextWindowPos(displaySize.x / 2f, displaySize.y / 10f, ImGuiCond.Always, 0.5f, 0.5f);
        ImGui.setNextWindowSize(400f, 150f);

        ImGuiStyle style = ImGui.getStyle();
        style.setWindowTitleAlign(0.5f, 0.5f);
        style.setWindowRounding(12);
    }

    private void centerNextItem(String label) {
        ImGuiStyle style = ImGui.getStyle();
        // Temporary workaround for https://github.com/SpaiR/imgui-java/pull/143
        // We need to create an instance of ImGui before we can call the method
        float size = new ImGui().calcTextSize(label).x + style.getFramePaddingX() * 2.0f;
        float avail = ImGui.getContentRegionAvail().x;

        // Center the button
        float off = (avail - size) / 2f;
        if (off > 0.0f) {
            ImGui.setCursorPosX(ImGui.getCursorPosX() + off);
        }
    }

    private void addMainMenu(Scene scene) {
        int windowFlags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse;

        if (ImGui.begin("Checkers!", windowFlags)) {
            // Mode select
            String[] gameModes = Arrays.stream(GameMode.values())
                .map(String::valueOf)
                .toArray(String[]::new);
            ImGui.combo("Game mode", selectedGameMode, gameModes);

            // Board size
            ImGui.sliderInt("Board size", selectedBoardSize, 8, 20);

            ImGui.newLine();
            ImGui.newLine();

            // Start button
            String label = "Start!";
            centerNextItem(label);
            if (ImGui.button(label)) {
                GameMode gameMode = GameMode.values()[selectedGameMode.get()];

                gameState.setGameScreen(GameScreen.IN_PROCESS);
                gameState.setGameMode(gameMode);

                gameState.getGame().setupBoard(scene, selectedBoardSize[0]);
            }

        }
        ImGui.end();
    }

    private void addEndGame(Scene scene, String text) {
        int windowFlags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse;

        if (ImGui.begin("End of game!", windowFlags)) {
            ImGui.newLine();
            ImGui.newLine();
            centerNextItem(text);
            ImGui.text(text);

            ImGui.newLine();
            ImGui.newLine();

            String label = "Again?";
            centerNextItem(label);
            if (ImGui.button(label)) {
                gameState.setGameScreen(GameScreen.MAIN_MENU);
                gameState.getGame().setupBoard(scene, Game.DEFAULT_BOARD_SIZE);
            }
        }
        ImGui.end();
    }
}
