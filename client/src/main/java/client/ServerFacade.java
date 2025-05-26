package client;

import com.google.gson.Gson;
import model.AuthData;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import model.*;

public class ServerFacade {

    private String serverUrl;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String pass, String email) throws Exception {
        URL url = new URL(serverUrl + "/user");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        RegisterRequest request = new RegisterRequest(username, pass, email);
        try (var out = new OutputStreamWriter(conn.getOutputStream())) {
            new Gson().toJson(request, out);
        }

        if (conn.getResponseCode() == 200) {
            try (var in = new InputStreamReader(conn.getInputStream())) {
                RegisterResult result = new Gson().fromJson(in, RegisterResult.class);
                return new AuthData(result.authToken(), result.username());
            }
        } else {
            throw new RuntimeException("Error: " + conn.getResponseMessage());
        }
    }

    public AuthData login(String u, String p) throws Exception{
        URL url = new URL(serverUrl + "/session");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        LoginRequest request = new LoginRequest(u, p);
        try (var out = new OutputStreamWriter(conn.getOutputStream())) {
            new Gson().toJson(request, out);
        }

        if (conn.getResponseCode() == 200) {
            try (var in = new InputStreamReader(conn.getInputStream())) {
                LoginResult result = new Gson().fromJson(in, LoginResult.class);
                return new AuthData(result.authToken(), result.username());
            }
        } else {
            throw new RuntimeException("Error: " + conn.getResponseMessage());
        }

    }

    public List<GameData> listGames(String authToken) throws Exception {
        URL url = new URL(serverUrl + "/game");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestProperty("authorization", authToken);

        if (conn.getResponseCode() == 200) {
            try (var in = new InputStreamReader(conn.getInputStream())) {
                ListGamesResult r = new Gson().fromJson(in, ListGamesResult.class);
                return r.games();
            }
        } else {
            throw new RuntimeException("Error: " + conn.getResponseMessage());
        }
    }
}
