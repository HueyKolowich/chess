package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.*;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import ui.ChessUI;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ServerFacade {
    private WebSocketFacade webSocketFacade;
    private final NotificationHandler notificationHandler;
    private final String serverUrl;
    public static boolean isLoggedIn = false;
    public static boolean isInGame = false;
    private int currentGameID = -1;
    private String sessionAuthToken = null;
    private HashMap result;
    private final HashMap<Integer, Integer> clientGameNumberingSeries = new HashMap<>();
    private int currentPositionInGameNumberingSeries = 1;

    public ServerFacade(String serverUrl, NotificationHandler notificationHandler) {
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;

        populateNumberingSeries();
    }

    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            if (isLoggedIn && isInGame) {
                return switch (cmd) {
                    case "leave" -> leave();
                    case "move" -> move(params);
                    default -> inGameHelp();
                };
            } else if (isLoggedIn) {
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
            System.out.println("There have been issues with communicating with the server! Please try again.\n");
            System.out.println(ioException.getMessage());
            return "";
        }
    }

    private void populateNumberingSeries() {
        clientGameNumberingSeries.clear();
        currentPositionInGameNumberingSeries = 1;

        try {
            result = connectionManager("/game", "GET", 0, null, null, "0192837465");

            ArrayList games = (ArrayList) result.get("games");
            if (games != null) {
                for (Object game : games) {
                    LinkedTreeMap gameLTM = (LinkedTreeMap) game;

                    Double tempGameIDObject = (Double) gameLTM.get("gameID");

                    clientGameNumberingSeries.put(currentPositionInGameNumberingSeries, tempGameIDObject.intValue());
                    currentPositionInGameNumberingSeries++;
                }
            }
        } catch (IOException ioException) {
            System.out.println("Could not assign numbering series to stored games!");
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
        } else {
            Double tempGameIDObject = (Double) result.get("gameID");

            clientGameNumberingSeries.put(currentPositionInGameNumberingSeries, tempGameIDObject.intValue());
            currentPositionInGameNumberingSeries++;

            return "Created game number: " + (currentPositionInGameNumberingSeries - 1) + '\n';
        }
    }

    private String list() throws IOException {
        populateNumberingSeries();

        result = connectionManager("/game", "GET", 0, null, null, this.sessionAuthToken);

        if (result.containsKey("message")) {
            return (String) result.get("message") + '\n';
        } else {
            StringBuilder resultString = new StringBuilder();
            ArrayList<LinkedTreeMap> games = (ArrayList) result.get("games");

            for (Integer position : clientGameNumberingSeries.keySet()) {
                for (LinkedTreeMap game : games) {
                    if (game.containsValue(clientGameNumberingSeries.get(position).doubleValue())) {
                        resultString.append(position);
                        resultString.append(" : ");

                        resultString.append(game);

                        resultString.append('\n');
                    }
                }
            }

            return resultString.toString();
        }
    }

    private String join(String[] params) throws IOException {
        if (clientGameNumberingSeries.get(Integer.parseInt(params[0])) != null) {
            params[0] = String.valueOf(clientGameNumberingSeries.get(Integer.parseInt(params[0])));
        }

        result = connectionManager("/game", "PUT", 2, params, new String[]{"gameID", "playerColor"}, this.sessionAuthToken);

        if (result.containsKey("message")) {
            return (String) result.get("message") + '\n';
        } else {
            currentGameID = Integer.parseInt(params[0]);

            webSocketFacade = new WebSocketFacade(serverUrl, notificationHandler);
            if (params.length > 1) {
                webSocketFacade.joinPlayer(this.sessionAuthToken, currentGameID, ChessGame.TeamColor.valueOf(params[1].toUpperCase()));
            } else {
                webSocketFacade.joinPlayer(this.sessionAuthToken, currentGameID, null);
            }

            return "";
        }
    }

    private String observe(String[] params) throws IOException {
        if (clientGameNumberingSeries.get(Integer.parseInt(params[0])) != null) {
            params[0] = String.valueOf(clientGameNumberingSeries.get(Integer.parseInt(params[0])));
        }

        result = connectionManager("/game", "PUT", 1, params, new String[]{"gameID"}, this.sessionAuthToken);

        if (result.containsKey("message")) {
            return (String) result.get("message") + '\n';
        } else {
            currentGameID = Integer.parseInt(params[0]);

            webSocketFacade = new WebSocketFacade(serverUrl, notificationHandler);
            webSocketFacade.joinPlayer(this.sessionAuthToken, currentGameID, null);

            return "";
        }
    }

    private String leave() {
        //TODO Needs to make a call to the ws/connection manager to remove its session from the set
        //TODO Will also probably need to update currentGameID to -1;
        setisInGame(false);

        return "";
    }

    private String move(String[] params) throws IOException { //TODO Need to add functionality for promoting
        if (currentGameID == -1) {
            return "The game was not correctly joined... please try again\n";
        }

        if (params.length != 2) {
            return "Incorrect amount of move arguments... please see the help command\n";
        }

        if ((new String(validMoveChars).indexOf(params[0].charAt(0)) == -1) || (new String(validMoveChars).indexOf(params[1].charAt(0)) == -1)) {
            return "Incorrect formatting of moves... please try again\n";
        }

        if ((new String(validMoveInts).indexOf(params[0].charAt(1)) == -1) || (new String(validMoveInts).indexOf(params[1].charAt(1)) == -1)) {
            return "Incorrect formatting of moves... please try again\n";
        }

        ChessPosition startPosition = new ChessPosition(Integer.parseInt(String.valueOf(params[0].charAt(1))), chessCharToInt.get(params[0].charAt(0)));
        ChessPosition endPosition = new ChessPosition(Integer.parseInt(String.valueOf(params[1].charAt(1))), chessCharToInt.get(params[1].charAt(0)));
        webSocketFacade.makeMove(this.sessionAuthToken, currentGameID, new ChessMove(startPosition, endPosition, null));

        return "";
    }

    private HashMap connectionManager(String urlEndpoint, String requestMethod,
                                     int numBodyParams, String[] bodyParams, String[] bodyParamKeys, String headerParam) throws IOException {
        URL url = new URL(this.serverUrl + urlEndpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);

        if (headerParam != null) {
            connection.setDoOutput(true);

            connection.setRequestProperty("authorization", headerParam);
        }

        if (numBodyParams > 0) {
            HashMap<String, String> body = new HashMap<>();

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
        HashMap responseMap;

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

    private String inGameHelp() {
        return """
                - redraw - the chessboard
                - leave - the current game
                - move <INITIAL POSITION> <END POSITION> - a chess piece
                - resign - from the current game
                - highlight <CHESS PIECE POSITION> - legal moves
                - help - with possible commands
                """;
    }

    private final char[] validMoveChars = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    private final char[] validMoveInts = new char[] {'1', '2', '3', '4', '5', '6', '7', '8'};

    private final HashMap<Character, Integer> chessCharToInt = new HashMap<Character, Integer>() {{
        put('a', 1);
        put('b', 2);
        put('c', 3);
        put('d', 4);
        put('e', 5);
        put('f', 6);
        put('g', 7);
        put('h', 8);
    }};

    public void setIsLoggedIn(boolean isLoggedIn) {
        ServerFacade.isLoggedIn = isLoggedIn;
    }

    public void setisInGame(boolean isInGame) {
        ServerFacade.isInGame = isInGame;
    }

    public int getCurrentPositionInGameNumberingSeries() {
        return currentPositionInGameNumberingSeries;
    }
}
