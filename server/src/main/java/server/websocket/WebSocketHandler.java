package server.websocket;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseAuthDao;
import dataAccess.DatabaseGameDao;
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
    DatabaseGameDao databaseGameDao;

    {
        try {
            databaseAuthDao = new DatabaseAuthDao();
            databaseGameDao = new DatabaseGameDao();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);

        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER, JOIN_OBSERVER -> joinPlayer(new Gson().fromJson(message, UserGameCommandJoinPlayer.class), session);
        }
    }

    private void joinPlayer(UserGameCommandJoinPlayer userGameCommandJoinPlayer, Session rootSession) throws IOException {
        connectionManager.addClientSession(userGameCommandJoinPlayer.getGameID(), rootSession);
        HashSet<Session> sessions = ConnectionManager.connections.get(userGameCommandJoinPlayer.getGameID());

        for (Session clientSession : sessions) {
            try {
                if ((clientSession.equals(rootSession)) && (userGameCommandJoinPlayer.getPlayerColor() != null)) {
                    String usernameInSpot = databaseGameDao.checkPlayerSpotTaken(userGameCommandJoinPlayer.getGameID(), userGameCommandJoinPlayer.getPlayerColor().toString());

                    if ((usernameInSpot == null) || (!usernameInSpot.equals(databaseAuthDao.getUsernameByAuth(userGameCommandJoinPlayer.getAuthString())))) {
                        clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error with joining game! (spot taken)", null)));
                    } else {
                        clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, userGameCommandJoinPlayer.getGameID(), null, null)));
                    }
                } else {
                    String playerColor = userGameCommandJoinPlayer.getPlayerColor() != null ? userGameCommandJoinPlayer.getPlayerColor().toString() : "observer";
                    String message = databaseAuthDao.getUsernameByAuth(userGameCommandJoinPlayer.getAuthString()) + " has joined the game as " + playerColor;

                    if (!clientSession.equals(rootSession)) {
                        clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, message)));
                    } else if (!databaseGameDao.findGame(userGameCommandJoinPlayer.getGameID())) {
                        clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error with joining game! (no game)", null)));
                    } else {
                        clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, userGameCommandJoinPlayer.getGameID(), null, null)));
                    }
                }
            } catch (DataAccessException dataAccessException) {
                    clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error with joining game!", null)));
            }
        }
    }
}
