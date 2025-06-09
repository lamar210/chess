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

    public void handleMessage(String message, ChessGame.TeamColor color){
        ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);

        switch (msg.getServerMessageType()) {
            case LOAD_GAME -> {
                ChessGame updateGame = msg.getGame();
                GamePlayUI.boardLayout.updateBoard(updateGame.getBoard());
                GamePlayUI.boardLayout.displayBoard(color, null);
            }
            case ERROR -> {
                System.out.println("Error: " + msg.getErrorMessage());
            }
            case NOTIFICATION -> {
                System.out.println("Notification: " + msg.getMessage());
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    @Override
    public void onOpen (Session session, EndpointConfig endpointConfig) {
    }

    @OnMessage
    public void onMessage(String message) {
        ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
        observer.notify(msg);
    }

}