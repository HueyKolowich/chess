package server;

import com.google.gson.Gson;
import service.resultRecords.*;
import service.*;
import chess.model.UserData;
import service.serviceExceptions.UserNameInUseException;
import spark.*;

public class Server {
    private final RegistrationService registrationService = new RegistrationService();
    private final ClearService clearService = new ClearService();

    public static void main(String[] args) {
        new Server().run(8080);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.get("/test", this::test);
        Spark.post("/user", this::register);
        Spark.delete("/db", this::delete);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object test(Request request, Response response) {
        return "This is a test";
    }

    private Object register(Request request, Response response) {
        UserData user = new Gson().fromJson(request.body(), UserData.class);

        try {
            AuthResult registerResult = registrationService.register(user);

            response.status(200);
            return new Gson().toJson(registerResult);
        } catch (UserNameInUseException userNameInUseException) {
            response.status(403);
            return new Gson().toJson(new ErrorResult(userNameInUseException.getMessage()));
        } //TODO I probably need to have a final catch for everything else here to assign the 500 status


    }

    private Object delete(Request request, Response response) {
        ClearResult clearResult = clearService.delete();

        return new Gson().toJson(clearResult);
    }
}
