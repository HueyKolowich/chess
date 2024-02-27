package dataAccess;

import chess.model.UserData;

public interface UserDao {
    String getUser(String username);
    void createUser(UserData user);
    void clear();
}