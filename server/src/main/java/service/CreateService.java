package service;

import dataAccess.AuthDao;
import dataAccess.GameDao;
import dataAccess.MemoryAuthDao;
import dataAccess.MemoryGameDao;
import service.resultRecords.CreateResult;

import java.util.Random;

public class CreateService {
    private final AuthDao authDao = new MemoryAuthDao();
    private final GameDao gameDao = new MemoryGameDao();
    public CreateResult create(String authToken, String gameName) {
//        if (!authDao.verifyAuth(authToken)) {
//            //Here throw an exception for bad authentication
//        }
        Random random = new Random();

        int gameID = gameDao.createGame(random.nextInt(9000) + 1000, gameName);

        return new CreateResult(gameID);
    }
}
