package service;

import chess.model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.resultRecords.AuthResult;
import service.resultRecords.CreateResult;
import service.serviceExceptions.AlreadyTakenException;
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
        UserData testUser1  = new UserData("TestUsername1", "TestPassword1", "TestEmail1@email.com");
        UserData testUser2  = new UserData("TestUsername2", "TestPassword2", "TestEmail2@email.com");
        UserData testUser3  = new UserData("TestUsername3", "TestPassword3", "TestEmail3@email.com");
        UserData testUser4  = new UserData("TestUsername4", "TestPassword4", "TestEmail4@email.com");

        try {
            //Initialization (continued)
            AuthResult authResult1 = registrationService.register(testUser1);
            AuthResult authResult2 = registrationService.register(testUser2);
            AuthResult authResult3 = registrationService.register(testUser3);
            AuthResult authResult4 = registrationService.register(testUser4);
            CreateResult createResponse  = createService.create(authResult1.authToken(), "TestGame1");

            //Positive case
            Assertions.assertDoesNotThrow(() -> joinService.join(authResult1.authToken(), "WHITE", createResponse.gameID()));
            Assertions.assertDoesNotThrow(() -> joinService.join(authResult2.authToken(), "BLACK", createResponse.gameID()));
            Assertions.assertDoesNotThrow(() -> joinService.join(authResult3.authToken(), null, createResponse.gameID()));

            //Negative tests
            Assertions.assertThrows(UnauthorizedAuthException.class, () -> joinService.join("", null, createResponse.gameID()));

            Assertions.assertThrows(AlreadyTakenException.class, () -> joinService.join(authResult4.authToken(), "WHITE", createResponse.gameID()));

            Assertions.assertThrows(MissingParameterException.class, () -> joinService.join(authResult4.authToken(), "WHITE", 1));
        } catch (UserNameInUseException | MissingParameterException registerException) {
            System.err.println("RegisterService failed!");
            System.err.println(registerException.getMessage());
        }
    }
}