package dataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseAuthDao extends DatabaseDao implements AuthDao {
    public DatabaseAuthDao() throws DataAccessException {
        configureDatabase(createStatements);
    }

    /**
     * Creates and stores a new authToken string
     * (if authToken already exists for a user then it is replaced)
     *
     * @param username username User for which the authToken will be generated
     * @return The new authToken
     * @throws DataAccessException If issue with DB connection
     */
    @Override
    public String createAuth(String username) throws DataAccessException {
        String newAuthToken = UUID.randomUUID().toString();
        String statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";

        executeUpdate(statement, username, newAuthToken);
        return newAuthToken;
    }

    /**
     * Find the username for a given authToken
     *
     * @param authToken authToken which provides the key for the username search
     * @return String username
     * @throws DataAccessException If issue with DB connection
     */
    @Override
    public String getUsernameByAuth(String authToken) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "SELECT username FROM auth WHERE authToken=?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString(1);
                    }
                }
            }
        } catch (SQLException sqlException) {
            throw new DataAccessException(String.format("Unable to get user: %s", sqlException.getMessage()));
        }

        throw new DataAccessException("No username associated with this authToken");
    }

    /**
     * Verifies that an authToken is held in the DB
     *
     * @param authToken the authToken being searched for
     * @return True if found, false otherwise
     * @throws DataAccessException If issue with DB connection
     */
    @Override
    public boolean verifyAuth(String authToken) throws DataAccessException {
        return selectItem("SELECT authToken FROM auth WHERE authToken=?", authToken);
    }

    /**
     * Removes occurrence of authToken in DB
     *
     * @param authToken The authToken to be removed
     * @throws DataAccessException If no authToken match is found, or if issue with DB connection
     */
    @Override
    public void clearAuth(String authToken) throws DataAccessException {
        if (!selectItem("SELECT authToken FROM auth WHERE authToken=?", authToken)) {
            throw new DataAccessException("No authToken match found!");
        }

        String statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    /**
     * Clears DB auth table
     *
     * @throws DataAccessException If issue with DB connection
     */
    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE game";
        executeUpdate(statement);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
                `id` int NOT NULL AUTO_INCREMENT,
                `username` varchar(256) NOT NULL,
                `authToken` varchar(256) NOT NULL,
                PRIMARY KEY (`id`),
                INDEX (`username`),
                INDEX (`authToken`)
            )
            """

            //`id` int NOT NULL AUTO_INCREMENT, (Primary Key)
    };
}
