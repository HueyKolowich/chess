package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private ChessBoard tempBoard = null;
    private ChessGame.TeamColor currentTurn;

    public ChessGame() {
        this.board = new ChessBoard();
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

    private void changeBoard(ChessBoard board) {
        this.board = board;
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
        HashSet<ChessMove> validMoves = new HashSet<>();
        ChessPiece currentPiece = board.getPiece(startPosition);

        if (currentPiece == null) { return null; }

        for (ChessMove pieceMove : currentPiece.pieceMoves(board, startPosition)) {
            ChessBoard simulatedBoard = new ChessBoard(board);
            ChessPiece simulatedCurrentPiece = simulatedBoard.getPiece(pieceMove.getStartPosition());

            if (pieceMove.getPromotionPiece() != null) {
                makePromotionalMove(pieceMove, currentPiece.getTeamColor());
            } else {
                simulatedBoard.addPiece(pieceMove.getEndPosition(), simulatedCurrentPiece);
                simulatedBoard.addPiece(pieceMove.getStartPosition(), null);
            }

            tempBoard = this.board;
            changeBoard(simulatedBoard);
            if (!isInCheck(currentPiece.getTeamColor())) {
                changeBoard(tempBoard);
                tempBoard = null;

                validMoves.add(pieceMove);
            } else {
                changeBoard(tempBoard);
                tempBoard = null;
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece currentPiece = board.getPiece(move.getStartPosition());

        if (!currentPiece.getTeamColor().equals(currentTurn)) {
            throw new InvalidMoveException("It is the other team's turn to move");
        }

        if (!board.isPiece(move.getStartPosition())) {
            throw new InvalidMoveException("There is no piece at the startPosition of this move!");
        }

        if (!currentPiece.pieceMoves(board, move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("This move is not a valid move for this chess piece!");
        }

        try {
            if (board.getKingPosition(flipTeamColor(currentTurn)).equals(move.getEndPosition())) {
                throw new InvalidMoveException("This move is not a valid move for this chess piece (it is the enemy king)!");
            }
        } catch (NoPieceException noPieceException) {
            System.out.printf("No pieces on the opposing team! %s", noPieceException);
        }

        if (isInCheck(currentTurn)) {
            ChessBoard simulatedBoard = new ChessBoard(board);
            ChessPiece simulatedCurrentPiece = simulatedBoard.getPiece(move.getStartPosition());

            if (move.getPromotionPiece() != null) {
                makePromotionalMove(move, currentTurn);
            } else {
                simulatedBoard.addPiece(move.getEndPosition(), simulatedCurrentPiece);
                simulatedBoard.addPiece(move.getStartPosition(), null);
            }

            tempBoard = this.board;
            changeBoard(simulatedBoard);
            if (isInCheck(currentTurn)) {
                changeBoard(tempBoard);
                tempBoard = null;

                throw new InvalidMoveException("This move did not take the king out of check!");
            } else {
                changeBoard(tempBoard);
                tempBoard = null;
            }
        }

        if (move.getPromotionPiece() != null) {
            makePromotionalMove(move, currentTurn);
        } else {
            board.addPiece(move.getEndPosition(), currentPiece);
            board.addPiece(move.getStartPosition(), null);
        }

        if (currentTurn.equals(TeamColor.WHITE)) { setTeamTurn(TeamColor.BLACK); }
        else { setTeamTurn(TeamColor.WHITE); }
    }

    private void makePromotionalMove(ChessMove move, ChessGame.TeamColor currentTeamColor) {
        board.addPiece(move.getEndPosition(), new ChessPiece(currentTeamColor, move.getPromotionPiece()));
        board.addPiece(move.getStartPosition(), null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition;

        try {
            kingPosition = board.getKingPosition(teamColor);
        } catch (NoPieceException noPieceException) {
            System.out.printf("Missing King! %s", noPieceException);
            kingPosition = null;
        }

        try {
            ChessGame.TeamColor oppositeTeamColor;
            if (teamColor.equals(TeamColor.WHITE)) { oppositeTeamColor = TeamColor.BLACK; }
            else { oppositeTeamColor = TeamColor.WHITE; }

            HashSet<PieceAndPositionTuple<ChessPiece, ChessPosition>> oppositeTeamPieces = board.getTeamPieces(oppositeTeamColor);

            for (PieceAndPositionTuple<ChessPiece, ChessPosition> oppositeTeamPiece : oppositeTeamPieces) {
                oppositeTeamPiece.getPiece().pieceMoves(this.board, oppositeTeamPiece.getPosition());

                for (ChessMove move : oppositeTeamPiece.getPiece().pieceMoves(this.board, oppositeTeamPiece.getPosition())) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }

            return false;
        } catch (NoPieceException noPieceException) {
            System.out.printf("No pieces on the opposing team! %s", noPieceException);
            return false;
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) { return false; }

        try {
            for (PieceAndPositionTuple<ChessPiece, ChessPosition> teamPiece : board.getTeamPieces(teamColor)) {
                for (ChessMove validMove : teamPiece.getPiece().pieceMoves(board, teamPiece.getPosition())) {
                    if (validMove.getEndPosition().equals(board.getKingPosition(flipTeamColor(teamColor)))) {
                        break;
                    }

                    if (!isInCheckHandler(validMove)) {
                        return false;
                    }
                }
            }
        } catch (NoPieceException noPieceException) {
            System.out.printf("No pieces on team! %s", noPieceException);
            return false;
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) { return false; }

        try {
            for (PieceAndPositionTuple<ChessPiece, ChessPosition> teamPiece : board.getTeamPieces(teamColor)) {
                if (!validMoves(teamPiece.getPosition()).isEmpty()) { return false; }
            }
        } catch (NoPieceException noPieceException) {
            System.out.printf("No pieces on team! %s", noPieceException);
            return false;
        }

        return true;
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

    private boolean isInCheckHandler(ChessMove move) {
        ChessBoard simulatedBoard = new ChessBoard(board);
        ChessPiece simulatedCurrentPiece = simulatedBoard.getPiece(move.getStartPosition());

        if (move.getPromotionPiece() != null) {
            makePromotionalMove(move, currentTurn);
        } else {
            simulatedBoard.addPiece(move.getEndPosition(), simulatedCurrentPiece);
            simulatedBoard.addPiece(move.getStartPosition(), null);
        }

        tempBoard = this.board;
        changeBoard(simulatedBoard);
        if (isInCheck(currentTurn)) {
            changeBoard(tempBoard);
            tempBoard = null;

            return true;
        } else {
            changeBoard(tempBoard);
            tempBoard = null;

            return false;
        }
    }

    private TeamColor flipTeamColor(TeamColor teamColor) {
        ChessGame.TeamColor oppositeTeamColor;

        if (teamColor.equals(TeamColor.WHITE)) { oppositeTeamColor = TeamColor.BLACK; }
        else { oppositeTeamColor = TeamColor.WHITE; }

        return oppositeTeamColor;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", tempBoard=" + tempBoard +
                ", currentTurn=" + currentTurn +
                '}';
    }
}
