import chess.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import server.Server;


public class Main {
    public static void main(String[] args) throws DataAccessException {
        System.out.println("♕ 240 Chess Server");

        DataAccess dao = new MySqlDataAccess();
        dao.clear();

        Server server = new Server();
        server.run(8080);
    }
}