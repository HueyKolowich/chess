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
import java.util.Map;

public class ChessClient {
    private final String serverUrl;
    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            return switch (cmd) {
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (IOException ioException) {
            System.out.println("STILL NEED TO CORRECTLY HANDLE THIS ERROR");
            return null;
        }
    }

    private String register(String[] params) throws IOException {
        URL url = new URL(this.serverUrl + "/user");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        connection.setDoOutput(true);
        String username = params.length > 0 ? params[0] : null;
        String password = params.length > 1 ? params[1]: null;
        String email = params.length > 2 ? params[2]: null;

        HashMap<String, String> body = new HashMap<String, String>(){
            {
                put("username", username);
                put("password", password);
                put("email", email);
            }
        };
        try (OutputStream outputStream = connection.getOutputStream()) {
            String jsonBody = new Gson().toJson(body, HashMap.class);
            outputStream.write(jsonBody.getBytes());
        }

        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream responseBody = connection.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
                System.out.println(new Gson().fromJson(inputStreamReader, HashMap.class));
            }
        } else {
            try (InputStream responseBody = connection.getErrorStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
                System.out.println(new Gson().fromJson(inputStreamReader, HashMap.class));
            }
        }

        return "";
    }

    private String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - login <USERNAME> <PASSWORD> - to play chess
                - quit - playing chess
                - help - with possible commands
                """;
    }
}
