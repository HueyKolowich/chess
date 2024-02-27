package dataAccess;

import chess.model.AuthData;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDao implements AuthDao {
    static HashMap<String, AuthData> auths = new HashMap<>();
    @Override
    public String createAuth(String username) {
        String newAuthToken = UUID.randomUUID().toString();

        MemoryAuthDao.auths.put(username, new AuthData(newAuthToken, username));
        return newAuthToken;
    }

    @Override
    public void clear() {
        MemoryAuthDao.auths.clear();
    }
}