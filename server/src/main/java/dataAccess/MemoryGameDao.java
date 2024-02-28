package dataAccess;

import chess.ChessGame;
import chess.model.GameData;
import service.resultRecords.ListResultBody;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

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
     * Formats and returns the games in memory
     *
     * @return The games in memory
     */
    @Override
    public Collection<ListResultBody> listGames() {
        Collection<ListResultBody> formattedGames = new HashSet<>();

        for (GameData gameData : MemoryGameDao.games.values()) {
            formattedGames.add(new ListResultBody(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName()));
        }

        return formattedGames;
    }

    /**
     * Finds a game by gameID in memory
     *
     * @param gameID for which to search for the game
     * @return True if game is found, false otherwise
     */
    @Override
    public boolean findGame(int gameID) {
        return MemoryGameDao.games.containsKey(gameID);
    }

    /**
     * Adds a player's username to the game
     *
     * @param playerColor The color the user wishes to join as
     * @param username String the username of the user that is joining
     * @param gameID int the game to join
     * @throws DataAccessException if a playerColor is already taken for the specified color
     */
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

    /**
     * Clears all users data in memory
     */
    @Override
    public void clear() {
        MemoryGameDao.games.clear();
    }
}
