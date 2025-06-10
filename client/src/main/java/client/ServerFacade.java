package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.net.URI;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;


public class ServerFacade {
    private final String url;
    private String authToken;
    private WebSocket ws;

    public void setWebSocket(WebSocket ws) {
        this.ws = ws;
    }

    public ServerFacade(int port) {
        this("localhost:8080");
    }

    public ServerFacade(String serverDomain) {
        this.url = "http://" + serverDomain;
    }

    private void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    private HttpURLConnection connection(String method, String endpoint, String body) throws IOException, URISyntaxException {
        URI uri = new URI(url + endpoint);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);

        if(getAuthToken() != null) {
            http.setRequestProperty("authorization", getAuthToken());
        }

        if (body != null) {
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
        http.connect();
        return http;
    }

    public Map request(String method, String endpoint, String body) {
        Map result;

        try {
            HttpURLConnection http = connection(method, endpoint, body);
            try (var respBody = http.getInputStream();
                 var reader = new java.io.InputStreamReader(respBody)) {
                result = new Gson().fromJson(reader, Map.class);
            }
        } catch (IOException | URISyntaxException ex) {
            result = Map.of("Error", ex.getMessage());
        }
        return result;
    }

    public boolean register(String u, String p, String e) {
        var body = Map.of (
                "username", u,
                "password", p,
                "email", e
        );

        String jsonBody = new Gson().toJson(body);
        Map res = request("POST", "/user", jsonBody);

        if (res.containsKey("Error")) {
            return false;
        }
        setAuthToken((String) res.get("authToken"));
        return true;
    }

    public boolean login(String u, String p) {
        var body = Map.of (
                "username", u,
                "password", p
        );

        String jsonBody = new Gson().toJson(body);
        Map res = request("POST", "/session", jsonBody);

        if (res.containsKey("Error")) {
            return false;
        }
        setAuthToken((String) res.get("authToken"));
        return true;
    }

    public boolean logout() {
        Map res = request("DELETE", "/session", null);

        if (res.containsKey("Error")) {
            return false;
        }
        setAuthToken(null);
        return true;
    }

    public int createGame(String gameName) {
        var body = Map.of("gameName", gameName);
        var jsonBody = new Gson().toJson(body);
        Map res = request("POST", "/game", jsonBody);

        if (res.containsKey("Error")) {
            return -1;
        }
        return ((Double) res.get("gameID")).intValue();
    }

    public Collection<GameData> listGames() {
        var res = request("GET", "/game", null);

        if (res.containsKey("Error")) {
            return new HashSet<>();
        }
        Object gameObj = res.get("games");
        String gamesJson = new Gson().toJson(gameObj);

        return new Gson().fromJson(gamesJson, new TypeToken<Collection<GameData>>(){}.getType());
    }

    public void connToWs (ChessGame.TeamColor color, int gameID) {
        try {
            ws = new WebSocket(color, authToken, gameID);
        } catch (Exception ex) {
            System.err.println("Failed to open ws: " + ex.getMessage());
        }
    }

    public boolean joinGame(ChessGame.TeamColor color, int gameID) {
        Map body;

        if (color != null) {
            body = Map.of("playerColor", color, "gameID", gameID);
        } else {
            body = Map.of("gameID", gameID);
        }

        var jsonBody = new Gson().toJson(body);
        var res = request("PUT", "/game", jsonBody);

        return !res.containsKey("Error");
    }

    public void sendMakeMove(int gameID, String authToken, ChessMove move) throws IOException {
        UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
        cmd.setMove(move);
        ws.sendMessage(new Gson().toJson(cmd));
    }
    public void joinPlayer(int gameID) throws IOException {
        UserGameCommand msg = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        ws.sendMessage(new Gson().toJson(msg));
    }

    public void sendResign(int gameID, String authToken) throws  IOException {
        UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        ws.sendMessage(new Gson().toJson(cmd));
    }

    public void sendLeaveGame(int gameID, String authToken) throws IOException {
        UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        ws.sendMessage(new Gson().toJson(cmd));
    }

    public void redraw(Integer gameID, ChessGame game) {
        ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        msg.setGame(game);
        String command = new Gson().toJson(msg);
        ws.handleMessage(command);
    }
}