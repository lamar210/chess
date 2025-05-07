package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
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

        if (users.containsKey(user.username())){
            throw new DataAccessException("User already exists");
        } else {
            users.put(user.username(), user);
        }

    }

    @Override
    public UserData getUser(String username) throws DataAccessException{

        UserData u = users.get(username);
        if (u == null) {
            throw new DataAccessException("User not found");
        }
        return u;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException{

        if (games.containsKey(game.gameID())){
            throw new DataAccessException("Game already exists");
        } else {
            games.put(game.gameID(), game);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException{

        GameData g = games.get(gameID);
        if (g == null) {
            throw new DataAccessException("Game not found");
        }
        return g;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException{
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException{

        if (!games.containsKey(game.gameID())){
            throw new DataAccessException("Game can't be updated. It doesn't exist.");
        } else {
            games.put(game.gameID(), game);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{

        AuthData a = auths.get(authToken);
        if (a == null) {
            throw new DataAccessException("Invalid auth token");
        }
        return a;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{

        if (auths.remove(authToken) == null) {
            throw new DataAccessException("Token not found");
        }
    }
}
