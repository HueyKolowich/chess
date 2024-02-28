package service;

import chess.model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.resultRecords.AuthResult;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UnauthorizedAuthException;
import service.serviceExceptions.UserNameInUseException;

import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTest {

    @Test
    void logout() throws UnauthorizedAuthException {
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

        //Initialization
        LogoutService logoutService = new LogoutService();
        LoginService loginService = new LoginService();
        RegistrationService registrationService = new RegistrationService();
        UserData testUser = new UserData("TestUsername1", "TestPassword1", "");

        try {
            //Initialization (continued)
            registrationService.register(testUser);
            AuthResult loginResponse = loginService.login(testUser);

            //Positive case
            Assertions.assertDoesNotThrow(() -> logoutService.logout(loginResponse.authToken()));

            //Negative case
            Assertions.assertThrows(UnauthorizedAuthException.class, () -> logoutService.logout(loginResponse.authToken()));
        } catch (UserNameInUseException | MissingParameterException registerException) {
            System.err.println("RegisterService failed!");
            System.err.println(registerException.getMessage());
        }
    }
}