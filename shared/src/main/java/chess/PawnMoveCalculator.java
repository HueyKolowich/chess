package chess;

import java.util.*;

public class PawnMoveCalculator implements ChessPieceCalculator {
    @Override
    public List<int[]> possibleEndPositionCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor) {
        List<int[]> possibleEndPositions = new ArrayList<int[]>();
        int edgeStatus = board.isEdgePiece(position);

        // Check for promotional move
        if ((pieceColor.toString().equals("WHITE") && (position.getRow() == 7)) || ((pieceColor.toString().equals("BLACK")) && (position.getRow() == 2))) {
            List<int[]> possiblePromotionEndPositions = new ArrayList<int[]>();

            // Cases for capturing
            if (!(pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5)) {
                if (board.isEnemyPiece(new ChessPosition(position.getRow() + 1, position.getColumn() + 1), pieceColor)) {
                    possiblePromotionEndPositions.add(new int[]{position.getRow() + 1, position.getColumn() + 1, 1});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() + 1, position.getColumn() + 1, 2});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() + 1, position.getColumn() + 1, 3});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() + 1, position.getColumn() + 1, 4});
                }
            }
            if (!(pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && !(edgeStatus == 7) && !(edgeStatus == 8)) {
                if (board.isEnemyPiece(new ChessPosition(position.getRow() + 1, position.getColumn() - 1), pieceColor)) {
                    possiblePromotionEndPositions.add(new int[]{position.getRow() + 1, position.getColumn() - 1, 1});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() + 1, position.getColumn() - 1, 2});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() + 1, position.getColumn() - 1, 3});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() + 1, position.getColumn() - 1, 4});
                }
            }
            if (!(pieceColor.toString().equals("WHITE")) && !(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7)) {
                if (board.isEnemyPiece(new ChessPosition(position.getRow() - 1, position.getColumn() + 1), pieceColor)) {
                    possiblePromotionEndPositions.add(new int[]{position.getRow() - 1, position.getColumn() + 1, 1});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() - 1, position.getColumn() + 1, 2});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() - 1, position.getColumn() + 1, 3});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() - 1, position.getColumn() + 1, 4});
                }
            }
            if (!(pieceColor.toString().equals("WHITE")) && !(edgeStatus == 1) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7) && !(edgeStatus == 8)) {
                if (board.isEnemyPiece(new ChessPosition(position.getRow() - 1, position.getColumn() - 1), pieceColor)) {
                    possiblePromotionEndPositions.add(new int[]{position.getRow() - 1, position.getColumn() - 1, 1});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() - 1, position.getColumn() - 1, 2});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() - 1, position.getColumn() - 1, 3});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() - 1, position.getColumn() - 1, 4});
                }
            }

            // Normal move cases
            if (!(pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3)) {
                if (!(board.isPiece(new ChessPosition(position.getRow() + 1, position.getColumn())))) {
                    possiblePromotionEndPositions.add(new int[]{position.getRow() + 1, position.getColumn(), 1});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() + 1, position.getColumn(), 2});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() + 1, position.getColumn(), 3});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() + 1, position.getColumn(), 4});
                }
            }
            if (!(pieceColor.toString().equals("WHITE")) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7)) {
                if (!(board.isPiece(new ChessPosition(position.getRow() - 1, position.getColumn())))) {
                    possiblePromotionEndPositions.add(new int[]{position.getRow() - 1, position.getColumn(), 1});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() - 1, position.getColumn(), 2});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() - 1, position.getColumn(), 3});
                    possiblePromotionEndPositions.add(new int[]{position.getRow() - 1, position.getColumn(), 4});
                }
            }

            return possiblePromotionEndPositions;
        } else {
            // Special cases for initial moves
            if (!(pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && position.getRow() == 2) {
                if (!(board.isPiece(new ChessPosition(position.getRow() + 1, position.getColumn())))) {
                    possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() + 1, position.getColumn(), pieceColor));

                    if (!(board.isPiece(new ChessPosition(position.getRow() + 2, position.getColumn())))) {
                        possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() + 2, position.getColumn(), pieceColor));
                    }
                }
            }
            if (!(pieceColor.toString().equals("WHITE")) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7) && position.getRow() == 7) {
                if (!(board.isPiece(new ChessPosition(position.getRow() - 1, position.getColumn())))) {
                    possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() - 1, position.getColumn(), pieceColor));

                    if (!(board.isPiece(new ChessPosition(position.getRow() - 2, position.getColumn())))) {
                        possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() - 2, position.getColumn(), pieceColor));
                    }
                }
            }

            // Cases for capturing
            if (!(pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5)) {
                if (board.isEnemyPiece(new ChessPosition(position.getRow() + 1, position.getColumn() + 1), pieceColor)) {
                    possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() + 1, position.getColumn() + 1, pieceColor));
                }
            }
            if (!(pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && !(edgeStatus == 7) && !(edgeStatus == 8)) {
                if (board.isEnemyPiece(new ChessPosition(position.getRow() + 1, position.getColumn() - 1), pieceColor)) {
                    possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() + 1, position.getColumn() - 1, pieceColor));
                }
            }
            if (!(pieceColor.toString().equals("WHITE")) && !(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7)) {
                if (board.isEnemyPiece(new ChessPosition(position.getRow() - 1, position.getColumn() + 1), pieceColor)) {
                    possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() - 1, position.getColumn() + 1, pieceColor));
                }
            }
            if (!(pieceColor.toString().equals("WHITE")) && !(edgeStatus == 1) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7) && !(edgeStatus == 8)) {
                if (board.isEnemyPiece(new ChessPosition(position.getRow() - 1, position.getColumn() - 1), pieceColor)) {
                    possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() - 1, position.getColumn() - 1, pieceColor));
                }
            }

            // Normal move cases
            if (!(pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3)) {
                if (!(board.isPiece(new ChessPosition(position.getRow() + 1, position.getColumn())))) {
                    possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() + 1, position.getColumn(), pieceColor));
                }
            }
            if (!(pieceColor.toString().equals("WHITE")) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7)) {
                if (!(board.isPiece(new ChessPosition(position.getRow() - 1, position.getColumn())))) {
                    possibleEndPositions.addAll(singleMoveHelper(board, position.getRow() - 1, position.getColumn(), pieceColor));
                }
            }

            return possibleEndPositions;
        }
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