package client;

import com.google.gson.Gson;


import java.io.IOException;
import java.net.URI;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;


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

}