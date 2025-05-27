package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MySqlDataAccess;
import service.CreateGameReq;
import service.CreateGameResult;
import service.LoginRequest;
import service.RegisterRequest;
import service.*;
import spark.*;

import java.util.Map;

import static spark.Spark.*;


public class Server {

    private int port;

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        this.port = desiredPort;
        Spark.staticFiles.location("web");

        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
        } catch (DataAccessException ex) {
            System.err.println("Error configuring database: " + ex.getMessage());
        }

        var dao = new MySqlDataAccess();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);
        var gson = new GsonBuilder().serializeNulls().create();

        configureExceptions(gson);
        registerRoutes(dao, userService, gameService, gson);

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public int getPort() {
        return port;
    }

    private void configureExceptions(com.google.gson.Gson gson) {
        exception(DataAccessException.class, (ex, req, res) -> {
            res.type("application/json");
            String msg = ex.getMessage().toLowerCase();

            if (msg.contains("bad request")) {
                res.status(400);
            }
            else if (msg.contains("unauthorized") || msg.contains("not found")
                    || msg.contains("invalid token") || msg.contains("invalid auth")
                    || msg.contains("user not found") || msg.contains("invalid credentials")) {
                    res.status(401);
                }
            else if (msg.contains("already")) {
                    res.status(403);
                }
                else {
                    res.status(500);
                }

                res.body(gson.toJson(Map.of("message", "Error: " + ex.getMessage())));
            });
        }

    private void registerRoutes(MySqlDataAccess dao, UserService userService, GameService gameService, Gson gson) {
        delete("/db", (req, res) -> {
            dao.clear();
            res.type("application/json");
            return "{}";
        });

        post("/user", (req, res) -> {
            var registerReq = gson.fromJson(req.body(), RegisterRequest.class);
            var registersRes = userService.register(registerReq);
            res.type("application/json");
            return gson.toJson(registersRes);
        });

        post("/session", (req, res) -> {
            var loginReq = gson.fromJson(req.body(), LoginRequest.class);
            var loginRes = userService.login(loginReq);
            res.type("application/json");
            return gson.toJson(loginRes);
        });

        delete("/session", (req, res) -> {
            String token = req.headers("authorization");
            if (token == null|| dao.getAuth(token) == null) {
                throw new DataAccessException("Unauthorized");
            }
            userService.logout(new LogoutRequest(token));
            res.type("application/json");
            return "{}";
        });

        get("/game", (req, res) -> {
            String token = req.headers("authorization");
            if (token == null || dao.getAuth(token) == null) {
                throw new DataAccessException("Unauthorized");
            }
            dao.getAuth(token);
            var games = gameService.listGames();
            res.type("application/json");
            return gson.toJson(Map.of("games", games));
        });

        post("/game", (req, res) -> {
            String token = req.headers("authorization");
            if (token == null) {
                throw new DataAccessException("Unauthorized");
            }

            var auth = dao.getAuth(token);
            if (auth == null) {
                throw new DataAccessException("Unauthorized");
            }

            @SuppressWarnings("unchecked")
            Map<String, ?> body = gson.fromJson(req.body(), Map.class);
            String gameName = (String) body.get("gameName");
            if (gameName == null) {
                throw new DataAccessException("Bad request");
            }

            int newID = dao.nextGameID();
            var svcReq = new CreateGameReq(newID, null, null, gameName, token);
            CreateGameResult result = gameService.createGame(svcReq);

            res.type("application/json");
            return gson.toJson(Map.of("gameID", result.gameID()));
        });

        put("/game", (req, res) -> {
            String token = req.headers("authorization");
            if (token == null || dao.getAuth(token) == null) {
                throw new DataAccessException("Unauthorized");
            }

            dao.getAuth(token);
            @SuppressWarnings("unchecked")
            Map<String, ?> body = gson.fromJson(req.body(), Map.class);
            Number gameID = (Number) body.get("gameID");
            String color = (String) body.get("playerColor");

            if (gameID == null || color == null) {
                throw new DataAccessException("Bad request");
            }

            String norm = color.trim().toUpperCase();
            if (!norm.equals("WHITE") && !norm.equals("BLACK")) {
                throw new DataAccessException("Bad request");
            }

            var joinReq = new JoinGameReq(gameID.intValue(), JoinGameReq.Color.valueOf(norm), token);
            gameService.joinGame(joinReq);
            res.type("application/json");
            return "{}";
        });
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}