package dataAccess;

import chess.model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;

public class DatabaseUserDao extends DatabaseDao implements UserDao {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public DatabaseUserDao() throws DataAccessException {
        configureDatabase(createStatements);
    }

    /**
     * Find user in the database
     *
     * @param username User being looked for
     * @return Username string if found, null otherwise
     * @throws DataAccessException If issue with DB connection
     */
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

    /**
     * Checks to see if a given password matches that of a registered user
     *
     * @param user The user for which to check the password
     * @return True if a match, false otherwise
     * @throws DataAccessException If issue with the DB connection
     */
    @Override
    public boolean checkPassword(UserData user) throws DataAccessException {
        String hashedStoredPassword = getHashedStoredPassword(user);

        return bCryptPasswordEncoder.matches(user.password(), hashedStoredPassword);
    }

    /**
     * Inserts user object into the database
     *
     * @param user The user to be inserted
     * @throws DataAccessException If issue with the DB connection
     */
    @Override
    public void createUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        String hashedPassword = bCryptPasswordEncoder.encode(user.password());

        executeUpdate(statement, user.username(), hashedPassword, user.email());
    }

    /**
     * Clears all users data in the database
     *
     * @throws DataAccessException If issue with the DB connection
     */
    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE user";
        executeUpdate(statement);
    }

    /**
     * Gets the hashed password for a user in the database
     *
     * @param user The user for whose password is being looked for
     * @return The hashed password for a user in the database
     * @throws DataAccessException If issue with the DB connection
     */
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

    /**
     * Will execute scripts that affect the state of the DB, cleans the script before execution
     *
     * @param statement The script to be run
     * @param params Objects to be parsed into the script if needed
     * @throws DataAccessException If issue with the DB connection
     */
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

    /**
     * Creates the user table if missing
     *
     * @throws DataAccessException If issue with the DB connection
     */
//    private void configureDatabase() throws DataAccessException {
//        DatabaseManager.createDatabase();
//        try (Connection connection = DatabaseManager.getConnection()) {
//            for (String statement : createStatements) {
//                try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException sqlException) {
//            throw new DataAccessException(String.format("Unable to configure database: %s", sqlException.getMessage()));
//        }
//    }
}
