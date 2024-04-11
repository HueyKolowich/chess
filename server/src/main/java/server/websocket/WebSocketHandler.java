package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
//    @OnWebSocketConnect
//    public void onConnect(Session session) throws Exception {
//        session.getRemote().sendString("@OnWebSocketConnect");
//    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception { //TODO This might need to be better than just throwing Exception
        session.getRemote().sendString("WebSocket response: " + message);
    }

//    @OnWebSocketError
//    public void onError(Session session, Throwable cause) throws Exception {
//        session.getRemote().sendString("@OnWebSocketError: " + cause.getMessage());
//    }
}
