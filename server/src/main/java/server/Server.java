package server;

import com.google.gson.Gson;
import service.requestRecords.*;
import service.resultRecords.*;
import service.*;
import chess.model.UserData;
import service.serviceExceptions.*;
import spark.*;

public class Server {
    private final RegistrationService registrationService = new RegistrationService();
    private final ClearService clearService = new ClearService();
    private final LoginService loginService = new LoginService();
    private final LogoutService logoutService = new LogoutService();
    private final CreateService createService = new CreateService();

    public static void main(String[] args) {
        new Server().run(8080);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", this::register);
        Spark.post("/game", this::createGame);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.delete("/db", this::delete);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request request, Response response) {
        UserData user = new Gson().fromJson(request.body(), UserData.class);

        try {
            AuthResult registerResult = registrationService.register(user);

            response.status(200);
            return new Gson().toJson(registerResult);
        } catch (MissingParameterException missingParameterException) {
            response.status(400);
            return new Gson().toJson(new ErrorResult(missingParameterException.getMessage()));
        } catch (UserNameInUseException userNameInUseException) {
            response.status(403);
            return new Gson().toJson(new ErrorResult(userNameInUseException.getMessage()));
        }
    }

    private Object createGame(Request request, Response response) {
        CreateRequest gameRequest = new Gson().fromJson(request.body(), CreateRequest.class);

        try {
            CreateResult createResult = createService.create(request.headers("authorization"), gameRequest.gameName());

            response.status(200);
            return new Gson().toJson(createResult);
        } catch (UnauthorizedAuthException unauthorizedAuthException) {
            response.status(401);
            return new Gson().toJson(new ErrorResult(unauthorizedAuthException.getMessage()));
        }
    }

    private Object login(Request request, Response response) {
        UserData user = new Gson().fromJson(request.body(), UserData.class);

        try {
            AuthResult loginResult = loginService.login(user);

            response.status(200);
            return new Gson().toJson(loginResult);
        } catch (UnauthorizedAuthException unauthorizedAuthException) {
            response.status(401);
            return new Gson().toJson(new ErrorResult(unauthorizedAuthException.getMessage()));
        }
    }

    private Object logout(Request request, Response response) {
        try {
            logoutService.logout(request.headers("authorization"));

            response.status(200);
            return "{}";
        } catch (UnauthorizedAuthException unauthorizedAuthException) {
            response.status(401);
            return new Gson().toJson(new ErrorResult(unauthorizedAuthException.getMessage()));
        }
    }

    private Object delete(Request request, Response response) {
        clearService.delete();

        response.status(200);
        return "{}";
    }
}
