package client;

import java.util.Scanner;

public class ServerFacade {
    private final ChessClient client;

    public ServerFacade(String serverUrl) {
        this.client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to 240 chess. Type help to get started.");

        Scanner scanner = new Scanner(System.in);
        String result = "";
        String line;

        while (!result.equals("quit")) {
            if (ChessClient.isLoggedIn) {
                System.out.print("[LOGGED_IN] >>> ");
            } else {
                System.out.print("[LOGGED_OUT] >>> ");
            }

            line = scanner.nextLine();

            result = client.eval(line);
            System.out.print(result);
        }
    }
}
