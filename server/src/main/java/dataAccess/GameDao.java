package dataAccess;

import service.resultRecords.CreateResult;

public interface GameDao {
    int createGame(int gameID, String gameName);
    void clear();
}
