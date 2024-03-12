package dataAccess;

import chess.model.UserData;

public interface UserDao {
    String getUser(String username) throws DataAccessException;
    boolean checkPassword(UserData user) throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    void clear();
}