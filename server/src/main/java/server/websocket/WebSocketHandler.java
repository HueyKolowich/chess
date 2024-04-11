package server.websocket;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseAuthDao;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.userCommands.UserGameCommandJoinPlayer;

import java.io.IOException;
import java.util.HashSet;

@WebSocket
public class WebSocketHandler {
    ConnectionManager connectionManager = new ConnectionManager();
    DatabaseAuthDao databaseAuthDao;

    {
        try {
            databaseAuthDao = new DatabaseAuthDao();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);

        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(message, UserGameCommandJoinPlayer.class), session);
        }
    }

    private void joinPlayer(UserGameCommandJoinPlayer userGameCommandJoinPlayer, Session rootSession) throws IOException {
        connectionManager.addClientSession(userGameCommandJoinPlayer.getGameID(), rootSession);
        HashSet<Session> sessions = ConnectionManager.connections.get(userGameCommandJoinPlayer.getGameID());

        for (Session clientSession : sessions) {
            if (clientSession.equals(rootSession)) {
                clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, userGameCommandJoinPlayer.getGameID(), null, null)));
            } else {
                try {
                    String message = databaseAuthDao.getUsernameByAuth(userGameCommandJoinPlayer.getAuthString()) + " has joined the game!";
                    clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, -1, null, message)));
                } catch (DataAccessException dataAccessException) {
                    clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, -1, "Error with joining game!", null)));
                }
            }
        }
    }
}
