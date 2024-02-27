package service;

import chess.model.UserData;
import dataAccess.*;
import service.resultRecords.AuthResult;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UserNameInUseException;

public class RegistrationService {
    private final UserDao userDao = new MemoryUserDao();
    private final AuthDao authDao = new MemoryAuthDao();

    public AuthResult register(UserData user) throws UserNameInUseException, MissingParameterException {
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new MissingParameterException("Error: bad request");
        }

        if (userDao.getUser(user.username()) == null) {
            userDao.createUser(user);

            String authToken = authDao.createAuth(user.username());

            return new AuthResult(user.username(), authToken);
        } else {
            throw new UserNameInUseException("Error: already taken");
        }
    }
}