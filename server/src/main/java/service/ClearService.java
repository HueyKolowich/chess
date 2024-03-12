package service;

import dataAccess.*;

public class ClearService {
    private final UserDao userDao;
    private final AuthDao authDao = new MemoryAuthDao();
    private final GameDao gameDao = new MemoryGameDao();

    {
        try {
            userDao = new DatabaseUserDao();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() throws DataAccessException {
        userDao.clear();
        authDao.clear();
        gameDao.clear();
    }
}