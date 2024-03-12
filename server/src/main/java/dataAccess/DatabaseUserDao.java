package dataAccess;

import chess.model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;

public class DatabaseUserDao implements UserDao {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public DatabaseUserDao() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public String getUser(String username) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "SELECT username FROM user WHERE username=?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString(1);
                    }
                }
            }
        } catch (SQLException sqlException) {
            throw new DataAccessException(String.format("Unable to get user: %s", sqlException.getMessage()));
        }

        return null;
    }

    @Override
    public boolean checkPassword(UserData user) throws DataAccessException {
        String hashedStoredPassword = getHashedStoredPassword(user);

        return bCryptPasswordEncoder.matches(user.password(), hashedStoredPassword);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        String hashedPassword = bCryptPasswordEncoder.encode(user.password());

        executeUpdate(statement, user.username(), hashedPassword, user.email());
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE user";
        executeUpdate(statement);
    }

    private static String getHashedStoredPassword(UserData user) throws DataAccessException {
        String hashedStoredPassword = null;

        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "SELECT password FROM user WHERE username=?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, user.username());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        hashedStoredPassword = resultSet.getString(1);
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Unable to get password: %s", e.getMessage()));
        }
        return hashedStoredPassword;
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
            throw new DataAccessException(String.format("Unable to update database: %s", sqlException.getMessage()));
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
