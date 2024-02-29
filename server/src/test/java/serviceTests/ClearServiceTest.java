package serviceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.ClearService;

class ClearServiceTest {
    /*
    Clear application

    property	        value
    Description	        Clears the database. Removes all users, games, and authTokens.
    URL path	        /db
    HTTP Method	        DELETE
    Success response	[200]
    Failure response	[500] { "message": "Error: description" }
     */

    @Test
    void delete() {
        ClearService clearService = new ClearService();

        Assertions.assertDoesNotThrow(clearService::delete);
    }
}