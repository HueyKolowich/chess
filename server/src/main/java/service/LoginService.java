package service;

import chess.model.UserData;
import dataAccess.AuthDao;
import dataAccess.MemoryAuthDao;
import dataAccess.MemoryUserDao;
import dataAccess.UserDao;
import service.resultRecords.AuthResult;
import service.serviceExceptions.UnauthorizedAuthException;

public class LoginService {
    private final UserDao userDao = new MemoryUserDao();
    private final AuthDao authDao = new MemoryAuthDao();
    public AuthResult login(UserData user) throws UnauthorizedAuthException {
        if (userDao.getUser(user.username()) != null) {
            if (userDao.checkPassword(user)) {
                return new AuthResult(user.username(), authDao.createAuth(user.username()));
            } else {
                throw new UnauthorizedAuthException("Error: unauthorized");
            }
        } else {
            throw new UnauthorizedAuthException("Error: unauthorized");
        }
    }
}
