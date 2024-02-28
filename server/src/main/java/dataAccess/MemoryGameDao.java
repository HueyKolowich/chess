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

    @Override
    public boolean findGame(int gameID) {
        return MemoryGameDao.games.containsKey(gameID);
    }

    @Override
    public void addPlayer(String playerColor, String username, int gameID) throws DataAccessException {
        String tempWhiteUsername = MemoryGameDao.games.get(gameID).whiteUsername();
        String tempBlackUsername = MemoryGameDao.games.get(gameID).blackUsername();
        String tempGameName = MemoryGameDao.games.get(gameID).gameName();
        ChessGame tempChessGame = MemoryGameDao.games.get(gameID).game();

        if (playerColor.equals("WHITE")) {
            if (MemoryGameDao.games.get(gameID).whiteUsername() != null) {
                throw new DataAccessException("playerColor already taken");
            }

            MemoryGameDao.games.put(gameID, new GameData(gameID, username, tempBlackUsername, tempGameName, tempChessGame));
        } else {
            if (MemoryGameDao.games.get(gameID).blackUsername() != null) {
                throw new DataAccessException("playerColor already taken");
            }

            MemoryGameDao.games.put(gameID, new GameData(gameID, tempWhiteUsername, username, tempGameName, tempChessGame));
        }
    }

    @Override
    public boolean addSpectator(int gameID) {
        return false;
    }

    /**
     * Clears all users data in memory
     */
    @Override
    public void clear() {
        MemoryGameDao.games.clear();
    }
}
