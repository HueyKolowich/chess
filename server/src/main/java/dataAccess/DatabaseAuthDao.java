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
     * @param username
     * @return
     */
    @Override
    public String createAuth(String username) throws DataAccessException {
        String newAuthToken = UUID.randomUUID().toString();
        String statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";

        executeUpdate(statement, username, newAuthToken);
        return newAuthToken;
    }

    /**
     * @param authToken
     * @return
     * @throws DataAccessException
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
     * @param authToken
     * @return
     */
    @Override
    public boolean verifyAuth(String authToken) {
        return false;
    }

    /**
     * @param authToken
     * @throws DataAccessException
     */
    @Override
    public void clearAuth(String authToken) throws DataAccessException {

    }

    /**
     *
     */
    @Override
    public void clear() {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
                `username` varchar(256) NOT NULL,
                `authToken` varchar(256) NOT NULL,
                PRIMARY KEY (`username`)
            )
            """
    };
}
