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
        if (req.gameID() == null || req.whiteUsername() == null || req.blackUsername() == null || req.authToken() == null){
            throw new DataAccessException("Bad request");
        }

        dao.getAuth(req.authToken());

        ChessGame newBoard = new ChessGame();
        GameData gd = new GameData(req.gameID(), req.whiteUsername(), req.blackUsername(), req.gameName(), newBoard);
        dao.createGame(gd);

        return new CreateGameResult(gd.gameID());
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return dao.getGame(gameID);
    }

    public List<GameData> listGames() throws DataAccessException {
        return dao.listGames();
    }


}
