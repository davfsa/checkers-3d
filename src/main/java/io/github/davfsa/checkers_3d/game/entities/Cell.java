package io.github.davfsa.checkers_3d.game.entities;

import io.github.davfsa.checkers_3d.engine.interfaces.IInteractable;
import io.github.davfsa.checkers_3d.engine.scene.Entity;
import io.github.davfsa.checkers_3d.engine.scene.Scene;
import io.github.davfsa.checkers_3d.game.enums.CellState;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Cell extends Entity implements IInteractable {
    public static final Vector3f BUFF_COLOUR = new Vector3f(1.0f, 0.93725f, 0.73725f);
    public static final Vector3f GREEN_COLOUR = new Vector3f(0.32941f, 0.53725f, 0.1098f);
    public static final Vector3f SELECTABLE_COLOUR = new Vector3f(0.8f, 0.8f, 0f);
    public static final Vector3f SELECTED_COLOUR = new Vector3f(0f, 0.9f, 0f);
    public static final Vector3f HUFFING_COLOUR = new Vector3f(0.9f, 0f, 0f);

    private static final String MODEL_ID = "cell";
    private static final String MODEL_PATH = "src/main/resources/models/cube.obj";
    private final Vector3f originalColour;
    private final Vector2f boardPosition;
    private Piece content;
    private CellState cellState;
    private PieceMovement possibleMovement;

    public Cell(Scene scene, String identifier, Vector3f position, Vector2f boardPosition, Vector3f colour) {
        super(
            scene,
            "cell-" + identifier,
            MODEL_ID,
            MODEL_PATH,
            position,
            colour
        );
        this.boardPosition = boardPosition;
        originalColour = new Vector3f(colour);
        content = null;
        cellState = CellState.NONE;
    }

    public Piece getContent() {
        return content;
    }

    public void setContent(Piece content) {
        this.content = content;
    }

    public CellState getCellState() {
        return cellState;
    }

    public void setCellState(CellState state) {
        possibleMovement = null;
        cellState = state;

        colour.set(switch (state) {
            case SELECTED, POSSIBLE_MOVEMENT -> SELECTED_COLOUR;
            case SELECTABLE -> SELECTABLE_COLOUR;
            case HUFFING -> HUFFING_COLOUR;
            default -> originalColour;
        });
    }

    public Vector2f getBoardPosition() {
        return boardPosition;
    }

    public void setCellState(CellState state, PieceMovement possibleMovement) {
        setCellState(state);
        this.possibleMovement = possibleMovement;
    }

    public PieceMovement getPossibleMovement() {
        return possibleMovement;
    }
}
