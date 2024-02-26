package server;

import com.google.gson.Gson;
import service.resultRecords.*;
import service.*;
import chess.model.UserData;
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

        AuthResult registerResult = registrationService.register(user);

        return new Gson().toJson(registerResult);
    }

    private Object delete(Request request, Response response) {
        ClearResult clearResult = clearService.delete();

        return new Gson().toJson(clearResult);
    }
}
