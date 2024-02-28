package dataAccess;

import chess.model.UserData;

public interface AuthDao {
    String createAuth(String username);
    boolean verifyAuth(String authToken);
    void clearAuth(String authToken) throws DataAccessException;
    void clear();
}