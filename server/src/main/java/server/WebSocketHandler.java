package server;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import chess.ChessPosition;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
            case MAKE_MOVE -> handleMakeMove(session, command);
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

    private void sendGame(Session session, ChessGame game) throws IOException {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        message.setGame(game);
        session.getRemote().sendString(gson.toJson(message));
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

    private void handleMakeMove(Session session, UserGameCommand command) throws IOException, DataAccessException {
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

        ChessGame game = gameData.game();

        ChessGame.TeamColor playerColor;
        if (auth.username().equals(gameData.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (auth.username().equals(gameData.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            sendError(session, "User is not a player in this game");
            return;
        }

        if (game.getTeamTurn() != playerColor) {
            sendError(session, "It is not your turn");
            return;
        }

        var move = command.getMove();
        try {

            game.makeMove(move);
            GameData updatedData = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game
            );
            Server.gameDAO.updateGame(updatedData);

        } catch (Exception ex) {
            sendError(session, "Illegal move: " + ex.getMessage());
            return;
        }
        sendGame(session, game);

        for (var entry: Server.sessions.entrySet()) {
            if (!entry.getKey().equals(session) && entry.getValue() == gameData.gameID()) {

                ServerMessage load = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                load.setGame(game);
                entry.getKey().getRemote().sendString(gson.toJson(load));

                ServerMessage notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notif.setMessage("%s made a move".formatted(auth.username()));
                entry.getKey().getRemote().sendString(gson.toJson(notif));
            }
        }
    }
}
