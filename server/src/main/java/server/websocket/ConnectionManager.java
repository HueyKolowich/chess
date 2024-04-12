package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.HashSet;

public class ConnectionManager {
    static HashMap<Integer, HashSet<SessionGrouping>> connections = new HashMap<>();

    public void addClientSession(int gameID, Session session, ChessGame.TeamColor playerColor) {
        HashSet<SessionGrouping> sessions = ConnectionManager.connections.get(gameID);
        if (sessions != null) {
            sessions.add(new SessionGrouping(session, playerColor));
            ConnectionManager.connections.put(gameID, sessions);
        } else {
            ConnectionManager.connections.put(gameID, new HashSet<>() {{ add(new SessionGrouping(session, playerColor)); }});
        }
    }
}
