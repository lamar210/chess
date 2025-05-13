package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.InMemoryDAO;
import service.*;
import spark.*;

import java.util.Map;

import static spark.Spark.*;

public class Server {

        public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

            var dao         = new InMemoryDAO();
            var userService = new UserService(dao);
            var gameService = new GameService(dao);
            var gson        = new Gson();

            exception(DataAccessException.class, (ex, request, response) -> {response.type("application/json");
                String msg = ex.getMessage().toLowerCase();

                if(msg.contains("bad request")){
                    response.status(400);
                } else if (msg.contains("unauthorized")){
                    response.status(401);
                } else if (msg.contains("already")){
                    response.status(403);
                } else{
                    response.status(500);
                }
                response.body(gson.toJson(Map.of("message", "Error: " + ex.getMessage())));
            } );

            delete("/db", (req,res) -> {
                dao.clear();
                res.type("application/json");
                return "{}";
            });

            post("/user", (req,res) -> {
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
                userService.logout(new LogoutRequest(token));
                res.type("application/json");
                return "{}";
            });

            get("/game", (req, res) -> {
                String token = req.headers("authorization");
                if (token == null) throw new DataAccessException("bad request");
                dao.getAuth(token);
                var games = gameService.listGames();
                res.type("application/json");
                return gson.toJson(Map.of("games", games));
            });

            post("/game",(req, res) -> {
                String token = req.headers("authorization");
                if (token == null) {
                    throw new DataAccessException("bad request");
                }
                var auth = dao.getAuth(token);

                var createReq = gson.fromJson(req.body(), Map.class);
                String gameName = (String)createReq.get("gameName");

                int newID = dao.nextGameID();
                var svcReq = new CreateGameReq(newID, auth.username(), null, gameName, token);

                CreateGameResult result = gameService.createGame(svcReq);

                res.type("application/json");
                return gson.toJson(Map.of("gameID", result.gameID()));

            });

            put ("/game", (req, res) -> {
               String token = req.headers("authorization");
               if (token == null){
                   throw new DataAccessException("bad request");
               }

               JoinGameReq joinReq = gson.fromJson(req.body(), JoinGameReq.class);

               gameService.joinGame(joinReq);
               res.type("application/json");
               return "{}";
            });


            // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
