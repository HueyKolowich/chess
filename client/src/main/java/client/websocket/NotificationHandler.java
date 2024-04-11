package client.websocket;

import webSocketMessages.serverMessages.*;

public interface NotificationHandler {
    void notify(ServerMessage serverMessage);
    void testNotify(String message);
}
