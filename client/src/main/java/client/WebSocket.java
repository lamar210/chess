package client;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

@ClientEndpoint
public class WebSocket {

    Session session;
    private final ServerMessageObserver observer;

    public WebSocket(ServerMessageObserver observer, String authToken, int gameID) throws Exception {
        this.observer = observer;
        try {
            URI uri = new URI("ws://localhost:8080/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            UserGameCommand connectedCmd = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT, authToken, gameID);

            String json = new Gson().toJson(connectedCmd);
            sendMessage(json);

        } catch (URISyntaxException | DeploymentException | IOException e){
            throw new Exception("WebSocket connection failed: " + e.getMessage(), e);
        }
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    @OnMessage
    public void onMessage(String message) {
        ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
        observer.notify(msg);
    }

}