package webSocketMessages.userCommands;

import chess.ChessMove;

public class UserGameCommandMakeMove extends UserGameCommand {
    private final int gameID;
    private final ChessMove move;
    private final String moveDescription;
    public UserGameCommandMakeMove(String authToken, int gameID, ChessMove move, String moveDescription) {
        super(authToken);
        commandType = CommandType.MAKE_MOVE;

        this.gameID = gameID;
        this.move = move;
        this.moveDescription = moveDescription;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getMoveDescription() { return moveDescription; }
}
