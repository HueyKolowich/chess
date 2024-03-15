package service;

import dataAccess.*;
import service.resultRecords.CreateResult;
import service.serviceExceptions.UnauthorizedAuthException;

import java.util.Random;

public class CreateService {
    private final AuthDao authDao;
    private final GameDao gameDao;

    {
        try {
            authDao = new DatabaseAuthDao();
            gameDao = new DatabaseGameDao();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new game
     *
     * @param authToken authToken to validate the request
     * @param gameName Name for the game to be created
     * @return CreateResult record containing the new gameID
     * @throws UnauthorizedAuthException If authToken is not valid
     */
    public CreateResult create(String authToken, String gameName) throws UnauthorizedAuthException, DataAccessException {
        if (!authDao.verifyAuth(authToken)) {
            throw new UnauthorizedAuthException("Error: unauthorized");
        }
        Random random = new Random();

        int gameID = gameDao.createGame(random.nextInt(9000) + 1000, gameName);

        return new CreateResult(gameID);
    }
}
