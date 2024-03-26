package service;

import chess.model.UserData;
import dataAccess.*;
import service.resultRecords.AuthResult;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UserNameInUseException;

public class RegistrationService {
    private final AuthDao authDao;
    private final UserDao userDao;

    {
        try {
            authDao = new DatabaseAuthDao();
            userDao = new DatabaseUserDao();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a new user
     *
     * @param user The UserData for the user to be registered
     * @return AuthResult object of the registered user containing the new authToken and username
     * @throws UserNameInUseException If username already exits for another user
     * @throws MissingParameterException If any field of the UserData object is null
     */
    public AuthResult register(UserData user) throws UserNameInUseException, MissingParameterException, DataAccessException {
        if (user == null || user.username() == null || user.password() == null || user.email() == null) {
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