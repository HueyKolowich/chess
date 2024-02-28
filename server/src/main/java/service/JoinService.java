package service;

import dataAccess.*;
import service.serviceExceptions.UnauthorizedAuthException;

public class JoinService {
    AuthDao authDao = new MemoryAuthDao();
    GameDao gameDao = new MemoryGameDao();
    public void join(String authToken, String playerColor, int gameID) {
//        if (!authDao.verifyAuth(authToken)) {
//            throws new UnauthorizedAuthException("Error: unauthorized");
//        }

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
            int x = 0; //TODO Throw something here
        }
    }
}
