package chess;

import java.util.*;

public class KnightMoveCalculator implements ChessPieceCalculator {
    @Override
    public List<int[]> possibleEndPositionCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor) {
        List<int[]> possibleEndPositions = new ArrayList<int[]>();

        if (position.getRow() + 2 <= 8 && position.getColumn() + 1 <= 8) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() + 2, position.getColumn() + 1, pieceColor));
        }
        if (position.getRow() + 1 <= 8 && position.getColumn() + 2 <= 8) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() + 1, position.getColumn() + 2, pieceColor));
        }

        if (position.getRow() - 1 >= 1 && position.getColumn() + 2 <= 8) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() - 1, position.getColumn() + 2, pieceColor));
        }
        if (position.getRow() - 2 >= 1 && position.getColumn() + 1 <= 8) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() - 2, position.getColumn() + 1, pieceColor));
        }

        if (position.getRow() - 2 >= 1 && position.getColumn() - 1 >= 1) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() - 2, position.getColumn() - 1, pieceColor));
        }
        if (position.getRow() - 1 >= 1 && position.getColumn() - 2 >= 1) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() - 1, position.getColumn() - 2, pieceColor));
        }

        if (position.getRow() + 1 <= 8 && position.getColumn() - 2 >= 1) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() + 1, position.getColumn() - 2, pieceColor));
        }
        if (position.getRow() + 2 <= 8 && position.getColumn() - 1 >= 1) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() + 2, position.getColumn() - 1, pieceColor));
        }

        return possibleEndPositions;
    }

    private List<int[]> singleMoveHelper(ChessBoard board, int row, int column, ChessGame.TeamColor pieceColor) {
        List<int[]> possibleEndPositions = new ArrayList<int[]>();

        if (board.isPiece(new ChessPosition(row, column))) {
            if (board.isEnemyPiece(new ChessPosition(row, column), pieceColor)) {
                possibleEndPositions.add(new int[]{row, column});
            }
        } else { possibleEndPositions.add(new int[]{row, column}); }

        return possibleEndPositions;
    }
}