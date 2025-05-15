package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

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
        throw new UnsupportedOperationException("Not implemented");
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
