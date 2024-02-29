package serviceTests;

import chess.model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.LoginService;
import service.LogoutService;
import service.RegistrationService;
import service.resultRecords.AuthResult;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UnauthorizedAuthException;
import service.serviceExceptions.UserNameInUseException;

class LogoutServiceTest {
    private static LogoutService logoutService;
    private static LoginService loginService;
    private static AuthResult loginResponse;
    private static UserData testUser1;

    /*
        Logout

        property	        value
        Description	        Logs out the user represented by the authToken.
        URL path	        /session
        HTTP Method	        DELETE
        Headers	            authorization: <authToken>
        Success response	[200]
        Failure response	[401] { "message": "Error: unauthorized" }
        Failure response	[500] { "message": "Error: description" }
    */

    @BeforeAll
    public static void init() {
        ClearService clearService = new ClearService();
        clearService.delete();

        logoutService = new LogoutService();
        loginService = new LoginService();
        RegistrationService registrationService = new RegistrationService();
        testUser1 = new UserData("TestUsername1", "TestPassword1", "TestEmail1@email.com");
        try {
            registrationService.register(testUser1);
            loginResponse = loginService.login(testUser1);
        } catch (UserNameInUseException | MissingParameterException | UnauthorizedAuthException e) {
            System.err.println("Setup failed!");
            System.err.println(e.getMessage());
        }
    }

    @Test
    void logoutPositive() {
        Assertions.assertDoesNotThrow(() -> logoutService.logout(loginResponse.authToken()));
    }

    @Test
    void logoutNegative() throws UnauthorizedAuthException {
        loginResponse = loginService.login(testUser1);
        logoutService.logout(loginResponse.authToken());
        Assertions.assertThrows(UnauthorizedAuthException.class, () -> logoutService.logout(loginResponse.authToken()));
    }
}