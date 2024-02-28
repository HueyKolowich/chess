package service;

import chess.model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.resultRecords.AuthResult;
import service.resultRecords.CreateResult;
import service.resultRecords.ListResult;
import service.resultRecords.ListResultBody;
import service.serviceExceptions.AlreadyTakenException;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UnauthorizedAuthException;
import service.serviceExceptions.UserNameInUseException;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ListServiceTest {

    @Test
    void list() throws UnauthorizedAuthException {
        /*
        List Games

        Note that whiteUsername and blackUsername may be null.

        property	        value
        Description	        Gives a list of all games.
        URL path	        /game
        HTTP Method	        GET
        Headers	            authorization: <authToken>
        Success response	[200] { "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}
        Failure response	[401] { "message": "Error: unauthorized" }
        Failure response	[500] { "message": "Error: description" }
         */

        //Initialization
        ListService listService = new ListService();
        JoinService joinService = new JoinService();
        CreateService createService = new CreateService();
        RegistrationService registrationService = new RegistrationService();
        UserData testUser1  = new UserData("TestUsername1", "TestPassword1", "TestEmail1@email.com");

        try {
            //Initialization (continued)
            AuthResult authResult1 = registrationService.register(testUser1);
            createService.create(authResult1.authToken(), "TestGame1");

            //Positive case
            ListResult listResult = listService.list(authResult1.authToken());
            HashSet<ListResultBody> expectedBody = new HashSet<>();
            expectedBody.add(new ListResultBody(listResult.games().iterator().next().gameID(), null, null, "TestGame1"));
            ListResult expectedResult = new ListResult(expectedBody);

            Assertions.assertEquals(expectedResult, listResult);

            //Negative tests
            Assertions.assertThrows(UnauthorizedAuthException.class, () -> listService.list(""));
        } catch (UserNameInUseException | MissingParameterException registerException) {
            System.err.println("RegisterService failed!");
            System.err.println(registerException.getMessage());
        }
    }
}