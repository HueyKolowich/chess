package service;

import chess.model.GameData;
import dataAccess.AuthDao;
import dataAccess.GameDao;
import dataAccess.MemoryAuthDao;
import dataAccess.MemoryGameDao;
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
    public ListResult list(String authToken) throws UnauthorizedAuthException {
        if (!authDao.verifyAuth(authToken)) {
            throw new UnauthorizedAuthException("Error: unauthorized");
        }

        return new ListResult(gameDao.listGames());
    }
}
