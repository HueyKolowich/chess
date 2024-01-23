package chess;

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
        squares[rowNumberingSeriesConversion.get(position.getRow())][columnNumberingSeriesConversion.get(position.getColumn())] = piece;
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
        throw new RuntimeException("Not implemented");
    }
}
