package service;

import dataAccess.*;
import service.serviceExceptions.AlreadyTakenException;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UnauthorizedAuthException;

public class JoinService {
    AuthDao authDao;
    GameDao gameDao;

    {
        try {
            ;
            authDao = new DatabaseAuthDao();
            gameDao = new DatabaseGameDao();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies that the specified game exists, and, if a color is specified,
     * adds the caller as the requested color to the game.
     * If no color is specified the user is joined as an observer. This request is idempotent.
     *
     * @param authToken to validate request
     * @param playerColor desired join color
     * @param gameID of the game to join
     * @throws UnauthorizedAuthException if authToken not found
     * @throws AlreadyTakenException if playerColor for specified color is already taken
     * @throws MissingParameterException if no game is found for the gameID
     */
    public void join(String authToken, String playerColor, int gameID) throws UnauthorizedAuthException, AlreadyTakenException, MissingParameterException, DataAccessException {
        if (!authDao.verifyAuth(authToken)) {
            throw new UnauthorizedAuthException("Error: unauthorized");
        }

        if (!gameDao.findGame(gameID)) {
            throw new MissingParameterException("Error: bad request");
        }

        try {
            if (playerColor != null) {
                if (playerColor.equals("WHITE") || playerColor.equals("BLACK")) {
                    gameDao.addPlayer(playerColor, authDao.getUsernameByAuth(authToken), gameID);
                } else {
                    throw new MissingParameterException("Error: bad request");
                }
            }
        } catch (DataAccessException dataAccessException) {
            if (dataAccessException.getMessage().contains("playerColor")) {
                throw new AlreadyTakenException("Error: already taken");
            }
        }
    }
}
