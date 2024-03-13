package dataAccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseGameDaoTest {
    private static DatabaseGameDao databaseGameDao;

    @BeforeAll
    public static void init() throws DataAccessException {
        databaseGameDao = new DatabaseGameDao();
    }

    @Test
    void createGame() throws DataAccessException {
        temporaryTestScript("DELETE FROM game WHERE gameID = '9999'");

        Assertions.assertEquals(9999, databaseGameDao.createGame(9999, "testGame"));
        Assertions.assertThrows(DataAccessException.class, () -> databaseGameDao.createGame(9999, "otherGame"));

//        temporaryTestScript("DELETE FROM game WHERE gameID = '9999'");
    }

    @Test
    void listGames() {
    }

    @Test
    void findGame() {
    }

    @Test
    void addPlayer() {
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