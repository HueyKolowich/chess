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

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
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
        throw new RuntimeException("Not implemented");
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

        // Could make a separate interface that is a piece moves calculator and have the different pieces inherit from it
        switch (type) {
            case BISHOP -> {
                List<int[]> possibleEndPositions = bishopMoves(board, myPosition);

                for (int[] possibleEndPosition : possibleEndPositions) {
                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(possibleEndPosition[0], possibleEndPosition[1]), null));
                }

                return pieceMoves;
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }

    /**
     * Calculates all the positions a bishop piece can move to
     *
     * @param bishopPosition
     * @return List of int[] of possible bishop positions
     */
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
}
