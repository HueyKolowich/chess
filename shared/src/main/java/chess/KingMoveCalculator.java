package chess;

import java.util.*;

public class KingMoveCalculator implements ChessPieceCalculator {
    @Override
    public List<int[]> possibleEndPositionCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor) {
        List<int[]> possibleEndPositions = new ArrayList<int[]>();
        int edgeStatus = board.isEdgePiece(position);

        if (!(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5)) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() + 1, position.getColumn() + 1, pieceColor));
        }
        if (!(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && !(edgeStatus == 7) && !(edgeStatus == 8)) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() + 1, position.getColumn() - 1, pieceColor));
        }
        if (!(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7)) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() - 1, position.getColumn() + 1, pieceColor));
        }
        if (!(edgeStatus == 1) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7) && !(edgeStatus == 8)) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() - 1, position.getColumn() - 1, pieceColor));
        }

        if (!(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3)) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() + 1, position.getColumn(), pieceColor));
        }
        if (!(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7)) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() - 1, position.getColumn(), pieceColor));
        }
        if (!(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5)) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow(), position.getColumn() + 1, pieceColor));
        }
        if (!(edgeStatus == 1)  && !(edgeStatus == 7) && !(edgeStatus == 8)) {
            possibleEndPositions.addAll(singleMoveHelper(board, position.getRow(), position.getColumn() - 1, pieceColor));
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