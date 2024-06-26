package webSocketMessages.serverMessages;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    String game;
    String errorMessage;
    String message;
    ChessGame.TeamColor playerColor;
    Collection<ChessMove> highlightedMoves;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String game, String errorMessage, String message, ChessGame.TeamColor playerColor, Collection<ChessMove> highlightedMoves) {
        this.serverMessageType = type;
        this.game = game;
        this.errorMessage = errorMessage;
        this.message = message;
        this.playerColor = playerColor;
        this.highlightedMoves = highlightedMoves;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getGame() {
        return game;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getMessage() {
        return message;
    }

    public ChessGame.TeamColor getPlayerColor() { return playerColor; }

    public Collection<ChessMove> getHighlightedMoves() { return highlightedMoves; }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerMessage))
            return false;
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
