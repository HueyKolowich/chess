package service;

import dataAccess.AuthDao;
import dataAccess.MemoryAuthDao;
import service.serviceExceptions.UnauthorizedAuthException;

public class LogoutService {
    private final AuthDao authDao = new MemoryAuthDao();

    public void logout(String authToken) {
        authDao.clearAuth(authToken);
    }
}
