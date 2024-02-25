package server;

import com.google.gson.Gson;
import service.RegistrationService;
import model.User;
import spark.*;

public class Server {
    private final RegistrationService registrationService = new RegistrationService();

    public static void main(String[] args) {
        new Server().run(8080);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.get("/test", this::test);
        Spark.post("/user", this::register);

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
        var user = new Gson().fromJson(request.body(), User.class);
        return new Gson().toJson(user);
    }
}
