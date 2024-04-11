package webSocketMessages.userCommands;

import chess.ChessMove;

public class UserGameCommandMakeMove extends UserGameCommand {
    private final int gameID;
    private final ChessMove move;
    public UserGameCommandMakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken);
        commandType = CommandType.MAKE_MOVE;

        this.gameID = gameID;
        this.move = move;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }
}
