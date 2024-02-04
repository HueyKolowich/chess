package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private ChessGame.TeamColor currentTurn;

    public ChessGame() {
        this.currentTurn = TeamColor.WHITE;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

        @Override
        public String toString() {
            return this.name();
        }
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece currentPiece = board.getPiece(move.getStartPosition());

        //TODO Is it the correct team's turn to make a move?
        if (!currentPiece.getTeamColor().equals(currentTurn)) {
            throw new InvalidMoveException("It is the other team's turn to move");
        }

        //TODO First ensure that there is piece at the start position in move
        if (!board.isPiece(move.getStartPosition())) {
            throw new InvalidMoveException("There is no piece at the startPosition of this move!");
        }

        //TODO Need to check using the chess piece at the startposition if the end position is included in the valid moves
        if (!currentPiece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("This move is not a valid move for this chess piece!");
        }

        //TODO Add new piece at endposition
        board.addPiece(move.getEndPosition(), currentPiece);

        //TODO Clear the piece on the board at start position
        board.addPiece(move.getStartPosition(), null);

        //TODO Change the current team's turn
        if (currentTurn.equals(TeamColor.WHITE)) { setTeamTurn(TeamColor.BLACK); }
        else { setTeamTurn(TeamColor.WHITE); }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        try {
            ChessPosition kingPosition = board.getKingPosition(teamColor);
        } catch (NoPieceException noPieceException) {
            System.out.printf("Missing King! %s", noPieceException);
        }

        //TODO For all the pieces on the opposite team...

        //TODO Check to see if the king position is included in any of their valid moves

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
