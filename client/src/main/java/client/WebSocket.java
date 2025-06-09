package client;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.BoardLayout;
import ui.GamePlayUI;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

@ClientEndpoint
public class WebSocket extends Endpoint{

    Session session;

    private final ChessGame.TeamColor color;

    public WebSocket(ChessGame.TeamColor color, String authToken, int gameID) throws Exception {
        this.color = color;

        try {
            URI uri = new URI("ws://localhost:8080/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String msg) {
                    handleMessage(msg);
                }
            });
            UserGameCommand connectedCmd = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT, authToken, gameID);

            String json = new Gson().toJson(connectedCmd);
            sendMessage(json);

        } catch (URISyntaxException | DeploymentException | IOException e){
            throw new Exception("WebSocket connection failed: " + e.getMessage(), e);
        }
    }

    public void handleMessage(String message){
        ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);

        if (msg.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            GamePlayUI.boardLayout.updateBoard(msg.getGame().getBoard());
            GamePlayUI.boardLayout.displayBoard(color, null);
        } else if (msg.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
            System.out.println(msg.getMessage());
        } else if (msg.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)) {
            System.out.println(msg.getErrorMessage());
        }
    }

    @Override
    public void onOpen (Session session, EndpointConfig endpointConfig) {
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}