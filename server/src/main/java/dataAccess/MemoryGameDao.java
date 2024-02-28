package dataAccess;

import chess.ChessGame;
import chess.model.GameData;
import service.resultRecords.CreateResult;

import java.util.HashMap;

public class MemoryGameDao implements GameDao {
    static HashMap<Integer, GameData> games = new HashMap<>();

    /**
     * Creates a new game in memory
     * (Usernames are initialized to null and a new ChessGame object is generated)
     *
     * @param gameID ID for the game to be generated
     * @param gameName Name for the game to be generated
     * @return int gameID
     */
    @Override
    public int createGame(int gameID, String gameName) {
        MemoryGameDao.games.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));

        return gameID;
    }

    /**
     * Clears all users data in memory
     */
    @Override
    public void clear() {
        MemoryGameDao.games.clear();
    }
}
