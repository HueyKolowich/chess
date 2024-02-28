package dataAccess;

import chess.model.AuthData;
import chess.model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDao implements AuthDao {
    static HashMap<String, AuthData> auths = new HashMap<>();

    /**
     * Creates and stores a new authToken string
     * (if authToken already exists for a user then it is replaced)
     *
     * @param username User for which the authToken will be generated
     * @return The new authToken
     */
    @Override
    public String createAuth(String username) {
        String newAuthToken = UUID.randomUUID().toString();

        MemoryAuthDao.auths.put(newAuthToken, new AuthData(newAuthToken, username));
        return newAuthToken;
    }

    @Override
    public String getUsernameByAuth(String authToken) throws DataAccessException {
        if (!MemoryAuthDao.auths.containsKey(authToken)) {
            throw new DataAccessException("No username associated with this authToken");
        }

        return MemoryAuthDao.auths.get(authToken).username();
    }

    @Override
    public boolean verifyAuth(String authToken) {
        return MemoryAuthDao.auths.containsKey(authToken);
    }

    /**
     * Removes the authToken from memory
     *
     * @param authToken The authToken to remove
     * @throws DataAccessException If the authToken is not found in memory
     */
    @Override
    public void clearAuth(String authToken) throws DataAccessException {
        if (!MemoryAuthDao.auths.containsKey(authToken)) {
            throw new DataAccessException("No authToken match found!");
        }

        MemoryAuthDao.auths.remove(authToken);
    }

    /**
     * Clears all users data in memory
     */
    @Override
    public void clear() {
        MemoryAuthDao.auths.clear();
    }
}