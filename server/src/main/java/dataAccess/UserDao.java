package dataAccess;

import chess.model.UserData;

public interface UserDao {
    String getUser(String username);
    boolean checkPassword(UserData user);
    void createUser(UserData user) throws DataAccessException;
    void clear();
}