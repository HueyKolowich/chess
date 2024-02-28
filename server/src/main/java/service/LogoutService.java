package service;

import dataAccess.AuthDao;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDao;
import service.serviceExceptions.UnauthorizedAuthException;

public class LogoutService {
    private final AuthDao authDao = new MemoryAuthDao();

    /**
     * Logs the user (for which the authToken belongs) out
     *
     * @param authToken Specifies the user to log out
     * @throws UnauthorizedAuthException If no active user (with an authToken) is found
     */
    public void logout(String authToken) throws UnauthorizedAuthException {
        try {
            authDao.clearAuth(authToken);
        } catch (DataAccessException dataAccessException) {
            throw new UnauthorizedAuthException("Error: unauthorized");
        }
    }
}
