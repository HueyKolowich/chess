package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import server.websocket.*;
import service.requestRecords.*;
import service.resultRecords.*;
import service.*;
import chess.model.UserData;
import service.serviceExceptions.*;
import spark.*;

public class Server {
    private final WebSocketHandler webSocketHandler = new WebSocketHandler();
    private final RegistrationService registrationService = new RegistrationService();
    private final ClearService clearService = new ClearService();
    private final LoginService loginService = new LoginService();
    private final LogoutService logoutService = new LogoutService();
    private final CreateService createService = new CreateService();
    private final ListService listService = new ListService();
    private final JoinService joinService = new JoinService();

    public static void main(String[] args) {
        new Server().run(8080);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/connect", webSocketHandler);

        Spark.post("/user", this::register);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.get("/game", this::listGames);
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
        } catch (DataAccessException dataAccessException) {
            response.status(500);
            return new Gson().toJson(new ErrorResult(dataAccessException.getMessage()));
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
        } catch (DataAccessException dataAccessException) {
            response.status(500);
            return new Gson().toJson(new ErrorResult(dataAccessException.getMessage()));
        }
    }

    private Object joinGame(Request request, Response response) {
        JoinRequest joinRequest = new Gson().fromJson(request.body(), JoinRequest.class);

        try {
            joinService.join(request.headers("authorization"), joinRequest.playerColor(), joinRequest.gameID());

            response.status(200);
            return "{}";
        } catch (MissingParameterException missingParameterException) {
            response.status(400);
            return new Gson().toJson(new ErrorResult(missingParameterException.getMessage()));
        } catch (UnauthorizedAuthException unauthorizedAuthException) {
            response.status(401);
            return new Gson().toJson(new ErrorResult(unauthorizedAuthException.getMessage()));
        } catch (AlreadyTakenException alreadyTakenException) {
            response.status(403);
            return new Gson().toJson(new ErrorResult(alreadyTakenException.getMessage()));
        } catch (DataAccessException dataAccessException) {
            response.status(500);
            return new Gson().toJson(new ErrorResult(dataAccessException.getMessage()));
        }
    }

    private Object listGames(Request request, Response response) {
        try {
            ListResult listResult = listService.list(request.headers("authorization"));

            response.status(200);
            return new Gson().toJson(listResult);
        } catch (UnauthorizedAuthException unauthorizedAuthException) {
            response.status(401);
            return new Gson().toJson(new ErrorResult(unauthorizedAuthException.getMessage()));
        } catch (DataAccessException dataAccessException) {
            response.status(500);
            return new Gson().toJson(new ErrorResult(dataAccessException.getMessage()));
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
        } catch (DataAccessException dataAccessException) {
            response.status(500);
            return new Gson().toJson(new ErrorResult(dataAccessException.getMessage()));
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
        try {
            clearService.delete();

            response.status(200);
            return "{}";
        } catch (DataAccessException dataAccessException) {
            response.status(500);
            return new Gson().toJson(new ErrorResult(dataAccessException.getMessage()));
        }
    }
}
