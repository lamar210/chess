package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.print("Connected: " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
//            case MAKE_MOVE -> handleMakeMove(session, command);
//            case LEAVE -> handleLeave(session, command);
//            case RESIGN -> handleResign(session, command);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.print("Closed: " + session + "due to" + reason);
        Server.sessions.remove(session);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error: " + error.getMessage());
    }

    private void sendError(Session session, String errorMessage) throws IOException {
        ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        error.setErrorMessage(errorMessage);
        session.getRemote().sendString(gson.toJson(error));
    }

    private void notifyOthers(Session sender, int gameID, String message) throws IOException {
        ServerMessage notify = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notify.setMessage(message);

        for (var entry : Server.sessions.entrySet()) {
            if (!entry.getKey().equals(sender) && entry.getValue() == gameID) {
                entry.getKey().getRemote().sendString(gson.toJson(notify));
            }
        }
    }

    private void handleConnect(Session session, UserGameCommand command) throws IOException, DataAccessException {
        var auth = Server.authDAO.getAuth(command.getAuthToken());
        if (auth == null) {
            sendError(session, "Invalid auth token");
            return;
        }

        var gameData = Server.gameDAO.getGame(command.getGameID());
        if (gameData == null) {
            sendError(session, "Game not found");
            return;
        }

        Server.sessions.put(session, gameData.gameID());

        ChessGame game = gameData.game();

        ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadGame.setGame(game);
        session.getRemote().sendString(gson.toJson(loadGame));

        String note = "%s has joined the game".formatted(auth.username());
        notifyOthers(session, gameData.gameID(), note);
    }
}
