package serviceTests;

import chess.model.UserData;
import dataAccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.LoginService;
import service.RegistrationService;
import service.resultRecords.AuthResult;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UnauthorizedAuthException;
import service.serviceExceptions.UserNameInUseException;

class LoginServiceTest {
    private static LoginService loginService;
    private static AuthResult registerResult;
    private static UserData testUser1;

    /*
            Login

            property	        value
            Description	        Logs in an existing user (returns a new authToken).
            URL path	        /session
            HTTP Method	        POST
            Body	            { "username":"", "password":"" }
            Success response	[200] { "username":"", "authToken":"" }
            Failure response	[401] { "message": "Error: unauthorized" }
            Failure response	[500] { "message": "Error: description" }
    */

    @BeforeAll
    public static void init() {
        try {
            ClearService clearService = new ClearService();
            clearService.delete();

            loginService = new LoginService();
            RegistrationService registrationService = new RegistrationService();
            testUser1 = new UserData("TestUsername1", "TestPassword1", "TestEmail1@email.com");
            registerResult = registrationService.register(testUser1);
        } catch (UserNameInUseException | MissingParameterException | DataAccessException e) {
            System.err.println("Setup failed!");
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void loginPositive() throws UnauthorizedAuthException, DataAccessException {
        AuthResult loginResponse = loginService.login(testUser1);
        AuthResult expectedResponse = new AuthResult("TestUsername1", "");

        Assertions.assertEquals(expectedResponse.username(), loginResponse.username());
        Assertions.assertNotNull(loginResponse.authToken());
        Assertions.assertNotEquals(registerResult.authToken(), loginResponse.authToken());
    }

    @Test
    public void loginNegative() {
        UserData badCopy1 = new UserData("TestUsername1", "Wrong", "");
        UserData badCopy2 = new UserData("Wrong", "TestPassword1", "");

        Assertions.assertThrows(UnauthorizedAuthException.class, () -> loginService.login(badCopy1));
        Assertions.assertThrows(UnauthorizedAuthException.class, () -> loginService.login(badCopy2));
    }
}
