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

    private final BishopMoveCalculator bishopMoveCalculator = new BishopMoveCalculator();
    private final QueenMoveCalculator queenMoveCalculator = new QueenMoveCalculator();
    private final RookMoveCalculator rookMoveCalculator = new RookMoveCalculator();
    private final KnightMoveCalculator knightMoveCalculator = new KnightMoveCalculator();
    private final KingMoveCalculator kingMoveCalculator = new KingMoveCalculator();
    private final PawnMoveCalculator pawnMoveCalculator = new PawnMoveCalculator();

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
        PAWN;

        @Override
        public String toString() {
            return this.name();
        }
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

        switch (type) {
            case BISHOP -> {
                possibleEndPositions = bishopMoveCalculator.possibleEndPositionCalculator(board, myPosition, this.pieceColor);
            }
            case KING -> {
                possibleEndPositions = kingMoveCalculator.possibleEndPositionCalculator(board, myPosition, this.pieceColor);
            }
            case KNIGHT -> {
                possibleEndPositions = knightMoveCalculator.possibleEndPositionCalculator(board, myPosition, this.pieceColor);
            }
            case PAWN -> {
                List<int[]> possiblePawnEndPositions;

                possiblePawnEndPositions = pawnMoveCalculator.possibleEndPositionCalculator(board, myPosition, this.pieceColor);

                if (!possiblePawnEndPositions.isEmpty()) {
                    if (possiblePawnEndPositions.getFirst().length == 3) {
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
                possibleEndPositions = queenMoveCalculator.possibleEndPositionCalculator(board, myPosition, this.pieceColor);
            }
            case ROOK -> {
                possibleEndPositions = rookMoveCalculator.possibleEndPositionCalculator(board, myPosition, this.pieceColor);
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


