package service;

import chess.model.UserData;
import dataAccess.*;
import service.resultRecords.AuthResult;
import service.serviceExceptions.UnauthorizedAuthException;

public class LoginService {
    private final UserDao userDao = new MemoryUserDao();
    private final AuthDao authDao = new MemoryAuthDao();

    /**
     * Logs a user in
     *
     * @param user Userdata is used to hold the username and password
     * @return AuthResult with the new authToken for the user
     * @throws UnauthorizedAuthException If a username and/or password is incorrect
     */
    public AuthResult login(UserData user) throws UnauthorizedAuthException, DataAccessException {
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
