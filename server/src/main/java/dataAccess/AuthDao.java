package dataAccess;

public interface AuthDao {
    String createAuth(String username) throws DataAccessException;
    String getUsernameByAuth(String authToken) throws DataAccessException;
    boolean verifyAuth(String authToken);
    void clearAuth(String authToken) throws DataAccessException;
    void clear();
}