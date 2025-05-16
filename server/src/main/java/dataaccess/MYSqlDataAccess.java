package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.List;

public class MYSqlDataAccess implements DataAccess {

    private final Gson gson = new Gson();

    public MYSqlDataAccess() {
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var stmt1 = conn.prepareStatement("DELETE FROM auth");
             var stmt2 = conn.prepareStatement("DELETE FROM game");
             var stmt3 = conn.prepareStatement("DELETE FROM user")) {

            stmt1.executeUpdate();
            stmt2.executeUpdate();
            stmt3.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Could not clear tables", ex);
        }
    }


    @Override
    public void createUser (UserData user) throws DataAccessException {
        String hashedPass = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?,?,?)")) {
            stmt.setString(1, user.username());
            stmt.setString(2, hashedPass);
            stmt.setString(3, user.email());
            System.out.println("HASHED PASSWORD: " + hashedPass);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Could not create user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String query = "SELECT * FROM user WHERE username = ?";

        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    return new UserData(username, password, email);
                } else {
                    System.out.println("User not found in DB: " + username); // 👈 DEBUG LINE
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Could not fetch user", ex);
        }
    }


    @Override
    public void createGame(GameData game) throws DataAccessException {
        String insert = "INSERT INTO game (gameID, gameName, whiteUsername, blackUsername, gameState) VALUES (?, ?, ?, ?, ?)";

        Gson gson = new Gson();
        String gameStateJson = gson.toJson(game.game());

        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(insert)) {
            stmt.setInt(1, game.gameID());
            stmt.setString(2, game.gameName());
            stmt.setString(3, game.whiteUsername());
            stmt.setString(4, game.blackUsername());
            stmt.setString(5, gameStateJson);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Can't insert game", ex);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String query = "SELECT * FROM game WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, gameID);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String gameName = rs.getString("gameName");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameStateJson = rs.getString("gameState");

                    Gson gson = new Gson();

                    ChessGame chessGame = gson.fromJson(gameStateJson, ChessGame.class);

                    return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Can't retrieve game", ex);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int nextGameID() throws DataAccessException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
