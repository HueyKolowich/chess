package dataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        return null;
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
