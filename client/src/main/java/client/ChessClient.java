package client;

import client.websocket.NotificationHandler;
import ui.ChessUI;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient implements NotificationHandler {
    private final ServerFacade serverFacade;

    public ChessClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl, this);
    }

    public void run() {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

        out.print("Welcome to 240 chess. Type help to get started.\n");

        Scanner scanner = new Scanner(System.in);
        String result = "";
        String line;

        while (!result.equals("quit")) {
            if (ServerFacade.isInGame && ServerFacade.isLoggedIn) {
                out.print("[COMMAND] >>> ");
            } else if (ServerFacade.isLoggedIn) {
                out.print("[LOGGED_IN] >>> ");
            } else {
                out.print("[LOGGED_OUT] >>> ");
            }

            line = scanner.nextLine();

            result = serverFacade.eval(line);
            out.print(result);

            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION:
                System.out.println(SET_TEXT_COLOR_RED + serverMessage.getMessage());
                break;
            case LOAD_GAME:
                ChessUI.main(null);
                serverFacade.setisInGame(true);
                break;
            case ERROR:
                System.out.println(SET_TEXT_COLOR_RED + serverMessage.getErrorMessage());
                break;
        }
    }
}
