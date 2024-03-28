package client;

import com.google.gson.Gson;
import ui.ChessUI;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class ChessClient {
    public static boolean isLoggedIn = false;
    private final String serverUrl;
    private String sessionAuthToken = null;
    private HashMap result;

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
                    case "logout" -> logout();
                    case "create" -> create(params);
                    case "list" -> list();
                    case "join" -> join(params);
                    case "observe" -> observe(params);
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
        result = connectionManager("/user", "POST", 3, params, new String[]{"username", "password", "email"}, null);

        if (result.containsKey("message")) {
            return (String) result.get("message") + '\n';
        } else { return ""; }
    }

    private String login(String[] params) throws IOException {
        result = connectionManager("/session", "POST", 2, params, new String[]{"username", "password"}, null);

        if (result.containsKey("message")) {
            return (String) result.get("message") + '\n';
        } else { return ""; }
    }

    private String logout() throws IOException {
        result = connectionManager("/session", "DELETE", 0, null, null, this.sessionAuthToken);

        if (result.containsKey("message")) {
            return (String) result.get("message") + '\n';
        } else { return ""; }
    }

    private String create(String[] params) throws IOException {
        result = connectionManager("/game", "POST", 1, params, new String[]{"gameName"}, this.sessionAuthToken);

        if (result.containsKey("message")) {
            return (String) result.get("message") + '\n';
        } else { return ""; }
    }

    private String list() throws IOException {
        result = connectionManager("/game", "GET", 0, null, null, this.sessionAuthToken);

        if (result.containsKey("message")) {
            return (String) result.get("message") + '\n';
        } else { return ""; }
    }

    private String join(String[] params) throws IOException {
        result = connectionManager("/game", "PUT", 2, params, new String[]{"gameID", "playerColor"}, this.sessionAuthToken);

        if (result.containsKey("message")) {
            return (String) result.get("message") + '\n';
        } else {
            ChessUI.main(null);
            return "";
        }
    }

    private String observe(String[] params) throws IOException {
        result = connectionManager("/game", "PUT", 1, params, new String[]{"gameID"}, this.sessionAuthToken);

        if (result.containsKey("message")) {
            return (String) result.get("message") + '\n';
        } else {
            ChessUI.main(null);
            return "";
        }
    }

    private HashMap connectionManager(String urlEndpoint, String requestMethod,
                                     int numBodyParams, String[] bodyParams, String[] bodyParamKeys, String headerParam) throws IOException {
        URL url = new URL(this.serverUrl + urlEndpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);

        if (headerParam != null) { //TODO can only handle authentication header... not sure if I will end up needing more
            connection.setDoOutput(true);

            connection.setRequestProperty("authorization", headerParam);
        }

        if (numBodyParams > 0) {
            HashMap<String, String> body = new HashMap<>(); //TODO Could be an issue if not String type body fields

            connection.setDoOutput(true);
            for (int i = 0; i < numBodyParams; i++) {
                String temp = bodyParams.length > i ? bodyParams[i] : null;
                body.put(bodyParamKeys[i], temp);
            }

            try (OutputStream outputStream = connection.getOutputStream()) {
                String jsonBody = new Gson().toJson(body, HashMap.class);
                outputStream.write(jsonBody.getBytes());
            }
        }

        connection.connect();

        InputStreamReader inputStreamReader;
        HashMap responseMap; //TODO this could be a problem in the future if response contains an int... gameID? POSSIBLY FIXED

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            if ((urlEndpoint.equals("/session") && requestMethod.equals("POST")) || urlEndpoint.equals("/user")) {
                isLoggedIn = true;
            } else if (urlEndpoint.equals("/session") && requestMethod.equals("DELETE")) {
                isLoggedIn = false;
            }

            try (InputStream responseBody = connection.getInputStream()) {
                inputStreamReader = new InputStreamReader(responseBody);
                responseMap = new Gson().fromJson(inputStreamReader, HashMap.class);

                if (responseMap.containsKey("authToken")) {
                    this.sessionAuthToken = (String) responseMap.get("authToken");
                }

                return responseMap;
            }
        } else {
            try (InputStream responseBody = connection.getErrorStream()) {
                inputStreamReader = new InputStreamReader(responseBody);
                return new Gson().fromJson(inputStreamReader, HashMap.class);
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
                - logout - when you are done
                - quit - playing chess
                - help - with possible commands
                """;
    }
}
