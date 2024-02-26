package dataAccess;

import chess.model.UserData;

public interface UserDao {
    String getUser(String username);
    String createAuth(String username);
}