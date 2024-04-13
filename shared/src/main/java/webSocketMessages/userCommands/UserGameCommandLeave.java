package webSocketMessages.userCommands;

public class UserGameCommandLeave extends UserGameCommand {
    private final int gameID;

    public UserGameCommandLeave(String authToken, int gameID) {
        super(authToken);
        commandType = CommandType.LEAVE;

        this.gameID = gameID;
    }

    public int getGameID() {
        return this.gameID;
    }
}
