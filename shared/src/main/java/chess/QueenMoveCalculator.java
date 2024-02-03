package chess;

import java.util.*;

public class QueenMoveCalculator implements ChessPieceCalculator {

    public List<int[]> possibleEndPositionCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor) {
        List<int[]> possibleEndPositions = new ArrayList<>();

        for (int r = position.getRow() + 1, c = position.getColumn() + 1; ((r < 9) && (c < 9)); r++, c++) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isEnemyPiece(new ChessPosition(r, c), pieceColor)) {
                break;
            } else if (board.isPiece(new ChessPosition(r, c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = position.getRow() + 1, c = position.getColumn() - 1; ((r < 9) && (c > 0)); r++, c--) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isEnemyPiece(new ChessPosition(r, c), pieceColor)) {
                break;
            } else if (board.isPiece(new ChessPosition(r, c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = position.getRow() - 1, c = position.getColumn() + 1; ((r > 0) && (c < 9)); r--, c++) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isEnemyPiece(new ChessPosition(r, c), pieceColor)) {
                break;
            } else if (board.isPiece(new ChessPosition(r, c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = position.getRow() - 1, c = position.getColumn() - 1; ((r > 0) && (c > 0)); r--, c--) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isEnemyPiece(new ChessPosition(r, c), pieceColor)) {
                break;
            } else if (board.isPiece(new ChessPosition(r, c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }


        for (int r = position.getRow() + 1; (r < 9); r++) {
            possibleEndPositions.add(new int[]{r, position.getColumn()});

            if (board.isEnemyPiece(new ChessPosition(r, position.getColumn()), pieceColor)) {
                break;
            } else if (board.isPiece(new ChessPosition(r, position.getColumn()))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = position.getRow() - 1; (r > 0); r--) {
            possibleEndPositions.add(new int[]{r, position.getColumn()});

            if (board.isEnemyPiece(new ChessPosition(r, position.getColumn()), pieceColor)) {
                break;
            } else if (board.isPiece(new ChessPosition(r, position.getColumn()))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int c = position.getColumn() + 1; (c < 9); c++) {
            possibleEndPositions.add(new int[]{position.getRow(), c});

            if (board.isEnemyPiece(new ChessPosition(position.getRow(), c), pieceColor)) {
                break;
            } else if (board.isPiece(new ChessPosition(position.getRow(), c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int c = position.getColumn() - 1; (c > 0); c--) {
            possibleEndPositions.add(new int[]{position.getRow(), c});

            if (board.isEnemyPiece(new ChessPosition(position.getRow(), c), pieceColor)) {
                break;
            } else if (board.isPiece(new ChessPosition(position.getRow(), c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        return possibleEndPositions;
    }
}