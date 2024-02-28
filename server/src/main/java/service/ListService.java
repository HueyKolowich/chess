package service;

import chess.model.GameData;
import dataAccess.AuthDao;
import dataAccess.GameDao;
import dataAccess.MemoryAuthDao;
import dataAccess.MemoryGameDao;
import service.resultRecords.ListResult;
import service.serviceExceptions.UnauthorizedAuthException;

import java.util.Set;

public class ListService {
    AuthDao authDao = new MemoryAuthDao();
    GameDao gameDao = new MemoryGameDao();

    public ListResult list(String authToken) throws UnauthorizedAuthException {
        if (!authDao.verifyAuth(authToken)) {
            throw new UnauthorizedAuthException("Error: unauthorized");
        }

        return new ListResult(gameDao.listGames());
    }
}
