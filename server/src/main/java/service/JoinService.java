package service;

import dataAccess.*;
import service.serviceExceptions.AlreadyTakenException;
import service.serviceExceptions.UnauthorizedAuthException;

public class JoinService {
    AuthDao authDao = new MemoryAuthDao();
    GameDao gameDao = new MemoryGameDao();
    public void join(String authToken, String playerColor, int gameID) throws UnauthorizedAuthException, AlreadyTakenException {
        if (!authDao.verifyAuth(authToken)) {
            throw new UnauthorizedAuthException("Error: unauthorized");
        }

//        if (!gameDao.findGame(gameID)) {
//            throws new something
//        }

        try {
            if (playerColor == null) {
                //Spectator
            } else if (playerColor.equals("WHITE") || playerColor.equals("BLACK")) {
                gameDao.addPlayer(playerColor, authDao.getUsernameByAuth(authToken), gameID);
            } else {
                //TODO Throw something here
            }
        } catch (DataAccessException dataAccessException) {
            //TODO One option here might be that no username was found... throw 500??
            if (dataAccessException.getMessage().contains("playerColor")) {
                throw new AlreadyTakenException("Error: already taken");
            }
        }
    }
}
