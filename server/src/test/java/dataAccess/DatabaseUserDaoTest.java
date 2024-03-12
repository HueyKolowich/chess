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
    void getUser() {
    }

    @Test
    void checkPassword() {
    }

    @Test
    void createUser() throws DataAccessException {
        UserData user = new UserData("testUser", "12345678", "testUser@fake.org");
        Assertions.assertDoesNotThrow(() -> databaseUserDao.createUser(user));

        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM user WHERE username = 'testUser'";

            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            throw new DataAccessException(String.format("Unable to configure database: %s", sqlException.getMessage()));
        }
    }

    @Test
    void clear() {
    }
}