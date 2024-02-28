package dataAccess;

import service.resultRecords.CreateResult;

public interface GameDao {
    int createGame(int gameID, String gameName);
    boolean findGame(int gameID);
    void addPlayer(String playerColor, String username, int gameID);
    boolean addSpectator(int gameID);
    void clear();
}
