package service;

import chess.model.GameData;
import dataAccess.*;
import service.resultRecords.ListResult;
import service.serviceExceptions.UnauthorizedAuthException;

public class ListService {
    AuthDao authDao = new MemoryAuthDao();
    GameDao gameDao = new MemoryGameDao();

    /**
     * Lists all games
     *
     * @param authToken To validate the current user can perform this request
     * @return ListResult record containing HashSet of ListResultBody records
     * @throws UnauthorizedAuthException If not authorized
     */
    public ListResult list(String authToken) throws UnauthorizedAuthException, DataAccessException {
        if (!authDao.verifyAuth(authToken)) {
            throw new UnauthorizedAuthException("Error: unauthorized");
        }

        return new ListResult(gameDao.listGames());
    }
}
