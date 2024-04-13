package webSocketMessages.userCommands;

public class UserGameCommandRedraw extends UserGameCommand {
    private final int gameID;

    public UserGameCommandRedraw(String authToken, int gameID) {
        super(authToken);
        commandType = CommandType.REDRAW;

        this.gameID = gameID;
    }

    public int getGameID() {
        return this.gameID;
    }
}
