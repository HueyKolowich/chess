package client;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class ChessClient {
    private final String serverUrl;
    public static boolean isLoggedIn = false;
    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            if (isLoggedIn) {
                return switch (cmd) {
                    case "quit" -> "quit";
                    default -> loggedInHelp();
                };
            } else {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> loggedOutHelp();
                };
            }
        } catch (IOException ioException) {
            System.out.println("STILL NEED TO CORRECTLY HANDLE THIS ERROR"); //TODO DONT FORGET ABOUT THIS
            return null;
        }
    }

    private String register(String[] params) throws IOException {
        return connectionManager("/user", "POST", 3, params, new String[]{"username", "password", "email"});
    }

    private String login(String[] params) throws IOException {
        return connectionManager("/session", "POST", 2, params, new String[]{"username", "password"});
    }

    private String connectionManager(String urlEndpoint, String requestMethod, int numParams, String[] params, String[] paramKeys) throws IOException {
        URL url = new URL(this.serverUrl + urlEndpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);

        HashMap<String, String> body = new HashMap<>();

        connection.setDoOutput(true);
        for (int i = 0; i < numParams; i++) {
            String temp = params.length > i ? params[i] : null;
            body.put(paramKeys[i], temp);
        }

        try (OutputStream outputStream = connection.getOutputStream()) {
            String jsonBody = new Gson().toJson(body, HashMap.class);
            outputStream.write(jsonBody.getBytes());
        }

        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            if ((urlEndpoint.equals("/session") && requestMethod.equals("POST")) || urlEndpoint.equals("user")) {
                isLoggedIn = true;
            }

            try (InputStream responseBody = connection.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(responseBody);

                return new Gson().fromJson(inputStreamReader, HashMap.class).toString() + '\n';
            }
        } else {
            try (InputStream responseBody = connection.getErrorStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
                return new Gson().fromJson(inputStreamReader, HashMap.class).toString();
            }
        }
    }

    private String loggedOutHelp() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - login <USERNAME> <PASSWORD> - to play chess
                - quit - playing chess
                - help - with possible commands
                """;
    }

    private String loggedInHelp() {
        return """
                - create <NAME> - a game
                - list - games
                - join <ID> [WHITE | BLACK | <empty>] - a game
                - observe <ID> - a game
                - quit - playing chess
                - help - with possible commands
                """;
    }
}
