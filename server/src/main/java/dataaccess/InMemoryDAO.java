package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import dataaccess.DataAccessException;


public class InMemoryDAO implements DataAccess{

    Map<String, UserData> users;
    Map<Integer, GameData> games;
    Map<String, AuthData> auths;

    public InMemoryDAO() {
        this.users = new HashMap<>();
        this.games = new HashMap<>();
        this.auths = new HashMap<>();
    }

    @Override
    public void clear() throws DataAccessException{
        users.clear();
        games.clear();
        auths.clear();

    }

    @Override
    public void createUser(UserData user) throws DataAccessException{


    }

    @Override
    public UserData getUser(String username) throws DataAccessException{

    }

    @Override
    public void createGame(GameData game) throws DataAccessException{

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException{

    }

    @Override
    public List<GameData> listGames() throws DataAccessException{

    }

    @Override
    public void updateGame(GameData game) throws DataAccessException{

    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{

    }
}
