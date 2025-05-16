package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;

public class MYSqlDataAccess implements DataAccess {

    public MYSqlDataAccess() {
    }

    @Override
    public void clear () throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    @Override
    public void createUser (UserData user) throws DataAccessException {
        String hashedPass = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement("INSERT INTO user (username, password) VALUES (?,?)")) {
            stmt.setString(1, user.username());
            stmt.setString(2, user.password());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Could not create user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        throw new UnsupportedOperationException("Not implemented");
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
