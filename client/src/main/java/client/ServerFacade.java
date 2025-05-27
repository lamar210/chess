package client;

import com.google.gson.Gson;


import java.io.IOException;
import java.net.URI;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.Map;


public class ServerFacade {
    private final String url;
    private String authToken;

    public ServerFacade() {
        this("localhost:8080");
    }

    public ServerFacade(String serverDomain) {
        this.url = "http://" + serverDomain;
    }

    private void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private String getAuthToken() {
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

    private Map request(String method, String endpoint, String body) {
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
            System.out.println("Register failed: " + res.get("Error"));
            return false;
        }
        setAuthToken((String) res.get("authToken"));
        return true;
    }

}