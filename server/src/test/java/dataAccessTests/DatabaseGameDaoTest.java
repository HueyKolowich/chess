package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseGameDao;
import dataAccess.DatabaseManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

        temporaryTestScript("DELETE FROM game WHERE gameID = '9999'");
    }

    @Test
    void listGames() throws DataAccessException {
        temporaryTestScript("INSERT INTO game (gameID, gameName, gameStatus, game) VALUES (9999, \"testGame\", 1, \"PLACEHOLDER\")");
        temporaryTestScript("INSERT INTO game (gameID, gameName, gameStatus, game) VALUES (9998, \"testGame\", 1, \"PLACEHOLDER\")");
        temporaryTestScript("INSERT INTO game (gameID, gameName, gameStatus, game) VALUES (9997, \"testGame\", 1, \"PLACEHOLDER\")");

        Assertions.assertDoesNotThrow(() -> databaseGameDao.listGames());

        temporaryTestScript("DELETE FROM game WHERE gameID = '9999'");
        temporaryTestScript("DELETE FROM game WHERE gameID = '9998'");
        temporaryTestScript("DELETE FROM game WHERE gameID = '9997'");
    }

    @Test
    void findGame() throws DataAccessException {
        temporaryTestScript("DELETE FROM game WHERE gameID = '9999'");
        temporaryTestScript("INSERT INTO game (gameID, gameName, gameStatus, game) VALUES (9999, \"testGame\", 1, \"PLACEHOLDER\")");

        Assertions.assertTrue(databaseGameDao.findGame(9999));

        temporaryTestScript("DELETE FROM game WHERE gameID = '9999'");

        Assertions.assertFalse(databaseGameDao.findGame(9999));

        temporaryTestScript("DELETE FROM game WHERE gameID = '9999'");
    }

    @Test
    void addPlayer() throws DataAccessException {
        temporaryTestScript("DELETE FROM game WHERE gameID = '9999'");
        temporaryTestScript("INSERT INTO game (gameID, gameName, gameStatus, game) VALUES (9999, \"testGame\", 1, \"PLACEHOLDER\")");

        Assertions.assertDoesNotThrow(() -> databaseGameDao.addPlayer("WHITE", "testUser", 9999));
        Assertions.assertDoesNotThrow(() -> databaseGameDao.addPlayer("BLACK", "testUser", 9999));

        temporaryTestScript("DELETE FROM game WHERE gameID = '9999'");
    }

    @Test
    void clear() throws DataAccessException {
        temporaryTestScript("DELETE FROM game WHERE gameID = '9999'");
        temporaryTestScript("DELETE FROM game WHERE gameID = '9998'");
        temporaryTestScript("INSERT INTO game (gameID, gameName, gameStatus, game) VALUES (9999, \"testGame\", 1, \"PLACEHOLDER\")");
        temporaryTestScript("INSERT INTO game (gameID, gameName, gameStatus, game) VALUES (9998, \"testGame\", 1, \"PLACEHOLDER\")");

        Assertions.assertDoesNotThrow(() -> databaseGameDao.clear());

        temporaryTestScript("DELETE FROM game WHERE gameID = '9999'");
        temporaryTestScript("DELETE FROM game WHERE gameID = '9998'");
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