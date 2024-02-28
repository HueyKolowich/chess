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

        MemoryAuthDao.auths.put(username, new AuthData(newAuthToken, username));
        return newAuthToken;
    }

    @Override
    public void clearAuth(String authToken) {
        for (String username : MemoryAuthDao.auths.keySet()) {
            if (MemoryAuthDao.auths.get(username).authToken().equals(authToken)) {
                MemoryAuthDao.auths.remove(username);
                break;
            }
        }
        //TODO should be a real problem if I get here
    }

    /**
     * Clears all users data in memory
     */
    @Override
    public void clear() {
        MemoryAuthDao.auths.clear();
    }
}