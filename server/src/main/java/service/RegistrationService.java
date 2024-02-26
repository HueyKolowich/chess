package service;

import chess.model.UserData;
import dataAccess.*;
import service.resultRecords.AuthResult;

public class RegistrationService {
    private final UserDao userDao = new MemoryUserDao();

    public AuthResult register(UserData user) {
        if (userDao.getUser(user.username()) == null) {
            //TODO Make call to createUser (for now just in memory) here

            String authToken = userDao.createAuth(user.username());

            return new AuthResult(user.username(), authToken);
        } else { return null; }
    }
}