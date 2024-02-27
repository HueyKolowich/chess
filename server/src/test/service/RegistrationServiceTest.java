package service;

import chess.model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.resultRecords.AuthResult;
import service.serviceExceptions.MissingParameterException;
import service.serviceExceptions.UserNameInUseException;
import spark.Response;

import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    @Test
    public void register() throws UserNameInUseException, MissingParameterException {
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

        //Initialization
        RegistrationService registrationService = new RegistrationService();
        UserData testUser1 = new UserData("TestUsername1", "TestPassword1", "TestEmail1@email.com");
        UserData testUser1copy = new UserData("TestUsername1", "TestPassword1.1", "TestEmail1.1@email.com");
        UserData testUser2 = new UserData("TestUsername2", null, "TestEmail2@email.com");

        //Positive case
        AuthResult registerResponse = registrationService.register(testUser1);
        AuthResult expectedResponse = new AuthResult("TestUsername1", "");

        Assertions.assertEquals(expectedResponse.username(), registerResponse.username());
        Assertions.assertNotNull(registerResponse.authToken());

        //Negative cases
        Assertions.assertThrows(UserNameInUseException.class, () -> registrationService.register(testUser1copy));
        Assertions.assertThrows(MissingParameterException.class, () -> registrationService.register(testUser2));
    }
}