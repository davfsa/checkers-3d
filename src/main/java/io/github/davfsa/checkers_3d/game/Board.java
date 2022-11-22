package io.github.davfsa.checkers_3d.game;

import io.github.davfsa.checkers_3d.engine.scene.Scene;
import io.github.davfsa.checkers_3d.game.entities.Border;
import io.github.davfsa.checkers_3d.game.entities.Cell;
import io.github.davfsa.checkers_3d.game.entities.Piece;
import io.github.davfsa.checkers_3d.game.entities.PieceMovement;
import io.github.davfsa.checkers_3d.game.enums.CellState;
import io.github.davfsa.checkers_3d.game.enums.GameMode;
import io.github.davfsa.checkers_3d.game.enums.GameScreen;
import io.github.davfsa.checkers_3d.game.enums.Player;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Board {
    private final Cell[][] board;
    private final List<Piece> pieces;
    private final HashMap<Piece, List<PieceMovement>> possibleMovements;
    private final Scene scene;
    private final GameState gameState;
    private Piece selectedPiece;
    private Player playerTurn;
    private int turnsWithNoMovement;

    public Board(Scene scene, GameState gameState, int boardSize, boolean demo) {
        this.scene = scene;
        this.gameState = gameState;
        this.board = new Cell[boardSize][boardSize];
        this.pieces = new ArrayList<>();
        this.possibleMovements = new HashMap<>();
        this.selectedPiece = null;
        this.turnsWithNoMovement = 0;
        this.playerTurn = Player.PLAYER_1;

        // Board
        float halfBoardSize = (boardSize - 1) / 2.0f;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boolean isBuff = (i + j) % 2 == 0;

                // Create cell
                String cellIdentifier = i + "-" + j;
                Vector3f cellPosition = new Vector3f(i - halfBoardSize, 0f, j - halfBoardSize);
                Vector3f cellColour = new Vector3f(isBuff ? Cell.BUFF_COLOUR : Cell.GREEN_COLOUR);
                Cell cell = new Cell(scene, cellIdentifier, cellPosition, new Vector2f(i, j), cellColour);
                this.board[i][j] = cell;

                // (Maybe) place pieces
                if (!isBuff) {
                    if (j < (int) halfBoardSize) {
                        this.pieces.add(new Piece(scene, String.valueOf(this.pieces.size()), Player.PLAYER_1, cell));
                    } else if (j >= boardSize - (int) halfBoardSize) {
                        this.pieces.add(new Piece(scene, String.valueOf(this.pieces.size()), Player.PLAYER_2, cell));
                    }
                }
            }
        }

        // Border
        Border border = new Border(scene, new Vector3f(0, -0.5f, 0));
        border.setScale(new Vector3f(boardSize + 1.7f, 1, boardSize + 1.7f));

        if (!demo) {
            // Start it off!
            calculatePossibleMovements();
            updateCellsState();
        }
    }

    private boolean enabledForGameMode(GameMode gameMode) {
        return gameState.getGameMode().ordinal() >= gameMode.ordinal();
    }

    public void handleCellClick(Cell cell) {
        if (gameState.getGameScreen() != GameScreen.IN_PROCESS || cell == null) {
            return;
        }

        selectedPiece = null;

        switch (cell.getCellState()) {
            case HUFFING -> {
                Piece huffedPiece = cell.getContent();

                huffedPiece.cleanup(scene);
                pieces.remove(huffedPiece);

                calculatePossibleMovements();
                checkGameState();
            }

            case POSSIBLE_MOVEMENT -> {
                PieceMovement possibleMovement = cell.getPossibleMovement();
                Cell eatenCell = possibleMovement.eatenCell();
                Piece piece = possibleMovement.getPiece();
                boolean canEatAgain = false;

                piece.setCell(possibleMovement.getMoveToCell());

                if (eatenCell != null) {
                    Piece eatenPiece = eatenCell.getContent();

                    eatenPiece.cleanup(scene);
                    pieces.remove(eatenPiece);

                    calculatePossibleMovements();
                    for (List<PieceMovement> movements : possibleMovements.values()) {
                        for (PieceMovement movement : movements) {
                            if (movement.eatenCell() != null) {
                                canEatAgain = true;
                                break;
                            }
                        }
                    }
                }

                if (enabledForGameMode(GameMode.INTERMEDIATE) && possibleMovement.crowns()) {
                    piece.crown(scene);
                    nextTurn();
                } else if (!canEatAgain) {
                    nextTurn();
                }

                checkGameState();
            }

            default -> {
                Piece piece = cell.getContent();
                selectedPiece = possibleMovements.containsKey(piece) ? piece : null;
            }
        }

        updateCellsState();
    }

    private void calculatePossibleMovements() {
        Stream<PieceMovement> tempPossibleMovementsStream = pieces.stream()
            .filter(piece -> piece.getPlayer() == playerTurn)
            .flatMap(piece -> piece.getValidMovements(board).stream());

        if (enabledForGameMode(GameMode.ADVANCED)) {
            tempPossibleMovementsStream = Stream.concat(
                tempPossibleMovementsStream,
                possibleMovements.values()
                    .stream()
                    .flatMap(List::stream)
                    .filter(pieceMovement -> pieceMovement.getPiece().getPlayer() != playerTurn)
                    .filter(PieceMovement::eats)
                    .map(pieceMovement -> new PieceMovement(pieceMovement.getPiece()))
            );
        } else {
            AtomicBoolean mustEat = new AtomicBoolean(false);

            tempPossibleMovementsStream = tempPossibleMovementsStream
                .sorted(Comparator.comparing(PieceMovement::eats).reversed())
                .peek(pieceMovement -> mustEat.compareAndSet(false, pieceMovement.eats()))
                .takeWhile(pieceMovement -> !mustEat.get() || pieceMovement.eats());
        }

        Map<Piece, List<PieceMovement>> tempPossibleMovements = tempPossibleMovementsStream.collect(
            Collectors.groupingBy(PieceMovement::getPiece)
        );

        possibleMovements.clear();
        possibleMovements.putAll(tempPossibleMovements);
    }

    private void checkGameState() {
        // Case 1: Run out of pieces
        Map<Player, List<Piece>> playerPieces = pieces.stream()
            .collect(Collectors.groupingBy(Piece::getPlayer));

        if (playerPieces.size() < 2) {
            gameState.setGameScreen(playerPieces.containsKey(Player.PLAYER_1)
                ? GameScreen.PLAYER_1_WIN : GameScreen.PLAYER_2_WIN);
            return;
        }

        // Case 2: No more movements
        if (possibleMovements.isEmpty()) {
            nextTurn();

            if (possibleMovements.isEmpty()) {
                gameState.setGameScreen(GameScreen.TIE);
                return;
            }

            turnsWithNoMovement++;
        } else {
            turnsWithNoMovement = 0;
        }

        if (turnsWithNoMovement >= 2) {
            gameState.setGameScreen(playerTurn == Player.PLAYER_1
                ? GameScreen.PLAYER_1_WIN : GameScreen.PLAYER_2_WIN);
        }
    }

    private void nextTurn() {
        playerTurn = (playerTurn == Player.PLAYER_1) ? Player.PLAYER_2 : Player.PLAYER_1;
        calculatePossibleMovements();
    }

    private void updateCellsState() {
        // Reset
        for (Cell[] cells : board) {
            for (Cell cell : cells) {
                cell.setCellState(CellState.NONE);
            }
        }

        if (gameState.getGameScreen() != GameScreen.IN_PROCESS) {
            return;
        }

        // Selectable ones
        for (Piece piece : possibleMovements.keySet()) {
            Cell cell = piece.getCell();

            // If the piece is not from the player whose turn it is, it will be a huffing move
            if (piece.getPlayer() == playerTurn) {
                cell.setCellState(CellState.SELECTABLE);
            } else {
                cell.setCellState(CellState.HUFFING);
            }
        }

        // Selected one and its options
        if (selectedPiece != null) {
            selectedPiece.getCell().setCellState(CellState.SELECTED);

            List<PieceMovement> currentPiecePossibleMovements = possibleMovements.get(
                selectedPiece);

            if (currentPiecePossibleMovements != null) {
                for (PieceMovement movement : currentPiecePossibleMovements) {
                    movement.updateCellState();
                }
            }
        }
    }
}
