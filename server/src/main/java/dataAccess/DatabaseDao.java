package dataAccess;

import java.sql.*;

public class DatabaseDao {
    /**
     * Creates the user table if missing
     *
     * @throws DataAccessException If issue with the DB connection
     */
    protected void configureDatabase(String[] createStatements) throws DataAccessException {
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

    /**
     * Will execute scripts that affect the state of the DB, cleans the script before execution
     *
     * @param statement The script to be run
     * @param params Objects to be parsed into the script if needed
     * @throws DataAccessException If issue with the DB connection
     */
    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) { preparedStatement.setString(i + 1, p); }
                    else if (param instanceof Integer integer) { preparedStatement.setInt(i + 1, integer); }
                }

                preparedStatement.executeUpdate();

                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }

                return 0;
            }
        } catch (SQLException sqlException) {
            throw new DataAccessException(String.format("Unable to update database: %s", sqlException.getMessage()));
        }
    }

    /**
     * Queries the occurrence of row(s) in DB
     *
     * @param statement SELECT query to be used
     * @param whereItem Object for WHERE clause (can be Integer or String)
     * @return True if row(s) exists, false otherwise
     * @throws DataAccessException If issue with DB connection
     */
    protected boolean selectItem(String statement, Object whereItem) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                if (whereItem instanceof String wi) { preparedStatement.setString(1, wi); }
                else if (whereItem instanceof Integer wi) { preparedStatement.setInt(1, wi); }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException sqlException) {
            throw new DataAccessException(String.format("Unable to get user: %s", sqlException.getMessage()));
        }

        return false;
    }
}
