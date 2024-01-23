package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType type;

    private final ChessGame.TeamColor pieceColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.type = type;
        this.pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> pieceMoves = new HashSet<ChessMove>();
        List<int[]> possibleEndPositions = null;

        //TODO build a separate interface that is a piece moves calculator and have the different pieces inherit from it
        switch (type) {
            case BISHOP -> {
                possibleEndPositions = bishopMoves(board, myPosition);
            }
            case KING -> {
                possibleEndPositions = kingMoves(board, myPosition);
            }
            case KNIGHT -> {
                possibleEndPositions = knightMoves(board, myPosition);
            }
            case PAWN -> {
                List<int[]> possiblePawnEndPositions;

                possiblePawnEndPositions = pawnMoves(board, myPosition);

                //TODO Must check here to ensure the size of possiblePawnEndPositions it could be two or three
                if (possiblePawnEndPositions.size() != 0) {
                    if (possiblePawnEndPositions.get(0).length == 3) {
                        for (int[] possiblePawnEndPosition : possiblePawnEndPositions) {
                            switch (possiblePawnEndPosition[2]) {
                                case 1 -> {
                                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(possiblePawnEndPosition[0], possiblePawnEndPosition[1]), PieceType.BISHOP));
                                }
                                case 2 -> {
                                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(possiblePawnEndPosition[0], possiblePawnEndPosition[1]), PieceType.ROOK));
                                }
                                case 3 -> {
                                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(possiblePawnEndPosition[0], possiblePawnEndPosition[1]), PieceType.QUEEN));
                                }
                                case 4 -> {
                                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(possiblePawnEndPosition[0], possiblePawnEndPosition[1]), PieceType.KNIGHT));
                                }
                            }
                        }
                    } else {
                        for (int[] possiblePawnEndPosition : possiblePawnEndPositions) {
                            pieceMoves.add(new ChessMove(myPosition, new ChessPosition(possiblePawnEndPosition[0], possiblePawnEndPosition[1]), null));
                        }
                    }
                }
            }
            case QUEEN -> {
                possibleEndPositions = queenMoves(board, myPosition);
            }
            case ROOK -> {
                possibleEndPositions = rookMoves(board, myPosition);
            }
            default -> {
                return new ArrayList<>();
            }
        }

        if (possibleEndPositions != null) {
            for (int[] possibleEndPosition : possibleEndPositions) {
                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(possibleEndPosition[0], possibleEndPosition[1]), null));
            }
        }

        return pieceMoves;
    }

    private List<int[]> bishopMoves(ChessBoard board, ChessPosition bishopPosition) {
        List<int[]> possibleEndPositions = new ArrayList<int[]>();

        for (int r = bishopPosition.getRow() + 1, c = bishopPosition.getColumn() + 1; ((r < 9) && (c < 9)); r++, c++) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isEnemyPiece(new ChessPosition(r, c), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = bishopPosition.getRow() + 1, c = bishopPosition.getColumn() - 1; ((r < 9) && (c > 0)); r++, c--) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isEnemyPiece(new ChessPosition(r, c), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = bishopPosition.getRow() - 1, c = bishopPosition.getColumn() + 1; ((r > 0) && (c < 9)); r--, c++) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isEnemyPiece(new ChessPosition(r, c), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = bishopPosition.getRow() - 1, c = bishopPosition.getColumn() - 1; ((r > 0) && (c > 0)); r--, c--) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isEnemyPiece(new ChessPosition(r, c), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        return possibleEndPositions;
    }

    private List<int[]> queenMoves(ChessBoard board, ChessPosition queenPosition) {
        List<int[]> possibleEndPositions = new ArrayList<>();

        for (int r = queenPosition.getRow() + 1, c = queenPosition.getColumn() + 1; ((r < 9) && (c < 9)); r++, c++) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isEnemyPiece(new ChessPosition(r, c), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = queenPosition.getRow() + 1, c = queenPosition.getColumn() - 1; ((r < 9) && (c > 0)); r++, c--) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isEnemyPiece(new ChessPosition(r, c), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = queenPosition.getRow() - 1, c = queenPosition.getColumn() + 1; ((r > 0) && (c < 9)); r--, c++) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isEnemyPiece(new ChessPosition(r, c), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = queenPosition.getRow() - 1, c = queenPosition.getColumn() - 1; ((r > 0) && (c > 0)); r--, c--) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isEnemyPiece(new ChessPosition(r, c), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }


        for (int r = queenPosition.getRow() + 1; (r < 9); r++) {
            possibleEndPositions.add(new int[]{r, queenPosition.getColumn()});

            if (board.isEnemyPiece(new ChessPosition(r, queenPosition.getColumn()), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, queenPosition.getColumn()))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = queenPosition.getRow() - 1; (r > 0); r--) {
            possibleEndPositions.add(new int[]{r, queenPosition.getColumn()});

            if (board.isEnemyPiece(new ChessPosition(r, queenPosition.getColumn()), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, queenPosition.getColumn()))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int c = queenPosition.getColumn() + 1; (c < 9); c++) {
            possibleEndPositions.add(new int[]{queenPosition.getRow(), c});

            if (board.isEnemyPiece(new ChessPosition(queenPosition.getRow(), c), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(queenPosition.getRow(), c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int c = queenPosition.getColumn() - 1; (c > 0); c--) {
            possibleEndPositions.add(new int[]{queenPosition.getRow(), c});

            if (board.isEnemyPiece(new ChessPosition(queenPosition.getRow(), c), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(queenPosition.getRow(), c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        return possibleEndPositions;
    }

    private List<int[]> rookMoves(ChessBoard board, ChessPosition rookPosition) {
        List<int[]> possibleEndPositions = new ArrayList<>();

        for (int r = rookPosition.getRow() + 1; (r < 9); r++) {
            possibleEndPositions.add(new int[]{r, rookPosition.getColumn()});

            if (board.isEnemyPiece(new ChessPosition(r, rookPosition.getColumn()), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, rookPosition.getColumn()))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int r = rookPosition.getRow() - 1; (r > 0); r--) {
            possibleEndPositions.add(new int[]{r, rookPosition.getColumn()});

            if (board.isEnemyPiece(new ChessPosition(r, rookPosition.getColumn()), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(r, rookPosition.getColumn()))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int c = rookPosition.getColumn() + 1; (c < 9); c++) {
            possibleEndPositions.add(new int[]{rookPosition.getRow(), c});

            if (board.isEnemyPiece(new ChessPosition(rookPosition.getRow(), c), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(rookPosition.getRow(), c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        for (int c = rookPosition.getColumn() - 1; (c > 0); c--) {
            possibleEndPositions.add(new int[]{rookPosition.getRow(), c});

            if (board.isEnemyPiece(new ChessPosition(rookPosition.getRow(), c), this.pieceColor)) { break; } else if (board.isPiece(new ChessPosition(rookPosition.getRow(), c))) {
                possibleEndPositions.removeLast();
                break;
            }
        }

        return possibleEndPositions;
    }

    private List<int[]> kingMoves(ChessBoard board, ChessPosition kingPosition) {
        List<int[]> possibleEndPositions = new ArrayList<int[]>();
        int edgeStatus = board.isEdgePiece(kingPosition);

        if (!(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5)) {
            possibleEndPositions.addAll(singleMoveHelper(board, kingPosition.getRow() + 1, kingPosition.getColumn() + 1));
        }
        if (!(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && !(edgeStatus == 7) && !(edgeStatus == 8)) {
            possibleEndPositions.addAll(singleMoveHelper(board, kingPosition.getRow() + 1, kingPosition.getColumn() - 1));
        }
        if (!(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7)) {
            possibleEndPositions.addAll(singleMoveHelper(board, kingPosition.getRow() - 1, kingPosition.getColumn() + 1));
        }
        if (!(edgeStatus == 1) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7) && !(edgeStatus == 8)) {
            possibleEndPositions.addAll(singleMoveHelper(board, kingPosition.getRow() - 1, kingPosition.getColumn() - 1));
        }

        if (!(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3)) {
            possibleEndPositions.addAll(singleMoveHelper(board, kingPosition.getRow() + 1, kingPosition.getColumn()));
        }
        if (!(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7)) {
            possibleEndPositions.addAll(singleMoveHelper(board, kingPosition.getRow() - 1, kingPosition.getColumn()));
        }
        if (!(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5)) {
            possibleEndPositions.addAll(singleMoveHelper(board, kingPosition.getRow(), kingPosition.getColumn() + 1));
        }
        if (!(edgeStatus == 1)  && !(edgeStatus == 7) && !(edgeStatus == 8)) {
            possibleEndPositions.addAll(singleMoveHelper(board, kingPosition.getRow(), kingPosition.getColumn() - 1));
        }

        return possibleEndPositions;
    }

    private List<int[]> knightMoves(ChessBoard board, ChessPosition knightPosition) {
        List<int[]> possibleEndPositions = new ArrayList<int[]>();

        if (knightPosition.getRow() + 2 <= 8 && knightPosition.getColumn() + 1 <= 8) {
            possibleEndPositions.addAll(singleMoveHelper(board, knightPosition.getRow() + 2, knightPosition.getColumn() + 1));
        }
        if (knightPosition.getRow() + 1 <= 8 && knightPosition.getColumn() + 2 <= 8) {
            possibleEndPositions.addAll(singleMoveHelper(board, knightPosition.getRow() + 1, knightPosition.getColumn() + 2));
        }

        if (knightPosition.getRow() - 1 >= 1 && knightPosition.getColumn() + 2 <= 8) {
            possibleEndPositions.addAll(singleMoveHelper(board, knightPosition.getRow() - 1, knightPosition.getColumn() + 2));
        }
        if (knightPosition.getRow() - 2 >= 1 && knightPosition.getColumn() + 1 <= 8) {
            possibleEndPositions.addAll(singleMoveHelper(board, knightPosition.getRow() - 2, knightPosition.getColumn() + 1));
        }

        if (knightPosition.getRow() - 2 >= 1 && knightPosition.getColumn() - 1 >= 1) {
            possibleEndPositions.addAll(singleMoveHelper(board, knightPosition.getRow() - 2, knightPosition.getColumn() - 1));
        }
        if (knightPosition.getRow() - 1 >= 1 && knightPosition.getColumn() - 2 >= 1) {
            possibleEndPositions.addAll(singleMoveHelper(board, knightPosition.getRow() - 1, knightPosition.getColumn() - 2));
        }

        if (knightPosition.getRow() + 1 <= 8 && knightPosition.getColumn() - 2 >= 1) {
            possibleEndPositions.addAll(singleMoveHelper(board, knightPosition.getRow() + 1, knightPosition.getColumn() - 2));
        }
        if (knightPosition.getRow() + 2 <= 8 && knightPosition.getColumn() - 1 >= 1) {
            possibleEndPositions.addAll(singleMoveHelper(board, knightPosition.getRow() + 2, knightPosition.getColumn() - 1));
        }

        return possibleEndPositions;
    }

    private List<int []> pawnMoves(ChessBoard board, ChessPosition pawnPosition) {
        List<int[]> possibleEndPositions = new ArrayList<int[]>();
        int edgeStatus = board.isEdgePiece(pawnPosition);

        // Check for promotional move
        if ((this.pieceColor.toString().equals("WHITE") && (pawnPosition.getRow() == 7)) || ((this.pieceColor.toString().equals("BLACK")) && (pawnPosition.getRow() == 2))) {
            List<int[]> possiblePromotionEndPositions = new ArrayList<int[]>();

            // Cases for capturing
            if (!(this.pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5)) {
                if (board.isEnemyPiece(new ChessPosition(pawnPosition.getRow() + 1, pawnPosition.getColumn() + 1), this.pieceColor)) {
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() + 1, pawnPosition.getColumn() + 1, 1});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() + 1, pawnPosition.getColumn() + 1, 2});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() + 1, pawnPosition.getColumn() + 1, 3});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() + 1, pawnPosition.getColumn() + 1, 4});
                }
            }
            if (!(this.pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && !(edgeStatus == 7) && !(edgeStatus == 8)) {
                if (board.isEnemyPiece(new ChessPosition(pawnPosition.getRow() + 1, pawnPosition.getColumn() - 1), this.pieceColor)) {
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() + 1, pawnPosition.getColumn() - 1, 1});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() + 1, pawnPosition.getColumn() - 1, 2});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() + 1, pawnPosition.getColumn() - 1, 3});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() + 1, pawnPosition.getColumn() - 1, 4});
                }
            }
            if (!(this.pieceColor.toString().equals("WHITE")) && !(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7)) {
                if (board.isEnemyPiece(new ChessPosition(pawnPosition.getRow() - 1, pawnPosition.getColumn() + 1), this.pieceColor)) {
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() - 1, pawnPosition.getColumn() + 1, 1});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() - 1, pawnPosition.getColumn() + 1, 2});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() - 1, pawnPosition.getColumn() + 1, 3});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() - 1, pawnPosition.getColumn() + 1, 4});
                }
            }
            if (!(this.pieceColor.toString().equals("WHITE")) && !(edgeStatus == 1) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7) && !(edgeStatus == 8)) {
                if (board.isEnemyPiece(new ChessPosition(pawnPosition.getRow() - 1, pawnPosition.getColumn() - 1), this.pieceColor)) {
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() - 1, pawnPosition.getColumn() - 1, 1});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() - 1, pawnPosition.getColumn() - 1, 2});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() - 1, pawnPosition.getColumn() - 1, 3});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() - 1, pawnPosition.getColumn() - 1, 4});
                }
            }

            // Normal move cases
            if (!(this.pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3)) {
                if (!(board.isPiece(new ChessPosition(pawnPosition.getRow() + 1, pawnPosition.getColumn())))) {
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() + 1, pawnPosition.getColumn(), 1});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() + 1, pawnPosition.getColumn(), 2});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() + 1, pawnPosition.getColumn(), 3});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() + 1, pawnPosition.getColumn(), 4});
                }
            }
            if (!(this.pieceColor.toString().equals("WHITE")) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7)) {
                if (!(board.isPiece(new ChessPosition(pawnPosition.getRow() - 1, pawnPosition.getColumn())))) {
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() - 1, pawnPosition.getColumn(), 1});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() - 1, pawnPosition.getColumn(), 2});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() - 1, pawnPosition.getColumn(), 3});
                    possiblePromotionEndPositions.add(new int[]{pawnPosition.getRow() - 1, pawnPosition.getColumn(), 4});
                }
            }

            return possiblePromotionEndPositions;
        } else {
            // Special cases for initial moves
            if (!(this.pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && pawnPosition.getRow() == 2) {
                if (!(board.isPiece(new ChessPosition(pawnPosition.getRow() + 1, pawnPosition.getColumn())))) {
                    possibleEndPositions.addAll(singleMoveHelper(board, pawnPosition.getRow() + 1, pawnPosition.getColumn()));

                    if (!(board.isPiece(new ChessPosition(pawnPosition.getRow() + 2, pawnPosition.getColumn())))) {
                        possibleEndPositions.addAll(singleMoveHelper(board, pawnPosition.getRow() + 2, pawnPosition.getColumn()));
                    }
                }
            }
            if (!(this.pieceColor.toString().equals("WHITE")) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7) && pawnPosition.getRow() == 7) {
                if (!(board.isPiece(new ChessPosition(pawnPosition.getRow() - 1, pawnPosition.getColumn())))) {
                    possibleEndPositions.addAll(singleMoveHelper(board, pawnPosition.getRow() - 1, pawnPosition.getColumn()));

                    if (!(board.isPiece(new ChessPosition(pawnPosition.getRow() - 2, pawnPosition.getColumn())))) {
                        possibleEndPositions.addAll(singleMoveHelper(board, pawnPosition.getRow() - 2, pawnPosition.getColumn()));
                    }
                }
            }

            // Cases for capturing
            if (!(this.pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5)) {
                if (board.isEnemyPiece(new ChessPosition(pawnPosition.getRow() + 1, pawnPosition.getColumn() + 1), this.pieceColor)) {
                    possibleEndPositions.addAll(singleMoveHelper(board, pawnPosition.getRow() + 1, pawnPosition.getColumn() + 1));
                }
            }
            if (!(this.pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3) && !(edgeStatus == 7) && !(edgeStatus == 8)) {
                if (board.isEnemyPiece(new ChessPosition(pawnPosition.getRow() + 1, pawnPosition.getColumn() - 1), this.pieceColor)) {
                    possibleEndPositions.addAll(singleMoveHelper(board, pawnPosition.getRow() + 1, pawnPosition.getColumn() - 1));
                }
            }
            if (!(this.pieceColor.toString().equals("WHITE")) && !(edgeStatus == 3) && !(edgeStatus == 4) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7)) {
                if (board.isEnemyPiece(new ChessPosition(pawnPosition.getRow() - 1, pawnPosition.getColumn() + 1), this.pieceColor)) {
                    possibleEndPositions.addAll(singleMoveHelper(board, pawnPosition.getRow() - 1, pawnPosition.getColumn() + 1));
                }
            }
            if (!(this.pieceColor.toString().equals("WHITE")) && !(edgeStatus == 1) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7) && !(edgeStatus == 8)) {
                if (board.isEnemyPiece(new ChessPosition(pawnPosition.getRow() - 1, pawnPosition.getColumn() - 1), this.pieceColor)) {
                    possibleEndPositions.addAll(singleMoveHelper(board, pawnPosition.getRow() - 1, pawnPosition.getColumn() - 1));
                }
            }

            // Normal move cases
            if (!(this.pieceColor.toString().equals("BLACK")) && !(edgeStatus == 1) && !(edgeStatus == 2) && !(edgeStatus == 3)) {
                if (!(board.isPiece(new ChessPosition(pawnPosition.getRow() + 1, pawnPosition.getColumn())))) {
                    possibleEndPositions.addAll(singleMoveHelper(board, pawnPosition.getRow() + 1, pawnPosition.getColumn()));
                }
            }
            if (!(this.pieceColor.toString().equals("WHITE")) && !(edgeStatus == 5) && !(edgeStatus == 6) && !(edgeStatus == 7)) {
                if (!(board.isPiece(new ChessPosition(pawnPosition.getRow() - 1, pawnPosition.getColumn())))) {
                    possibleEndPositions.addAll(singleMoveHelper(board, pawnPosition.getRow() - 1, pawnPosition.getColumn()));
                }
            }

            return possibleEndPositions;
        }
    }

    private List<int[]> singleMoveHelper(ChessBoard board, int row, int column) {
        List<int[]> possibleEndPositions = new ArrayList<int[]>();

        if (board.isPiece(new ChessPosition(row, column))) {
            if (board.isEnemyPiece(new ChessPosition(row, column), this.pieceColor)) {
                possibleEndPositions.add(new int[]{row, column});
            }
        } else { possibleEndPositions.add(new int[]{row, column}); }

        return possibleEndPositions;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", pieceColor=" + pieceColor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece piece = (ChessPiece) o;
        return type == piece.type && pieceColor == piece.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }
}


