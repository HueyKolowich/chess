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

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
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
        throw new RuntimeException("Not implemented");
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

//        // TODO These for loops need to be reconfigured to explore (r++, c++)(r++, c--)[etc..] from bishopPosition
//        // Could we just have four sets of nested for loops run in a series?
//        for (int r = 8; r > 0; r--) {
//            for (int c = 1; c < 9; ++c) {
//                // Is there a blocking piece?
//                // If yes, break (and break entirely from the exploration of both r and c)
//                // Otherwise, continue
//                //
//                // Is there a piece here? (Can chesspiece call chessboard for this information?)
//                // Cases:
//                // There is a piece here - it can be captured, we must break from all exploration
//                // No piece here - We would have already broken earlier and so this position can be added to possibleEndPositions
//                //
//
//                // The absolute value of the difference between bishopPosition's row and column and r & c must be equivalent for it to be a valid bishop move
//                if (Math.abs(bishopPosition.getRow() - r) == Math.abs(bishopPosition.getColumn() - c)) {
//                    // Removing the current bishop position from the list TODO this can be more efficiently done outside (after) of the loops I think
//                    if (!((Math.abs(bishopPosition.getRow() - r) == 0) && (Math.abs(bishopPosition.getColumn() - c) == 0))) {
//                        possibleEndPositions.add(new int[]{r, c});
//                    }
//                }
//            }
//        }

        for (int r = bishopPosition.getRow() + 1, c = bishopPosition.getColumn() + 1; ((r < 9) && (c < 9)); r++, c++) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isPiece(new ChessPosition(r, c))) { break; }
        }

        for (int r = bishopPosition.getRow() + 1, c = bishopPosition.getColumn() - 1; ((r < 9) && (c > 0)); r++, c--) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isPiece(new ChessPosition(r, c))) { break; }
        }

        for (int r = bishopPosition.getRow() - 1, c = bishopPosition.getColumn() + 1; ((r > 0) && (c < 9)); r--, c++) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isPiece(new ChessPosition(r, c))) { break; }
        }

        for (int r = bishopPosition.getRow() - 1, c = bishopPosition.getColumn() - 1; ((r > 0) && (c > 0)); r--, c--) {
            possibleEndPositions.add(new int[]{r, c});

            if (board.isPiece(new ChessPosition(r, c))) { break; }
        }

        return possibleEndPositions;
    }
}
