package service;

import dataAccess.*;

public class ClearService {
    private final UserDao userDao = new MemoryUserDao();
    private final AuthDao authDao = new MemoryAuthDao();
    private final GameDao gameDao = new MemoryGameDao();
    public void delete() throws DataAccessException {
        userDao.clear();
        authDao.clear();
        gameDao.clear();
    }
}