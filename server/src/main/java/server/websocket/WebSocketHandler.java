package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseAuthDao;
import dataAccess.DatabaseGameDao;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;
import webSocketMessages.userCommands.UserGameCommandJoinPlayer;
import webSocketMessages.userCommands.UserGameCommandMakeMove;
import webSocketMessages.userCommands.UserGameCommandResign;

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
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, UserGameCommandMakeMove.class), session);
            case RESIGN -> resign(new Gson().fromJson(message, UserGameCommandResign.class));
        }
    }

    private void joinPlayer(UserGameCommandJoinPlayer userGameCommandJoinPlayer, Session rootSession) throws IOException {
        connectionManager.addClientSession(userGameCommandJoinPlayer.getGameID(), rootSession, userGameCommandJoinPlayer.getPlayerColor());
        HashSet<SessionGrouping> sessions = ConnectionManager.connections.get(userGameCommandJoinPlayer.getGameID());

        if (checkGameStatus(rootSession, userGameCommandJoinPlayer.getGameID())) {
            for (SessionGrouping clientSessionGroup : sessions) {
                Session clientSession = clientSessionGroup.session();

                try {
                    if ((clientSession.equals(rootSession)) && (userGameCommandJoinPlayer.getPlayerColor() != null)) {
                        String usernameInSpot = databaseGameDao.checkPlayerSpotTaken(userGameCommandJoinPlayer.getGameID(), userGameCommandJoinPlayer.getPlayerColor().toString());

                        if ((usernameInSpot == null) || (!usernameInSpot.equals(databaseAuthDao.getUsernameByAuth(userGameCommandJoinPlayer.getAuthString())))) {
                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error with joining game! (spot taken)", null, null)));
                        } else {
                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, databaseGameDao.getGame(userGameCommandJoinPlayer.getGameID()), null, null, clientSessionGroup.playerColor())));
                        }
                    } else {
                        String playerColor = userGameCommandJoinPlayer.getPlayerColor() != null ? userGameCommandJoinPlayer.getPlayerColor().toString() : "observer";
                        String message = databaseAuthDao.getUsernameByAuth(userGameCommandJoinPlayer.getAuthString()) + " has joined the game as " + playerColor;

                        if (!clientSession.equals(rootSession)) {
                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, message, null)));
                        } else if (!databaseGameDao.findGame(userGameCommandJoinPlayer.getGameID())) {
                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error with joining game! (no game)", null, null)));
                        } else {
                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, databaseGameDao.getGame(userGameCommandJoinPlayer.getGameID()), null, null, clientSessionGroup.playerColor())));
                        }
                    }
                } catch (DataAccessException dataAccessException) {
                    clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error with joining game!", null, null)));
                }
            }
        }
    }

    private void makeMove(UserGameCommandMakeMove userGameCommandMakeMove, Session rootSession) throws IOException {
        HashSet<SessionGrouping> sessions = ConnectionManager.connections.get(userGameCommandMakeMove.getGameID());

        ChessGame.TeamColor rootPlayerColor;

        if (checkGameStatus(rootSession, userGameCommandMakeMove.getGameID())) {
            try {
                String rootUsername = databaseAuthDao.getUsernameByAuth(userGameCommandMakeMove.getAuthString());

                if (rootUsername.equals(databaseGameDao.checkPlayerSpotTaken(userGameCommandMakeMove.getGameID(), "WHITE"))) {
                    rootPlayerColor = ChessGame.TeamColor.WHITE;
                } else if (rootUsername.equals(databaseGameDao.checkPlayerSpotTaken(userGameCommandMakeMove.getGameID(), "BLACK"))) {
                    rootPlayerColor = ChessGame.TeamColor.BLACK;
                } else {
                    rootPlayerColor = null;
                }
            } catch (DataAccessException e) {
                rootPlayerColor = null;
            }

            try {
                ChessGame game = new Gson().fromJson(databaseGameDao.getGame(userGameCommandMakeMove.getGameID()), ChessGame.class);

                if (game.getTeamTurn().equals(rootPlayerColor)) {
                    game.makeMove(userGameCommandMakeMove.getMove());

                    databaseGameDao.updateGame(userGameCommandMakeMove.getGameID(), new Gson().toJson(game));

                    for (SessionGrouping clientSessionGroup : sessions) {
                        Session clientSession = clientSessionGroup.session();

                        clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, new Gson().toJson(game), null, null, clientSessionGroup.playerColor())));

                        if (!clientSession.equals(rootSession)) {
                            String message = databaseAuthDao.getUsernameByAuth(userGameCommandMakeMove.getAuthString()) + " moved: " + userGameCommandMakeMove.getMoveDescription();

                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, message, null)));
                        }
                    }
                } else {
                    rootSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Not your turn to make move!", null, null)));
                }
            } catch (DataAccessException dataAccessException) {
                throw new IOException(dataAccessException.getMessage());
            } catch (InvalidMoveException invalidMoveException) {
                rootSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error invalid move!", null, null)));
            }
        }
    }

    private boolean checkGameStatus(Session rootSession, int gameID) throws IOException {
        try {
            int status = databaseGameDao.checkGameStatus(gameID);
            if (status == 0 || status == -1) {
                rootSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "This game is over!", null, null)));
                return false;
            }
        } catch (DataAccessException dataAccessException) {
            return false;
        }

        return true;
    }

    private void resign(UserGameCommandResign userGameCommandResign) throws IOException {
        HashSet<SessionGrouping> sessions = ConnectionManager.connections.get(userGameCommandResign.getGameID());

        try {
            databaseGameDao.updateGameStatus(userGameCommandResign.getGameID());

            String message = databaseAuthDao.getUsernameByAuth(userGameCommandResign.getAuthString()) + " has resigned from the game.";
            for (SessionGrouping sessionGroup : sessions) {
                sessionGroup.session().getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, message, null)));
            }
        } catch (DataAccessException dataAccessException) {
            throw new IOException(dataAccessException.getMessage());
        }
    }
}
