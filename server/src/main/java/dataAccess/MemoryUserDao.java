package dataAccess;

import chess.model.UserData;

public class MemoryUserDao implements UserDao {
    @Override
    public String getUser(String username) {
        return null;
    }

    @Override
    public String createAuth(String username) {
        return "Really cool authToken";
    }
}