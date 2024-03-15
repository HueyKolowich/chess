package service;

import dataAccess.*;

public class ClearService {
    private final UserDao userDao;
    private final AuthDao authDao;
    private final GameDao gameDao;

    {
        try {
            userDao = new DatabaseUserDao();
            authDao = new DatabaseAuthDao();
            gameDao = new DatabaseGameDao();
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