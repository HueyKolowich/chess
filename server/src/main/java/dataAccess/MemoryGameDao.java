package dataAccess;

import chess.model.GameData;
import java.util.HashMap;

public class MemoryGameDao implements GameDao {
    static HashMap<String, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        MemoryGameDao.games.clear();
    }
}
