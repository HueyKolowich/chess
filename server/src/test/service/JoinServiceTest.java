package service;

import chess.model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.resultRecords.AuthResult;
import service.resultRecords.CreateResult;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UnauthorizedAuthException;
import service.serviceExceptions.UserNameInUseException;

import static org.junit.jupiter.api.Assertions.*;

class JoinServiceTest {

    @Test
    void join() throws UnauthorizedAuthException {
        /*
        Join Game

        property	        value
        Description	        Verifies that the specified game exists, and, if a color is specified, adds the caller as the requested color to the game. If no color is specified the user is joined as an observer. This request is idempotent.
        URL path	        /game
        HTTP Method	        PUT
        Headers	            authorization: <authToken>
        Body	            { "playerColor":"WHITE/BLACK", "gameID": 1234 }
        Success response	[200]
        Failure response	[400] { "message": "Error: bad request" }
        Failure response	[401] { "message": "Error: unauthorized" }
        Failure response	[403] { "message": "Error: already taken" }
        Failure response	[500] { "message": "Error: description" }
         */

        //Initialization
        JoinService joinService = new JoinService();
        CreateService createService = new CreateService();
        RegistrationService registrationService = new RegistrationService();
        UserData testUser  = new UserData("TestUsername1", "TestPassword1", "TestEmail1@email.com");

        try {
            //Initialization (continued)
            AuthResult authResult = registrationService.register(testUser);
            CreateResult createResponse  = createService.create(authResult.authToken(), "TestGame1");

            //Positive case
            Assertions.assertDoesNotThrow(() -> joinService.join(authResult.authToken(), "White", createResponse.gameID()));

            //Negative test

        } catch (UserNameInUseException | MissingParameterException registerException) {
            System.err.println("RegisterService failed!");
            System.err.println(registerException.getMessage());
        }
    }
}