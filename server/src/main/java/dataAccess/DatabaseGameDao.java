package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import service.resultRecords.ListResultBody;

import java.util.Collection;

public class DatabaseGameDao extends DatabaseDao implements GameDao {
    public DatabaseGameDao() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS game (
                `gameID` int NOT NULL,
                `whiteUsername` varchar(256) DEFAULT NULL,
                `blackUsername` varchar(256) DEFAULT NULL,
                `gameName` varchar(256) NOT NULL,
                `game` longtext NOT NULL,
                PRIMARY KEY (`gameID`),
                INDEX (`gameName`)
            )
            """
        };
        configureDatabase(createStatements);
    }

    @Override
    public int createGame(int gameID, String gameName) throws DataAccessException {
        String statement = "INSERT INTO game (gameID, gameName, game) VALUES (?, ?, ?)";
        String game = new Gson().toJson(new ChessGame());

        executeUpdate(statement, gameID, gameName, game);

        if (selectItem("SELECT gameID FROM game WHERE gameID=?", (Integer) gameID)) {
            return gameID;
        } else { throw new DataAccessException("Game was not created!"); }
    }

    @Override
    public Collection<ListResultBody> listGames() {
        return null;
    }

    @Override
    public boolean findGame(int gameID) {
        return false;
    }

    @Override
    public void addPlayer(String playerColor, String username, int gameID) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
