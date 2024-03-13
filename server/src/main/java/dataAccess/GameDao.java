package dataAccess;

import chess.model.GameData;
import service.resultRecords.CreateResult;
import service.resultRecords.ListResultBody;

import java.util.Collection;

public interface GameDao {
    int createGame(int gameID, String gameName) throws DataAccessException;
    Collection<ListResultBody> listGames();
    boolean findGame(int gameID);
    void addPlayer(String playerColor, String username, int gameID) throws DataAccessException;
    void clear();
}
