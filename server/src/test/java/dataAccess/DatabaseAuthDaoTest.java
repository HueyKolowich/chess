package dataAccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseAuthDaoTest {
    private static DatabaseAuthDao databaseAuthDao;

    @BeforeAll
    public static void init() throws DataAccessException {
        databaseAuthDao = new DatabaseAuthDao();
    }

    @Test
    void createAuth() throws DataAccessException {
        temporaryTestScript("DELETE FROM auth WHERE username = 'testUser'");

        Assertions.assertNotNull(databaseAuthDao.createAuth("testUser"));

        temporaryTestScript("DELETE FROM auth WHERE username = 'testUser'");
    }

    @Test
    void getUsernameByAuth() throws DataAccessException {
        temporaryTestScript("DELETE FROM auth WHERE username = 'testUser'");
        temporaryTestScript("INSERT INTO auth (username, authToken) VALUES (\"testUser\", \"9946bad9-5210-44b2-a798-6a8ecee68948\")");

        Assertions.assertEquals("testUser", databaseAuthDao.getUsernameByAuth("9946bad9-5210-44b2-a798-6a8ecee68948"));
        Assertions.assertThrows(DataAccessException.class, () -> databaseAuthDao.getUsernameByAuth("9946bad9-5210-44b2-a798-aaaaaaaaaaaa"));

        temporaryTestScript("DELETE FROM auth WHERE username = 'testUser'");
    }

    @Test
    void verifyAuth() {
    }

    @Test
    void clearAuth() {
    }

    @Test
    void clear() {
    }

    private void temporaryTestScript(String statement) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            throw new DataAccessException(String.format("Unable to configure database: %s", sqlException.getMessage()));
        }
    }
}