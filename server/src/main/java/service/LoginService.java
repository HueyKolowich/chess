package service;

import chess.model.UserData;
import dataAccess.AuthDao;
import dataAccess.MemoryAuthDao;
import dataAccess.MemoryUserDao;
import dataAccess.UserDao;
import service.resultRecords.AuthResult;

public class LoginService {
    private final UserDao userDao = new MemoryUserDao();
    private final AuthDao authDao = new MemoryAuthDao();
    public AuthResult login(UserData user) {
        if (userDao.getUser(user.username()) != null) {
            if (userDao.checkPassword(user)) {
                return new AuthResult(user.username(), authDao.createAuth(user.username()));
            } else {
                //TODO Throw an exception here that shows that the password was incorrect
            }
        } else {
            //TODO Throw an exception here that shows that there was no instance of this username
        }

        return null;
    }
}
