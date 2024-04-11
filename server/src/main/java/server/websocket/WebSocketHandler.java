package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.userCommands.UserGameCommandJoinPlayer;

import java.io.IOException;
import java.util.HashSet;

@WebSocket
public class WebSocketHandler {
    ConnectionManager connectionManager = new ConnectionManager();
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);

        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(message, UserGameCommandJoinPlayer.class), session);
        }
        session.getRemote().sendString("WebSocket response: " + message);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable cause) throws IOException {
        session.getRemote().sendString("Error response: " + cause.getMessage());
    }

    private void joinPlayer(UserGameCommandJoinPlayer userGameCommandJoinPlayer, Session session) throws IOException {
        connectionManager.addClientSession(userGameCommandJoinPlayer.getGameID(), session);
        HashSet<Session> sessions = ConnectionManager.connections.get(userGameCommandJoinPlayer.getGameID());

        for (Session clientSession : sessions) {
            clientSession.getRemote().sendString("This is a response to a session connected to the game");
        }
    }
}
