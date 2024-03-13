package dataAccess;

import chess.ChessGame;
import chess.model.GameData;
import com.google.gson.Gson;
import service.resultRecords.ListResultBody;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

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
    public Collection<ListResultBody> listGames() throws DataAccessException {
        Collection<ListResultBody> formattedGames = new HashSet<>();

        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM game";

            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeQuery();

                ResultSet resultSet = preparedStatement.getResultSet();

                while (resultSet.next()) {
                    formattedGames.add(new ListResultBody(resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4)));
                }

            }
        } catch (SQLException sqlException) {
            throw new DataAccessException(String.format("Unable to get DB connection: %s", sqlException.getMessage()));
        }

        return formattedGames;
    }

    @Override
    public boolean findGame(int gameID) throws DataAccessException {
        return selectItem("SELECT gameID FROM game WHERE gameID=?", gameID);
    }

    @Override
    public void addPlayer(String playerColor, String username, int gameID) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
