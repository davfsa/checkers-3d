package io.github.davfsa.checkers_3d.game.entities;

import io.github.davfsa.checkers_3d.game.enums.CellState;

public class PieceMovement {
    private final Piece piece;
    private final Cell moveToCell;
    private final Cell eatenCell;
    private final boolean crowns;

    public PieceMovement(Piece piece, Cell moveToCell, Cell eatenCell, boolean crowns) {
        this.piece = piece;
        this.moveToCell = moveToCell;
        this.eatenCell = eatenCell;
        this.crowns = crowns;
    }

    public PieceMovement(Piece piece) {
        this(piece, null, null, false);
    }

    public void updateCellState() {
        moveToCell.setCellState(CellState.POSSIBLE_MOVEMENT, this);
    }

    public boolean eats() {
        return eatenCell != null;
    }

    public boolean crowns() {
        return crowns;
    }

    public Piece getPiece() {
        return piece;
    }

    public Cell getMoveToCell() {
        return moveToCell;
    }

    public Cell eatenCell() {
        return eatenCell;
    }
}
