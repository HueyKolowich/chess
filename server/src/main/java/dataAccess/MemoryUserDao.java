package dataAccess;

import chess.model.UserData;

import java.util.HashMap;

public class MemoryUserDao implements UserDao {
    static HashMap<String, UserData> users = new HashMap<>();

    /**
     * Find user in memory
     *
     * @param username User being looked for
     * @return Username string if found, null otherwise
     */
    @Override
    public String getUser(String username) {
        if (MemoryUserDao.users.containsKey(username)) {
            return username;
        } else {
            return null;
        }
    }

    /**
     * Inserts user object into memory
     *
     * @param user The user to be inserted
     */
    @Override
    public void createUser(UserData user) {
        MemoryUserDao.users.put(user.username(), user);
    }

    @Override
    public boolean checkPassword(UserData user) {
        if (user.password().equals(MemoryUserDao.users.get(user.username()).password())) {
            return true;
        } else { return false; }
    }

    /**
     * Clears all users data in memory
     */
    @Override
    public void clear() { MemoryUserDao.users.clear(); }
}