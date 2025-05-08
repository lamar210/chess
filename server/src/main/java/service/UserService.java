package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;


import java.util.UUID;


public class UserService {

    private final DataAccess dao;

    public UserService(DataAccess dao){
        this. dao = dao;
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException{
        if (req.username() == null || req.password() == null || req.email() == null){
            throw new DataAccessException("Bad request");
        }

        UserData user = new UserData(req.username(), req.password(), req.email());
        dao.createUser(user);

        String token = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(token, req.username()));

        return new RegisterResult(req.username(), token);
    }

    public LoginResult login(LoginRequest req) throws DataAccessException{

        if (req.username() == null || req.password() == null){
            throw new DataAccessException("Bad request");
        }

        UserData user = dao.getUser(req.username());
        if (!user.password().equals(req.password())){
            throw new DataAccessException("Unauthorized");
        }

        String token = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(token, req.username()));

        return new LoginResult(req.username(), token);


    }

    public void logout(LogoutRequest req) throws DataAccessException{

        if (req.authToken() == null){
            throw new DataAccessException("Bad request");
        }
        dao.getAuth(req.authToken());
        dao.deleteAuth(req.authToken());
    }
}
