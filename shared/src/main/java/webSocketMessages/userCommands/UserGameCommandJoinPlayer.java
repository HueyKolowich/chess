package webSocketMessages.userCommands;

import chess.ChessGame;

public class UserGameCommandJoinPlayer extends UserGameCommand {
    private final int gameID;
    private final ChessGame.TeamColor playerColor;

    public UserGameCommandJoinPlayer(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        commandType = CommandType.JOIN_PLAYER;

        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return this.gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return this.playerColor;
    }
}
