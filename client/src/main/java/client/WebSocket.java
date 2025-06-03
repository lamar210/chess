package client;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;

public class WebSocket {

    Session session;
    private final ServerMessageObserver observer;

    public WebSocket(ServerMessageObserver observer, String authToken, int gameID) throws Exception {
        this.observer = observer;
        try {
            URI uri = new URI("ws://localhost:8080/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

        } catch (URISyntaxException | DeploymentException | IOException e){
            throw new Exception();
        }
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}