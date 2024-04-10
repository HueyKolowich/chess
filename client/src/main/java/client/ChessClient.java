package client;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade serverFacade;

    public ChessClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
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
            if (ServerFacade.isLoggedIn) {
                out.print("[LOGGED_IN] >>> ");
            } else {
                out.print("[LOGGED_OUT] >>> ");
            }

            line = scanner.nextLine();

            result = serverFacade.eval(line);
            out.print(result);
        }
    }
}
