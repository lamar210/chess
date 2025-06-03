package client;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import ui.BoardLayout;
import ui.GamePlay;
import websocket.messages.ServerMessage;

public class WebSocket extends Endpoint {

    Session session;


    public WebSocket(String serverDomain) throws Exception {
        try {
            URI uri = new URI("ws://" + serverDomain + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e){
            throw new Exception();
        }
    }
    public void handleMessage(String message){
        ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
        BoardLayout boardLayout = new BoardLayout(msg.getGame());
        if (msg.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)){
            GamePlay.boardLayout.updateGame(msg.getGame());
            boardLayout.printBoard(BoardLayout.team, null);
        } else if (msg.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)){
            System.out.println(msg.getMessageError());
        } else if (msg.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)){
            System.out.println(msg.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}