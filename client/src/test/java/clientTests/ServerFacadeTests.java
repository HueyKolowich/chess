package clientTests;

import client.ChessClient;
import org.junit.jupiter.api.*;
import server.Server;
import dataAccess.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServerFacadeTests {
    private static Server server;
    private static ChessClient chessClient;

    private String loggedOutHelpSourceOfTruth() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - login <USERNAME> <PASSWORD> - to play chess
                - quit - playing chess
                - help - with possible commands
                """;
    }

    private String loggedInHelpSourceOfTruth() {
        return """
                - create <NAME> - a game
                - list - games
                - join <ID> [WHITE | BLACK | <empty>] - a game
                - observe <ID> - a game
                - logout - when you are done
                - quit - playing chess
                - help - with possible commands
                """;
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(5051);
        System.out.println("Started test HTTP server on " + port);

        chessClient = new ChessClient("http://localhost:5051");
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        temporaryTestScript("DELETE FROM user WHERE username = 'testUser'");
        temporaryTestScript("DELETE FROM game WHERE gameName = 'testGame'");
        temporaryTestScript("DELETE FROM game WHERE gameName = 'testGame2'");

        chessClient.setIsLoggedIn(false);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @AfterEach
    public void cleanup() throws DataAccessException {
        temporaryTestScript("DELETE FROM user WHERE username = 'testUser'");
        temporaryTestScript("DELETE FROM game WHERE gameName = 'testGame'");
        temporaryTestScript("DELETE FROM game WHERE gameName = 'testGame2'");

        chessClient.setIsLoggedIn(false);
    }

    @Test
    public void loggedOutHelpPositive() {
        Assertions.assertEquals(this.loggedOutHelpSourceOfTruth(), chessClient.eval("help"));
    }
    @Test
    public void loggedOutHelpNegative() {
        Assertions.assertDoesNotThrow(() -> chessClient.eval("help"));
    }

    @Test
    public void registerPositive() throws DataAccessException {
        Assertions.assertEquals("", chessClient.eval("register testUser 1 email"));
    }
    @Test
    public void registerNegative() throws DataAccessException {
        temporaryTestScript("INSERT INTO user (username, password, email) VALUES ('testUser', '1', 'email')");

        Assertions.assertEquals("Error: already taken\n", chessClient.eval("register testUser 1 email"));
    }

    @Test
    public void loginPositive() throws DataAccessException {
        chessClient.eval("register testUser 1 email");
        chessClient.setIsLoggedIn(false);

        Assertions.assertEquals("", chessClient.eval("login testUser 1"));
    }
    @Test
    public void loginNegative() {
        chessClient.eval("register testUser 1 email");
        chessClient.setIsLoggedIn(false);

        Assertions.assertEquals("Error: unauthorized\n", chessClient.eval("login testUser 2"));
    }

    @Test
    public void loggedInHelpPositive() {
        chessClient.setIsLoggedIn(true);

        Assertions.assertEquals(this.loggedInHelpSourceOfTruth(), chessClient.eval("help"));
    }
    @Test
    public void loggedInHelpNegative() {
        chessClient.setIsLoggedIn(true);

        Assertions.assertDoesNotThrow(() -> chessClient.eval("help"));
    }

    @Test
    public void observePositive() {
        chessClient.eval("register testUser 1 email");
        chessClient.eval("create testGame");
        int gameNumber = chessClient.getCurrentPositionInGameNumberingSeries() - 1;

        Assertions.assertEquals("", chessClient.eval("observe " + gameNumber));
    }
    @Test
    public void observeNegative() {
        chessClient.eval("register testUser 1 email");
        chessClient.eval("create testGame");
        int gameNumber = chessClient.getCurrentPositionInGameNumberingSeries() - 1;
        gameNumber++;

        Assertions.assertEquals("Error: bad request\n", chessClient.eval("observe " + gameNumber));
    }

    @Test
    public void joinPositive() {
        chessClient.eval("register testUser 1 email");
        chessClient.eval("create testGame");
        int gameNumber = chessClient.getCurrentPositionInGameNumberingSeries() - 1;

        Assertions.assertEquals("", chessClient.eval("join " + gameNumber + " WHITE"));
    }
    @Test
    public void joinNegative() {
        chessClient.eval("register testUser 1 email");
        chessClient.eval("create testGame");
        int gameNumber = chessClient.getCurrentPositionInGameNumberingSeries() - 1;

        Assertions.assertEquals("Error: bad request\n", chessClient.eval("join " + gameNumber + " BLUE"));

        gameNumber++;

        Assertions.assertEquals("Error: bad request\n", chessClient.eval("join " + gameNumber + " WHITE"));
    }

    @Test
    public void listPositive() {
        chessClient.eval("register testUser 1 email");
        chessClient.eval("create testGame");
        chessClient.eval("create testGame2");

        Assertions.assertTrue(chessClient.eval("list").contains("testgame") && chessClient.eval("list").contains("testgame2"));
    }
    @Test
    public void listNegative() {
        chessClient.eval("register testUser 1 email");

        Assertions.assertEquals("", chessClient.eval("list"));

        chessClient.eval("create testGame");

        Assertions.assertNotNull(chessClient.eval("list"));
    }

    @Test
    public void createPositive() {
        chessClient.eval("register testUser 1 email");
        int gameNumber = chessClient.getCurrentPositionInGameNumberingSeries();

        Assertions.assertEquals("Created game number: " + gameNumber + "\n", chessClient.eval("create testGame"));
    }
    @Test
    public void createNegative() {
        chessClient.eval("register testUser 1 email");
        int gameNumber = chessClient.getCurrentPositionInGameNumberingSeries();

        Assertions.assertEquals("Unable to update database: No value specified for parameter 2\n", chessClient.eval("create"));
    }

    @Test
    public void logoutPositive() {
        chessClient.eval("register testUser 1 email");

        Assertions.assertEquals("", chessClient.eval("logout"));
    }
    @Test
    public void logoutNegative() {
        chessClient.eval("register testUser 1 email");
        chessClient.setIsLoggedIn(false);

        Assertions.assertNotEquals("", chessClient.eval("logout"));
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
