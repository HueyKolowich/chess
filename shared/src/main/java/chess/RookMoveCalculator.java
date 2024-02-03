package chess;

import java.util.*;

public class RookMoveCalculator implements ChessPieceCalculator {
    @Override
    public List<int[]> possibleEndPositionCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor) {
        List<int[]> possibleEndPositions = new ArrayList<>();

        for (int r = position.getRow() + 1; (r < 9); r++) {
            possibleEndPositions.add(new int[]{r, position.getColumn()});

            if (board.isEnemyPiece(new ChessPosition(r, position.getColumn()), pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, position.getColumn()))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = position.getRow() - 1; (r > 0); r--) {
            possibleEndPositions.add(new int[]{r, position.getColumn()});

            if (board.isEnemyPiece(new ChessPosition(r, position.getColumn()), pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, position.getColumn()))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int c = position.getColumn() + 1; (c < 9); c++) {
            possibleEndPositions.add(new int[]{position.getRow(), c});

            if (board.isEnemyPiece(new ChessPosition(position.getRow(), c), pieceColor)) { break; } else if (board.isPiece(new ChessPosition(position.getRow(), c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int c = position.getColumn() - 1; (c > 0); c--) {
            possibleEndPositions.add(new int[]{position.getRow(), c});

            if (board.isEnemyPiece(new ChessPosition(position.getRow(), c), pieceColor)) { break; } else if (board.isPiece(new ChessPosition(position.getRow(), c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        return possibleEndPositions;
    }
}