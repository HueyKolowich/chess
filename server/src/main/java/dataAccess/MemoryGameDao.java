package dataAccess;

import chess.ChessGame;
import chess.model.GameData;
import service.resultRecords.CreateResult;

import java.util.HashMap;

public class MemoryGameDao implements GameDao {
    static HashMap<Integer, GameData> games = new HashMap<>();

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
