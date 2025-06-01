package client;

import org.eclipse.jetty.websocket.client.WebSocketClient;

public class WebSocketCommunicator extends WebSocketClient {

    private final ServerMessageObserver observer;

    public WebSocketCommunicator(ServerMessageObserver observer, String authToken, int gameID) {
        this.observer = observer;
        this.connect();

        while (!this.isOpen()) {
            Thread.sleep(100);
        }
    }
}
