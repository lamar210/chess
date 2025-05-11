package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;

import model.GameData;
import service.CreateGameResult;
import service.CreateGameReq;

import java.util.List;



public class GameService {

    private final DataAccess dao;

    public GameService(DataAccess dao){
        this.dao = dao;
    }

    public CreateGameResult createGame(CreateGameReq req) throws DataAccessException{
        if (req.gameID() == null || req.whiteUsername() == null || req.authToken() == null){
            throw new DataAccessException("Bad request");
        }

        dao.getAuth(req.authToken());

        ChessGame newBoard = new ChessGame();
        GameData gd = new GameData(req.gameID(), req.whiteUsername(), null, req.gameName(), newBoard);
        dao.createGame(gd);

        return new CreateGameResult(gd.gameID());
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return dao.getGame(gameID);
    }

    public List<GameData> listGames() throws DataAccessException {
        return dao.listGames();
    }

    public void joinGame(JoinGameReq req) throws DataAccessException{
        if (req.gameID() == null || req.playerColor() == null || req.authToken() == null){
            throw new DataAccessException("Bad request");
        }

        String joiningUser = dao.getAuth(req.authToken()).username();
        GameData g = dao.getGame(req.gameID());

        if (req.playerColor() == JoinGameReq.Color.WHITE){
            if (g.whiteUsername() != null){
                throw new DataAccessException("AlreadyTaken");
            }
            g = new GameData(g.gameID(), joiningUser, g.blackUsername(), g.gameName(), g.game());
        } else {
            if (g.blackUsername() != null) {
                throw new DataAccessException("Already Taken");
            }
            g = new GameData(g.gameID(), g.whiteUsername(), joiningUser, g.gameName(), g.game());
        }
        dao.updateGame(g);
    }


}
