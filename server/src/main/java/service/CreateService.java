package service;

import dataAccess.AuthDao;
import dataAccess.GameDao;
import dataAccess.MemoryAuthDao;
import dataAccess.MemoryGameDao;
import service.resultRecords.CreateResult;
import service.serviceExceptions.UnauthorizedAuthException;

import java.util.Random;

public class CreateService {
    private final AuthDao authDao = new MemoryAuthDao();
    private final GameDao gameDao = new MemoryGameDao();

    /**
     * Creates a new game
     *
     * @param authToken authToken to validate the request
     * @param gameName Name for the game to be created
     * @return CreateResult record containing the new gameID
     * @throws UnauthorizedAuthException If authToken is not valid
     */
    public CreateResult create(String authToken, String gameName) throws UnauthorizedAuthException {
        if (!authDao.verifyAuth(authToken)) {
            throw new UnauthorizedAuthException("Error: unauthorized");
        }
        Random random = new Random();

        int gameID = gameDao.createGame(random.nextInt(9000) + 1000, gameName);

        return new CreateResult(gameID);
    }
}
