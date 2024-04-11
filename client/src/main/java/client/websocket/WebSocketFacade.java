package client.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.userCommands.UserGameCommandJoinPlayer;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws IOException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (URISyntaxException | DeploymentException ex) {
            throw new IOException(ex.getMessage());
        }

    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor playerColor) throws IOException {
        UserGameCommand userGameCommand = new UserGameCommandJoinPlayer(authToken, gameID, playerColor);
        this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
