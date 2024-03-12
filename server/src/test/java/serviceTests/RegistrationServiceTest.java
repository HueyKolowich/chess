package serviceTests;

import chess.model.UserData;
import dataAccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.RegistrationService;
import service.resultRecords.AuthResult;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UserNameInUseException;


class RegistrationServiceTest {
    private static RegistrationService registrationService;
    private static UserData testUser1;

    /*Register

        property	        value
        Description	        Register a new user.
        URL path	        /user
        HTTP Method	        POST
        Body	            { "username":"", "password":"", "email":"" }
        Success response	[200] { "username":"", "authToken":"" }
        Failure response	[400] { "message": "Error: bad request" }
        Failure response	[403] { "message": "Error: already taken" }
        Failure response	[500] { "message": "Error: description" }
    */

    @BeforeAll
    public static void init() throws DataAccessException {
        ClearService clearService = new ClearService();
        clearService.delete();

        registrationService = new RegistrationService();
        testUser1 = new UserData("TestUsername1", "TestPassword1", "TestEmail1@email.com");
    }

    @Test
    public void registerPositive() throws MissingParameterException, UserNameInUseException, DataAccessException {
        AuthResult registerResponse = registrationService.register(testUser1);
        AuthResult expectedResponse = new AuthResult("TestUsername1", "");

        Assertions.assertEquals(expectedResponse.username(), registerResponse.username());
        Assertions.assertNotNull(registerResponse.authToken());
    }

    @Test
    public void registerNegative() throws MissingParameterException, UserNameInUseException, DataAccessException {
        UserData testUser2 = new UserData("TestUsername2", "TestPassword2", "TestEmail2@email.com");
        UserData testUser3 = new UserData("TestUsername3", null, "TestEmail3@email.com");

        registrationService.register(testUser2);

        Assertions.assertThrows(UserNameInUseException.class, () -> registrationService.register(testUser2));
        Assertions.assertThrows(MissingParameterException.class, () -> registrationService.register(testUser3));
    }
}