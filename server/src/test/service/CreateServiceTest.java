package service;

import chess.model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.resultRecords.AuthResult;
import service.resultRecords.CreateResult;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UserNameInUseException;

import static org.junit.jupiter.api.Assertions.*;

class CreateServiceTest {

    @Test
    void create() {
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

        //Initialization
        CreateService createService = new CreateService();
        RegistrationService registrationService = new RegistrationService();
        UserData testUser  = new UserData("TestUsername1", "TestPassword1", "TestEmail1@email.com");

        try {
            //Initialization (continued)
            AuthResult authResult = registrationService.register(testUser);

            //Positive case
            CreateResult createResponse  = createService.create(authResult.authToken(), "TestGame1");
            Assertions.assertTrue(createResponse.gameID() > 0);
        } catch (UserNameInUseException | MissingParameterException registerException) {
            System.err.println("RegisterService failed!");
            System.err.println(registerException.getMessage());
        }
    }
}