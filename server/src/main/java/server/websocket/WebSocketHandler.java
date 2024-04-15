package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseAuthDao;
import dataAccess.DatabaseGameDao;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Collection;
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
            case RESIGN -> resign(new Gson().fromJson(message, UserGameCommandResign.class), session);
            case LEAVE -> leave(new Gson().fromJson(message, UserGameCommandLeave.class), session);
            case REDRAW -> redraw(new Gson().fromJson(message, UserGameCommandRedraw.class), session);
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
                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error with joining game! (spot taken)", null, null, null)));
                        } else {
                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, databaseGameDao.getGame(userGameCommandJoinPlayer.getGameID()), null, null, clientSessionGroup.playerColor(), null)));
                        }
                    } else {
                        String playerColor = userGameCommandJoinPlayer.getPlayerColor() != null ? userGameCommandJoinPlayer.getPlayerColor().toString() : "observer";
                        String message = databaseAuthDao.getUsernameByAuth(userGameCommandJoinPlayer.getAuthString()) + " has joined the game as " + playerColor;

                        if (!clientSession.equals(rootSession)) {
                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, message, null, null)));
                        } else if (!databaseGameDao.findGame(userGameCommandJoinPlayer.getGameID())) {
                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error with joining game! (no game)", null, null, null)));
                        } else {
                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, databaseGameDao.getGame(userGameCommandJoinPlayer.getGameID()), null, null, clientSessionGroup.playerColor(), null)));
                        }
                    }
                } catch (DataAccessException dataAccessException) {
                    clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error with joining game!", null, null, null)));
                }
            }
        }
    }

    private void makeMove(UserGameCommandMakeMove userGameCommandMakeMove, Session rootSession) throws IOException {
        HashSet<SessionGrouping> sessions = ConnectionManager.connections.get(userGameCommandMakeMove.getGameID());

        if (checkGameStatus(rootSession, userGameCommandMakeMove.getGameID())) {
            ChessGame.TeamColor rootPlayerColor = getTeamColor(userGameCommandMakeMove.getAuthString(), userGameCommandMakeMove.getGameID());

            try {
                ChessGame game = new Gson().fromJson(databaseGameDao.getGame(userGameCommandMakeMove.getGameID()), ChessGame.class);

                if (game.getTeamTurn().equals(rootPlayerColor)) {
                    game.makeMove(userGameCommandMakeMove.getMove());

                    databaseGameDao.updateGame(userGameCommandMakeMove.getGameID(), new Gson().toJson(game));

                    for (SessionGrouping clientSessionGroup : sessions) {
                        Session clientSession = clientSessionGroup.session();

                        clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, new Gson().toJson(game), null, null, clientSessionGroup.playerColor(), null)));

                        if (game.isInCheck(ChessGame.TeamColor.BLACK) || game.isInCheck(ChessGame.TeamColor.WHITE)) {
                            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                                databaseGameDao.updateGameStatus(userGameCommandMakeMove.getGameID());
                                clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, "White is in checkmate!", null, null)));
                            } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                                databaseGameDao.updateGameStatus(userGameCommandMakeMove.getGameID());
                                clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, "Black is in checkmate!", null, null)));
                            } else {
                                clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, "Check!", null, null)));
                            }
                        }

                        if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)) {
                            databaseGameDao.updateGameStatus(userGameCommandMakeMove.getGameID());
                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, "Game is in stalemate!", null, null)));
                        }

                        if (!clientSession.equals(rootSession)) {
                            String message = databaseAuthDao.getUsernameByAuth(userGameCommandMakeMove.getAuthString()) + " moved: " + userGameCommandMakeMove.getMoveDescription();

                            clientSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, message, null, null)));
                        }
                    }
                } else {
                    rootSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Not your turn to make move!", null, null, null)));
                }
            } catch (DataAccessException dataAccessException) {
                throw new IOException(dataAccessException.getMessage());
            } catch (InvalidMoveException invalidMoveException) {
                rootSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error invalid move!", null, null, null)));
            }
        }
    }

    private void resign(UserGameCommandResign userGameCommandResign, Session rootSession) throws IOException {
        HashSet<SessionGrouping> sessions = ConnectionManager.connections.get(userGameCommandResign.getGameID());

        try {
            ChessGame.TeamColor rootPlayerColor = getTeamColor(userGameCommandResign.getAuthString(), userGameCommandResign.getGameID());

            if (databaseGameDao.checkGameStatus(userGameCommandResign.getGameID()) == 0) {
                rootSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "The other player has already resigned from the game!", null, null, null)));
            } else if (rootPlayerColor != null) {
                databaseGameDao.updateGameStatus(userGameCommandResign.getGameID());

                String message = databaseAuthDao.getUsernameByAuth(userGameCommandResign.getAuthString()) + " has resigned from the game.";
                for (SessionGrouping sessionGroup : sessions) {
                    sessionGroup.session().getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, message, null, null)));
                }
            } else {
                rootSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "An observer cannot resign from the game!", null, null, null)));
            }
        } catch (DataAccessException dataAccessException) {
            throw new IOException(dataAccessException.getMessage());
        }
    }

    private void leave(UserGameCommandLeave userGameCommandLeave, Session rootSession) throws IOException {
        HashSet<SessionGrouping> sessions = ConnectionManager.connections.get(userGameCommandLeave.getGameID());
        SessionGrouping sessionGroupToBeRemoved = null;

        for (SessionGrouping sessionGroup : sessions) {
            if (sessionGroup.session().equals(rootSession)) {
                sessionGroupToBeRemoved = sessionGroup;
                
                rootSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, "You have left the game.", null, null)));
            } else {
                try {
                    String message = databaseAuthDao.getUsernameByAuth(userGameCommandLeave.getAuthString()) + " has left the game.";
                    sessionGroup.session().getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, null, message, null, null)));
                } catch (DataAccessException dataAccessException) {
                    throw new IOException(dataAccessException.getMessage());
                }
            }
        }

        sessions.remove(sessionGroupToBeRemoved);
        ConnectionManager.connections.put(userGameCommandLeave.getGameID(), sessions);
    }

    private void redraw(UserGameCommandRedraw userGameCommandRedraw, Session rootSession) throws IOException {
        HashSet<SessionGrouping> sessions = ConnectionManager.connections.get(userGameCommandRedraw.getGameID());

        SessionGrouping rootSessionGroup = null;
        if (sessions.isEmpty()) {
            throw new IOException("There was no SessionGroup found for this client!");
        }

        for (SessionGrouping clientSessionGroup : sessions) {
            if (clientSessionGroup.session().equals(rootSession)) {
                rootSessionGroup = clientSessionGroup;
            }
        }

        try {
            ChessGame game = new Gson().fromJson(databaseGameDao.getGame(userGameCommandRedraw.getGameID()), ChessGame.class);

            Collection<ChessMove> highlightedMoves = null;
            if (userGameCommandRedraw.getPosition() != null) {
                highlightedMoves = game.validMoves(userGameCommandRedraw.getPosition());
            }

            assert rootSessionGroup != null;
            rootSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, new Gson().toJson(game), null, null, rootSessionGroup.playerColor(), highlightedMoves)));
        } catch (DataAccessException e) {
            rootSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "Error in redrawing the game!", null, null, null)));
        }
    }

    private ChessGame.TeamColor getTeamColor(String authString, int gameID) {
        ChessGame.TeamColor rootPlayerColor;
        String rootUsername;

        try {
            rootUsername = databaseAuthDao.getUsernameByAuth(authString);

            if (rootUsername.equals(databaseGameDao.checkPlayerSpotTaken(gameID, "WHITE"))) {
                rootPlayerColor = ChessGame.TeamColor.WHITE;
            } else if (rootUsername.equals(databaseGameDao.checkPlayerSpotTaken(gameID, "BLACK"))) {
                rootPlayerColor = ChessGame.TeamColor.BLACK;
            } else {
                rootPlayerColor = null;
            }
        } catch (DataAccessException dataAccessException) {
            rootPlayerColor = null;
        }
        return rootPlayerColor;
    }

    private boolean checkGameStatus(Session rootSession, int gameID) throws IOException {
        try {
            int status = databaseGameDao.checkGameStatus(gameID);
            if (status == 0 || status == -1) {
                rootSession.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, "This game is over!", null, null, null)));
                return false;
            }
        } catch (DataAccessException dataAccessException) {
            return false;
        }

        return true;
    }
}
