package dataAccess;

import chess.model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUserDao implements UserDao {
    public DatabaseUserDao() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public String getUser(String username) {
        return null;
    }

    @Override
    public boolean checkPassword(UserData user) {
        return false;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), user.password(), user.email());
    }

    @Override
    public void clear() {

    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) { preparedStatement.setString(i + 1, p); }
                }

                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            throw new DataAccessException(String.format("Unable to configure database: %s", sqlException.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
                `username` varchar(256) NOT NULL,
                `password` varchar(256) NOT NULL,
                `email` varchar(256) NOT NULL,
                PRIMARY KEY (`username`)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException sqlException) {
            throw new DataAccessException(String.format("Unable to configure database: %s", sqlException.getMessage()));
        }
    }
}
