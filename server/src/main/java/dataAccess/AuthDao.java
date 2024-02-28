package dataAccess;

import chess.model.UserData;

public interface AuthDao {
    String createAuth(String username);
    void clearAuth(String authToken);
    void clear();
}