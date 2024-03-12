package service;

import chess.model.UserData;
import dataAccess.*;
import service.resultRecords.AuthResult;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UserNameInUseException;

public class RegistrationService {
    private final UserDao userDao = new MemoryUserDao();
    private final AuthDao authDao = new MemoryAuthDao();

    private final UserDao tempDao;

    {
        try {
            tempDao = new DatabaseUserDao();
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
    public AuthResult register(UserData user) throws UserNameInUseException, MissingParameterException {
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new MissingParameterException("Error: bad request");
        }

        if (userDao.getUser(user.username()) == null) {
            try {
                userDao.createUser(user);
                tempDao.createUser(user);
            } catch (DataAccessException dataAccessException) {
                System.out.println(dataAccessException.getMessage()); //TODO NEEDS TO BE FIXED!!!!
            }

            String authToken = authDao.createAuth(user.username());

            return new AuthResult(user.username(), authToken);
        } else {
            throw new UserNameInUseException("Error: already taken");
        }
    }
}