package serviceTests;

import chess.model.UserData;
import dataAccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.CreateService;
import service.ListService;
import service.RegistrationService;
import service.resultRecords.AuthResult;
import service.resultRecords.ListResult;
import service.resultRecords.ListResultBody;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UnauthorizedAuthException;
import service.serviceExceptions.UserNameInUseException;

import java.util.HashSet;

class ListServiceTest {
    private static ListService listService;
    private static AuthResult authResult;

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

    @BeforeAll
    public static void init() {
        //Initialization
        ClearService clearService = new ClearService();
        clearService.delete();

        listService = new ListService();
        CreateService createService = new CreateService();
        RegistrationService registrationService = new RegistrationService();
        UserData testUser = new UserData("TestUsername1", "TestPassword1", "TestEmail1@email.com");
        try {
            authResult = registrationService.register(testUser);
            createService.create(authResult.authToken(), "TestGame1");
        } catch (UserNameInUseException | MissingParameterException | UnauthorizedAuthException | DataAccessException e) {
            System.err.println("Setup failed!");
            System.err.println(e.getMessage());
        }
    }

    @Test
    void listPositive() throws UnauthorizedAuthException {
        ListResult listResult = listService.list(authResult.authToken());
        HashSet<ListResultBody> expectedBody = new HashSet<>();
        expectedBody.add(new ListResultBody(listResult.games().iterator().next().gameID(), null, null, "TestGame1"));
        ListResult expectedResult = new ListResult(expectedBody);

        Assertions.assertEquals(expectedResult, listResult);
    }

    @Test
    void listNegative() {
        Assertions.assertThrows(UnauthorizedAuthException.class, () -> listService.list(""));
    }
}