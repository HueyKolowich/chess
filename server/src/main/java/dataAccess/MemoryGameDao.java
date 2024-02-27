package dataAccess;

import chess.model.GameData;
import java.util.HashMap;

public class MemoryGameDao implements GameDao {
    static HashMap<String, GameData> games = new HashMap<>();

    /**
     * Clears all users data in memory
     */
    @Override
    public void clear() {
        MemoryGameDao.games.clear();
    }
}
