package webSocketMessages.userCommands;

public class UserGameCommandResign extends UserGameCommand {
    private final int gameID;

    public UserGameCommandResign(String authToken, int gameID) {
        super(authToken);
        commandType = CommandType.RESIGN;

        this.gameID = gameID;
    }

    public int getGameID() {
        return this.gameID;
    }
}
