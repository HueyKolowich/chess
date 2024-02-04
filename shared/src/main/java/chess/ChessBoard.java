package chess;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * 
 * 
 * VERY IMPORTANT - Whenever positions are accessed within the ChessBoard class, the numberingseriesconversion maps must be used
 * as the logic for interpreting board positions is different in all other parts of the program
 * 
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] squares = new ChessPiece[8][8];

    private final HashMap<Integer, Integer> rowNumberingSeriesConversion = new HashMap<Integer, Integer>() {{
        put(1, 7);
        put(2, 6);
        put(3, 5);
        put(4, 4);
        put(5, 3);
        put(6, 2);
        put(7, 1);
        put(8, 0);
    }};

    private final HashMap<Integer, Integer> columnNumberingSeriesConversion = new HashMap<Integer, Integer>() {{
        put(1, 0);
        put(2, 1);
        put(3, 2);
        put(4, 3);
        put(5, 4);
        put(6, 5);
        put(7, 6);
        put(8, 7);
    }};

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[rowNumberingSeriesConversion.get((Integer) position.getRow())][columnNumberingSeriesConversion.get((Integer) position.getColumn())] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[rowNumberingSeriesConversion.get(position.getRow())][columnNumberingSeriesConversion.get(position.getColumn())];
    }

    /**
     * Finds the king's position on the board
     *
     * @param teamColor The team who's king's position is needed
     * @return A new ChessPosition with the King's position
     * @throws NoPieceException If no king is found on the board for that team
     */
    public ChessPosition getKingPosition(ChessGame.TeamColor teamColor) throws NoPieceException {
        for (int row = 8; row > 0; row--) {
            for (int col = 1; col < 9; col++) {
                if (squares[rowNumberingSeriesConversion.get(row)][columnNumberingSeriesConversion.get(col)].getPieceType().equals(ChessPiece.PieceType.KING)
                && squares[rowNumberingSeriesConversion.get(row)][columnNumberingSeriesConversion.get(col)].getTeamColor().equals(teamColor)) {
                    return new ChessPosition(row, col);
                }
            }
        }

        throw new NoPieceException("No king found for this team!");
    }

    /**
     * Returns true if there is a piece at the specified position, otherwise false
     *
     * @param position The position to check
     * @return Returns true if there is a piece at the specified position, otherwise false
     */
    public boolean isPiece(ChessPosition position) {
        return squares[rowNumberingSeriesConversion.get(position.getRow())][columnNumberingSeriesConversion.get(position.getColumn())] != null;
    }

    /**
     * Returns true if there is an enemy piece at the specified position, otherwise false
     *
     * @param position The position to check
     * @return Returns true if there is a piece at the specified position, otherwise false
     */
    public boolean isEnemyPiece(ChessPosition position, ChessGame.TeamColor pieceColor) {
        if (squares[rowNumberingSeriesConversion.get(position.getRow())][columnNumberingSeriesConversion.get(position.getColumn())] == null) { return false; }

        return !(pieceColor.toString().equals(squares[rowNumberingSeriesConversion.get(position.getRow())][columnNumberingSeriesConversion.get(position.getColumn())].getTeamColor().toString()));
    }

    /**
     * Returns an int describing the edge status of a chess piece.
     * """
     *    |1|2|2|2|2|2|2|3|
     *    |8| | | | | | |4|
     *    |8| | | | | | |4|
     *    |8| | |-1| | | |4|
     *    |8| | | |-1| | |4|
     *    |8| | | | | | |4|
     *    |8| | | | | | |4|
     *    |7|6|6|6|6|6|6|5|
     *    """
     *
     * @param position the position of the chess piece
     * @return Returns an int describing the edge status of a chess piece.
     */
    public int isEdgePiece(ChessPosition position) {
        if (rowNumberingSeriesConversion.get(position.getRow()) == 0) {
            if (columnNumberingSeriesConversion.get(position.getColumn()) == 0) {
                return 1;
            } else if (columnNumberingSeriesConversion.get(position.getColumn()) == 7) {
                return 3;
            } else { return 2; }
        }

        if (columnNumberingSeriesConversion.get(position.getColumn()) == 7) {
            if (rowNumberingSeriesConversion.get(position.getRow()) == 0) {
                return 3;
            } else if (rowNumberingSeriesConversion.get(position.getColumn()) == 7) {
                return 5;
            } else { return 4; }
        }

        if (rowNumberingSeriesConversion.get(position.getRow()) == 7) {
            if (columnNumberingSeriesConversion.get(position.getColumn()) == 0) {
                return 7;
            } else if (columnNumberingSeriesConversion.get(position.getColumn()) == 7) {
                return 5;
            } else { return 6; }
        }

        if (columnNumberingSeriesConversion.get(position.getColumn()) == 0) {
            if (rowNumberingSeriesConversion.get(position.getRow()) == 0) {
                return 1;
            } else if (rowNumberingSeriesConversion.get(position.getColumn()) == 7) {
                return 7;
            } else { return 8; }
        }

        return -1;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = null;
            }
        }

        this.addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        this.addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        for (int col = 1; col <= 8; col++) {
            this.addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        this.addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        this.addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        for (int col = 1; col <= 8; col++) {
            this.addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        String chessBoardOutput = "";

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (squares[row][col] != null) {
                    chessBoardOutput += squares[row][col].toString();
                    chessBoardOutput += "";
                }
            }
        }

        return "ChessBoard{" +
                "squares=" + chessBoardOutput +
                '}';
    }
}
