package clientTests;

import client.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;
import dataAccess.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade serverFacade;

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
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new ServerFacade("http://localhost:" + port, null);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        temporaryTestScript("DELETE FROM user WHERE username = 'testUser'");
        temporaryTestScript("DELETE FROM game WHERE gameName = 'testGame'");
        temporaryTestScript("DELETE FROM game WHERE gameName = 'testGame2'");

        serverFacade.setIsLoggedIn(false);
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

        serverFacade.setIsLoggedIn(false);
    }

    @Test
    public void loggedOutHelpPositive() {
        Assertions.assertEquals(this.loggedOutHelpSourceOfTruth(), serverFacade.eval("help"));
    }
    @Test
    public void loggedOutHelpNegative() {
        Assertions.assertDoesNotThrow(() -> serverFacade.eval("help"));
    }

    @Test
    public void registerPositive() throws DataAccessException {
        Assertions.assertEquals("", serverFacade.eval("register testUser 1 email"));
    }
    @Test
    public void registerNegative() throws DataAccessException {
        temporaryTestScript("INSERT INTO user (username, password, email) VALUES ('testUser', '1', 'email')");

        Assertions.assertEquals("Error: already taken\n", serverFacade.eval("register testUser 1 email"));
    }

    @Test
    public void loginPositive() throws DataAccessException {
        serverFacade.eval("register testUser 1 email");
        serverFacade.setIsLoggedIn(false);

        Assertions.assertEquals("", serverFacade.eval("login testUser 1"));
    }
    @Test
    public void loginNegative() {
        serverFacade.eval("register testUser 1 email");
        serverFacade.setIsLoggedIn(false);

        Assertions.assertEquals("Error: unauthorized\n", serverFacade.eval("login testUser 2"));
    }

    @Test
    public void loggedInHelpPositive() {
        serverFacade.setIsLoggedIn(true);

        Assertions.assertEquals(this.loggedInHelpSourceOfTruth(), serverFacade.eval("help"));
    }
    @Test
    public void loggedInHelpNegative() {
        serverFacade.setIsLoggedIn(true);

        Assertions.assertDoesNotThrow(() -> serverFacade.eval("help"));
    }

    @Test
    public void observePositive() {
        serverFacade.eval("register testUser 1 email");
        serverFacade.eval("create testGame");
        int gameNumber = serverFacade.getCurrentPositionInGameNumberingSeries() - 1;

        Assertions.assertEquals("", serverFacade.eval("observe " + gameNumber));
    }
    @Test
    public void observeNegative() {
        serverFacade.eval("register testUser 1 email");
        serverFacade.eval("create testGame");
        int gameNumber = serverFacade.getCurrentPositionInGameNumberingSeries() - 1;
        gameNumber++;

        Assertions.assertEquals("Error: bad request\n", serverFacade.eval("observe " + gameNumber));
    }

    @Test
    public void joinPositive() {
        serverFacade.eval("register testUser 1 email");
        serverFacade.eval("create testGame");
        int gameNumber = serverFacade.getCurrentPositionInGameNumberingSeries() - 1;

        Assertions.assertEquals("", serverFacade.eval("join " + gameNumber + " WHITE"));
    }
    @Test
    public void joinNegative() {
        serverFacade.eval("register testUser 1 email");
        serverFacade.eval("create testGame");
        int gameNumber = serverFacade.getCurrentPositionInGameNumberingSeries() - 1;

        Assertions.assertEquals("Error: bad request\n", serverFacade.eval("join " + gameNumber + " BLUE"));

        gameNumber++;

        Assertions.assertEquals("Error: bad request\n", serverFacade.eval("join " + gameNumber + " WHITE"));
    }

    @Test
    public void listPositive() {
        serverFacade.eval("register testUser 1 email");
        serverFacade.eval("create testGame");
        serverFacade.eval("create testGame2");

        Assertions.assertTrue(serverFacade.eval("list").contains("testgame") && serverFacade.eval("list").contains("testgame2"));
    }
    @Test
    public void listNegative() {
        serverFacade.eval("register testUser 1 email");
        int gameNumber = serverFacade.getCurrentPositionInGameNumberingSeries();

        Assertions.assertFalse(serverFacade.eval("list").contains(String.valueOf(gameNumber + 1)));

        serverFacade.eval("create testGame");

        Assertions.assertNotNull(serverFacade.eval("list"));
    }

    @Test
    public void createPositive() {
        serverFacade.eval("register testUser 1 email");
        int gameNumber = serverFacade.getCurrentPositionInGameNumberingSeries();

        Assertions.assertEquals("Created game number: " + gameNumber + "\n", serverFacade.eval("create testGame"));
    }
    @Test
    public void createNegative() {
        serverFacade.eval("register testUser 1 email");

        Assertions.assertEquals("Unable to update database: No value specified for parameter 2\n", serverFacade.eval("create"));
    }

    @Test
    public void logoutPositive() {
        serverFacade.eval("register testUser 1 email");

        Assertions.assertEquals("", serverFacade.eval("logout"));
    }
    @Test
    public void logoutNegative() {
        serverFacade.eval("register testUser 1 email");
        serverFacade.setIsLoggedIn(false);

        Assertions.assertNotEquals("", serverFacade.eval("logout"));
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
