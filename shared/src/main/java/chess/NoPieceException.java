package chess;

/**
 * Indicates an invalid move was made in a game
 */
public class NoPieceException extends Exception {

    public NoPieceException() {}

    public NoPieceException(String message) {
        super(message);
    }
}
