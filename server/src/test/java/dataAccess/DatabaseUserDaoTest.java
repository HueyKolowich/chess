package dataAccess;

import chess.model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.parameters.P;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseUserDaoTest {
    private static DatabaseUserDao databaseUserDao;

    @BeforeAll
    public static void init() throws DataAccessException {
        databaseUserDao = new DatabaseUserDao();
    }

    @Test
    void getUser() throws DataAccessException {
        temporaryTestScript("DELETE FROM user WHERE username = 'testUser'");
        temporaryTestScript("INSERT INTO user (username, password, email) VALUES (\"testUser\", \"12345678\", \"testUser@fake.org\")");
        Assertions.assertEquals("testUser", databaseUserDao.getUser("testUser"));

        temporaryTestScript("DELETE FROM user WHERE username = 'testUser'");
        assertNull(databaseUserDao.getUser("testUser"));
    }

    @Test
    void checkPassword() throws DataAccessException {
        temporaryTestScript("DELETE FROM user WHERE username = 'testUser'");
        UserData user = new UserData("testUser", "12345678", "testUser@fake.org");
        databaseUserDao.createUser(user);
        assertTrue(databaseUserDao.checkPassword(user));

        UserData badPasswordUser = new UserData("testUser", "abcdefg", "testUser@fake.org");
        assertFalse(databaseUserDao.checkPassword(badPasswordUser));
    }

    @Test
    void createUser() throws DataAccessException {
        temporaryTestScript("DELETE FROM user WHERE username = 'testUser'");
        UserData user = new UserData("testUser", "12345678", "testUser@fake.org");
        Assertions.assertDoesNotThrow(() -> databaseUserDao.createUser(user));

        temporaryTestScript("DELETE FROM user WHERE username = 'testUser'");
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