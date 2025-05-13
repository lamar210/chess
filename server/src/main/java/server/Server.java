package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.InMemoryDAO;
import service.GameService;
import service.RegisterRequest;
import service.UserService;
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

            exception(DataAccessException.class, (ex, req, res) -> {res.type("application/json");
                String msg = ex.getMessage().toLowerCase();

                if(msg.contains("bad request")){
                    res.status(400);
                } else if (msg.contains("unauthorized")){
                    res.status(401);
                } else if (msg.contains("already")){
                    res.status(403);
                } else{
                    res.status(500);
                }
                res.body(gson.toJson(Map.of("message", "Error: " + ex.getMessage())));
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
