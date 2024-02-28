package dataAccess;

import chess.model.UserData;

public interface AuthDao {
    String createAuth(String username);
    String getUsernameByAuth(String authToken) throws DataAccessException;
    boolean verifyAuth(String authToken);
    void clearAuth(String authToken) throws DataAccessException;
    void clear();
}