package serviceTests;

import chess.model.UserData;
import dataAccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.CreateService;
import service.RegistrationService;
import service.resultRecords.AuthResult;
import service.resultRecords.CreateResult;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UnauthorizedAuthException;
import service.serviceExceptions.UserNameInUseException;

class CreateServiceTest {
    private static CreateService createService;
    private static AuthResult authResult;

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

    @BeforeAll
    public static void init() {
        //Initialization
        ClearService clearService = new ClearService();
        clearService.delete();

        createService = new CreateService();
        RegistrationService registrationService = new RegistrationService();
        UserData testUser = new UserData("TestUsername1", "TestPassword1", "TestEmail1@email.com");
        try {
            authResult = registrationService.register(testUser);
        } catch (UserNameInUseException | MissingParameterException | DataAccessException e) {
            System.err.println("Setup failed!");
            System.err.println(e.getMessage());
        }
    }


    @Test
    void createPositive() throws UnauthorizedAuthException {
        CreateResult createResponse  = createService.create(authResult.authToken(), "TestGame1");
        Assertions.assertTrue(createResponse.gameID() > 0);
    }

    @Test
    void createNegative() throws UnauthorizedAuthException {
        Assertions.assertThrows(UnauthorizedAuthException.class, () -> createService.create("", "TestGame2"));
    }
}