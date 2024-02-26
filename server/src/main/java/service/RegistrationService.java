package service;

import chess.model.UserData;
import dataAccess.*;
import service.resultRecords.AuthResult;
import service.serviceExceptions.UserNameInUseException;

public class RegistrationService {
    private final UserDao userDao = new MemoryUserDao();

    public AuthResult register(UserData user) throws UserNameInUseException {
        if (userDao.getUser(user.username()) == null) {
            userDao.createUser(user);

            String authToken = userDao.createAuth(user.username());

            return new AuthResult(user.username(), authToken);
        } else {
            throw new UserNameInUseException("Error: already taken");
        }
    }
}