package webSocketMessages.userCommands;

import chess.ChessPosition;

public class UserGameCommandRedraw extends UserGameCommand {
    private final int gameID;
    private final ChessPosition position;

    public UserGameCommandRedraw(String authToken, int gameID, ChessPosition position) {
        super(authToken);
        commandType = CommandType.REDRAW;

        this.gameID = gameID;
        this.position = position;
    }

    public int getGameID() {
        return this.gameID;
    }
    public ChessPosition getPosition() { return this.position; }
}
