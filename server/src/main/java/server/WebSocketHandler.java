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

        if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            var gameID = command.getGameID();
            var token = command.getAuthToken();
            var move = command.getMove();

            var auth = Server.authDAO.getAuth(token);
            if (auth == null) {
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Invalid or expired auth token.");
                session.getRemote().sendString(gson.toJson(error));
                return;
            }

            var gameData = Server.gameDAO.getGame(gameID);
            if (gameData == null) {
                ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Game not found");
                session.getRemote().sendString(gson.toJson(error));
                return;
            }

            Server.sessions.put(session, gameID);
            ChessGame game = gameData.game();



            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(game);
            session.getRemote().sendString(gson.toJson(loadGame));

            var username = auth.username();
            String note = "%s has joined the game.".formatted(username);

            ServerMessage notify = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notify.setMessage(note);

            for (var entry : Server.sessions.entrySet()) {
                Session s = entry.getKey();
                int sessionGameId = entry.getValue();
                if (!s.equals(session) && sessionGameId == gameID) {
                    s.getRemote().sendString(gson.toJson(notify));
                }
            }
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
}
