package dataAccess;

import chess.ChessGame;
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

    /**
     * Creates a new game in memory
     * (Usernames are initialized to null and a new ChessGame object is generated)
     *
     * @param gameID ID for the game to be generated
     * @param gameName Name for the game to be generated
     * @return int gameID
     * @throws DataAccessException If issue with the DB connection
     */
    @Override
    public int createGame(int gameID, String gameName) throws DataAccessException {
        String statement = "INSERT INTO game (gameID, gameName, game) VALUES (?, ?, ?)";
        String game = new Gson().toJson(new ChessGame());

        executeUpdate(statement, gameID, gameName, game);

        if (selectItem("SELECT gameID FROM game WHERE gameID=?", (Integer) gameID)) {
            return gameID;
        } else { throw new DataAccessException("Game was not created!"); }
    }

    /**
     * Formats and returns the games in memory
     *
     * @return The games in memory
     * @throws DataAccessException If issue with the DB connection
     */
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

    /**
     * Finds a game by gameID in memory
     *
     * @param gameID for which to search for the game
     * @return True if game is found, false otherwise
     * @throws DataAccessException If issue with the DB connection
     */
    @Override
    public boolean findGame(int gameID) throws DataAccessException {
        return selectItem("SELECT gameID FROM game WHERE gameID=?", gameID);
    }

    /**
     * Adds a player's username to the game
     *
     * @param playerColor The color the user wishes to join as
     * @param username String the username of the user that is joining
     * @param gameID int the game to join
     * @throws DataAccessException if a playerColor is already taken for the specified color, or if issue with DB connection
     */
    @Override
    public void addPlayer(String playerColor, String username, int gameID) throws DataAccessException {
        if (playerColor.equalsIgnoreCase("WHITE")) {
            try (Connection connection = DatabaseManager.getConnection()) {
                String statement = "SELECT whiteUsername FROM game WHERE gameID=?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.setInt(1, gameID);

                    preparedStatement.executeQuery();

                    ResultSet resultSet = preparedStatement.getResultSet();
                    if (resultSet.next()) {
                        if (resultSet.getString("whiteUsername") != null) {
                            throw new DataAccessException("playerColor already taken");
                        }

                        executeUpdate("UPDATE game SET whiteUsername=? WHERE gameID=?", username, gameID);
                    }
                }
            } catch (SQLException sqlException) {
                throw new DataAccessException(String.format("Unable to get DB connection: %s", sqlException.getMessage()));
            }
        } else {
            try (Connection connection = DatabaseManager.getConnection()) {
                String statement = "SELECT blackUsername FROM game WHERE gameID=?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.setInt(1, gameID);

                    preparedStatement.executeQuery();

                    ResultSet resultSet = preparedStatement.getResultSet();
                    if (resultSet.next()) {
                        if (resultSet.getString("blackUsername") != null) {
                            throw new DataAccessException("playerColor already taken");
                        }

                        executeUpdate("UPDATE game SET blackUsername=? WHERE gameID=?", username, gameID);
                    }
                }
            } catch (SQLException sqlException) {
                throw new DataAccessException(String.format("Unable to get DB connection: %s", sqlException.getMessage()));
            }
        }
    }

    /**
     * Clears all users data in memory
     */
    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE auth";
        executeUpdate(statement);
    }
}
