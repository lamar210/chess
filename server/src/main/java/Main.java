import chess.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import server.Server;


public class Main {
    public static void main(String[] args) throws DataAccessException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        DataAccess dao = new MySqlDataAccess();
        dao.clear();

        Server server = new Server();
        server.run(8080);
    }
}