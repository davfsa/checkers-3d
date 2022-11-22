package io.github.davfsa.checkers_3d.game.entities;

import io.github.davfsa.checkers_3d.engine.scene.Entity;
import io.github.davfsa.checkers_3d.engine.scene.Scene;
import io.github.davfsa.checkers_3d.game.enums.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Piece extends Entity {
    private static final String MODEL_ID = "piece";
    private static final String MODEL_PATH = "src/main/resources/models/piece.obj";
    private static final Vector3f BLACK_COLOUR = new Vector3f(0f, 0f, 0f);
    private static final Vector3f WHITE_COLOUR = new Vector3f(1f, 1f, 1f);
    private final Player player;
    private Cell cell;
    private Crown crown;

    public Piece(Scene scene, String identifier, Player player, Cell cell) {
        super(
            scene,
            "piece-" + identifier + "-" + player,
            MODEL_ID,
            MODEL_PATH,
            new Vector3f(0, 0.5f, 0),
            player == Player.PLAYER_1 ? BLACK_COLOUR : WHITE_COLOUR
        );

        this.player = player;
        this.cell = cell;
        this.crown = null;
        setCell(cell);
    }

    public void cleanup(Scene scene) {
        cell.setContent(null);
        scene.removeEntity(id);

        if (crown != null) {
            scene.removeEntity(crown.getId());
        }
    }

    public void crown(Scene scene) {
        if (crown != null) {
            return;
        }

        Vector3f crownColour = player == Player.PLAYER_1 ? WHITE_COLOUR : BLACK_COLOUR;
        Vector3f crownPosition = new Vector3f(position).add(0, 0.15f, 0);

        crown = new Crown(scene, id, crownPosition, crownColour);
        crown.setScale(new Vector3f(0.5f, 0.5f, 0.5f));
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell.setContent(null);
        cell.setContent(this);
        this.cell = cell;

        Vector3f cellPosition = cell.getPosition();
        setPosition(cellPosition.x, position.y, cellPosition.z);

        if (crown != null) {
            crown.setPosition(cellPosition.x, crown.getPosition().y, cellPosition.z);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public List<PieceMovement> getValidMovements(Cell[][] board) {
        int direction = player == Player.PLAYER_1 ? 1 : -1;
        List<PieceMovement> movements = new ArrayList<>();

        movements.add(checkMovement(board, 1, direction));
        movements.add(checkMovement(board, -1, direction));

        if (crown != null) {
            movements.add(checkMovement(board, 1, -direction));
            movements.add(checkMovement(board, -1, -direction));
        }

        return movements.stream().filter(Objects::nonNull).toList();
    }

    private PieceMovement checkMovement(Cell[][] board, int directionX, int directionY) {
        Vector2f boardPosition = cell.getBoardPosition();
        int possibleCellX = (int) boardPosition.x + directionX;
        int possibleCellY = (int) boardPosition.y + directionY;
        boolean crowns = possibleCellY == board.length - 1 || possibleCellY == 0;

        if (possibleCellX >= board.length || possibleCellX < 0 || possibleCellY >= board.length || possibleCellY < 0) {
            return null;
        }

        Cell cell = board[possibleCellX][possibleCellY];
        Piece cellContent = cell.getContent();

        if (cellContent == null) {
            return new PieceMovement(this, cell, null, crowns);
        }
        if (cellContent.player != player) {
            possibleCellX = possibleCellX + directionX;
            possibleCellY = possibleCellY + directionY;
            crowns = possibleCellY == board.length - 1 || possibleCellY == 0;

            if (possibleCellX >= board.length || possibleCellX < 0 || possibleCellY >= board.length
                || possibleCellY < 0) {
                return null;
            }

            Cell eatenCell = cell;
            cell = board[possibleCellX][possibleCellY];
            if (cell.getContent() == null) {
                return new PieceMovement(this, cell, eatenCell, crowns);
            }
        }
        return null;
    }
}
