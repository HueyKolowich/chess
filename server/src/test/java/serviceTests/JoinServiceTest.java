package serviceTests;

import chess.model.UserData;
import dataAccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.CreateService;
import service.JoinService;
import service.RegistrationService;
import service.resultRecords.AuthResult;
import service.resultRecords.CreateResult;
import service.serviceExceptions.AlreadyTakenException;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UnauthorizedAuthException;
import service.serviceExceptions.UserNameInUseException;

class JoinServiceTest {
    private static JoinService joinService;
    private static AuthResult authResult1;
    private static CreateResult createResult;

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

    @BeforeAll
    public static void init() {
        //Initialization
        try {
            ClearService clearService = new ClearService();
            clearService.delete();

            joinService = new JoinService();
            CreateService createService = new CreateService();
            RegistrationService registrationService = new RegistrationService();
            UserData testUser = new UserData("TestUsername1", "TestPassword1", "TestEmail1@email.com");
            authResult1 = registrationService.register(testUser);

            createResult  = createService.create(authResult1.authToken(), "TestGame1");
        } catch (UserNameInUseException | MissingParameterException | UnauthorizedAuthException | DataAccessException e) {
            System.err.println("Setup failed!");
            System.err.println(e.getMessage());
        }
    }

    @Test
    void joinPositive() {
        Assertions.assertDoesNotThrow(() -> joinService.join(authResult1.authToken(), "WHITE", createResult.gameID()));
    }

    @Test
    void joinNegative() {
        Assertions.assertDoesNotThrow(() -> joinService.join(authResult1.authToken(), "BLACK", createResult.gameID()));

        Assertions.assertThrows(UnauthorizedAuthException.class, () -> joinService.join("", null, createResult.gameID()));
        Assertions.assertThrows(AlreadyTakenException.class, () -> joinService.join(authResult1.authToken(), "BLACK", createResult.gameID()));
        Assertions.assertThrows(MissingParameterException.class, () -> joinService.join(authResult1.authToken(), "WHITE", 1));
    }
}