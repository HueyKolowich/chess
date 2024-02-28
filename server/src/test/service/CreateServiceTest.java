package service;

import chess.model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.resultRecords.AuthResult;
import service.resultRecords.CreateResult;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UnauthorizedAuthException;
import service.serviceExceptions.UserNameInUseException;

class CreateServiceTest {
    private static CreateService createService;
    private static RegistrationService registrationService;
    private static UserData testUser;
    private static AuthResult authResult;

    @BeforeAll
    public static void init() {
        //Initialization
        createService = new CreateService();
        registrationService = new RegistrationService();
        testUser  = new UserData("TestUsername1", "TestPassword1", "TestEmail1@email.com");
        try {
            authResult = registrationService.register(testUser);
        } catch (UserNameInUseException | MissingParameterException registerException) {
            System.err.println("RegisterService failed!");
            System.err.println(registerException.getMessage());
        }
    }


    @Test
    void createPositive() throws UnauthorizedAuthException {
        /*
        Create Game

        property	        value
        Description	        Creates a new game.
        URL path	        /game
        HTTP Method	        POST
        Headers	            authorization: <authToken>
        Body	            { "gameName":"" }
        Success response	[200] { "gameID": 1234 }
        Failure response	[400] { "message": "Error: bad request" }
        Failure response	[401] { "message": "Error: unauthorized" }
        Failure response	[500] { "message": "Error: description" }
         */

        //Positive case
        CreateResult createResponse  = createService.create(authResult.authToken(), "TestGame1");
        Assertions.assertTrue(createResponse.gameID() > 0);
    }

    @Test
    void createNegative() throws UnauthorizedAuthException {
        //Negative test
        Assertions.assertThrows(UnauthorizedAuthException.class, () -> createService.create("", "TestGame2"));
    }
}