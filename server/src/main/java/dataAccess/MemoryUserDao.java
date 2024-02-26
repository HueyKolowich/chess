package dataAccess;

import chess.model.UserData;

import java.util.HashMap;
import java.util.HashSet;

public class MemoryUserDao implements UserDao {
    static HashMap<String, UserData> users = new HashMap<>();

    /**
    * Returns username string if the username is found in memory, null otherwise
    * */
    @Override
    public String getUser(String username) {
        if (users.containsKey(username)) {
            return null;
        } else {
            return username;
        }
    }

    @Override
    public String createAuth(String username) {
        return "Really cool authToken";
    }
}