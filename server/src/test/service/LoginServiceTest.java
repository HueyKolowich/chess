package service;

import chess.model.UserData;
import org.junit.jupiter.api.Assertions;
import service.resultRecords.AuthResult;
import org.junit.jupiter.api.Test;
import service.serviceExceptions.*;

class LoginServiceTest {

    @Test
    public void login() throws UserNameInUseException, MissingParameterException, UnauthorizedAuthException {
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

        //Initialization
        LoginService loginService = new LoginService();
        RegistrationService registrationService = new RegistrationService();
        UserData testUser1 = new UserData("TestUsername1", "TestPassword1", "");
        UserData badcopy1 = new UserData("TestUsername1", "Wrong", "");
        UserData badcopy2 = new UserData("Wrong", "TestPassword1", "");
        AuthResult registerResponse = registrationService.register(testUser1);

        //Positive case
        AuthResult loginResponse = loginService.login(testUser1);
        AuthResult expectedResponse = new AuthResult("TestUsername1", "");

        Assertions.assertEquals(expectedResponse.username(), loginResponse.username());
        Assertions.assertNotNull(loginResponse.authToken());
        Assertions.assertNotEquals(registerResponse.authToken(), loginResponse.authToken());

        //Negative cases
        Assertions.assertThrows(UnauthorizedAuthException.class, () -> loginService.login(badcopy1));
        Assertions.assertThrows(UnauthorizedAuthException.class, () -> loginService.login(badcopy2));
    }
}
