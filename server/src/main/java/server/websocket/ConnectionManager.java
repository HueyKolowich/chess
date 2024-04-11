package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.HashSet;

public class ConnectionManager {
    static HashMap<Integer, HashSet<Session>> connections = new HashMap<>();

    public void addClientSession(int gameID, Session session) {
        HashSet<Session> sessions = ConnectionManager.connections.get(gameID);
        if (sessions != null) {
            sessions.add(session);
            ConnectionManager.connections.put(gameID, sessions);
        } else {
            ConnectionManager.connections.put(gameID, new HashSet<>() {{ add(session); }});
        }
    }
}
