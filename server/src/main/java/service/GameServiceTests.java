package service;

import model.GameData;
import model.AuthData;
import dataaccess.DataAccessException;
import dataaccess.InMemoryDAO;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;


public class GameServiceTests {

    private InMemoryDAO dao;
    private GameService service;

    @BeforeEach
    void setup() {
        dao = new InMemoryDAO();
        service = new GameService(dao);
    }

    @Test
    void fail_createGame_NULL_ID() {
        String token = UUID.randomUUID().toString();
        assertThrows(DataAccessException.class, () -> service.createGame(new CreateGameReq(null, "white", "black", "firstGame", token)));
    }

    @Test
    void fail_createGame_NULL_WHITE() {
        String token = UUID.randomUUID().toString();
        assertThrows(DataAccessException.class, () -> service.createGame(new CreateGameReq(1, null, "black", "firstGame", token)));
    }

    @Test
    void fail_createGame_NULL_BLACK() {
        String token = UUID.randomUUID().toString();
        assertThrows(DataAccessException.class, () -> service.createGame(new CreateGameReq(1, "white", null, "firstGame", token)));
    }

    @Test
    void fail_createGame_NULL_GAMENAME() {
        String token = UUID.randomUUID().toString();
        assertThrows(DataAccessException.class, () -> service.createGame(new CreateGameReq(1, "white", "black", null, token)));
    }

    @Test
    void fail_createGame_NULL_TOKEN() {
        assertThrows(DataAccessException.class, () -> service.createGame(new CreateGameReq(1, "white", "black", "firstGame", null)));
    }

    @Test
    void fail_createGame_invalid_TOKEN() {
        CreateGameReq badReq = new CreateGameReq(22, "white", "black", "myGame", "totally‑not-valid‑token");
        assertThrows(DataAccessException.class, () -> service.createGame(badReq));

    }

    @Test
    void success_createGame() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(token, "white"));
        CreateGameResult result = service.createGame(new CreateGameReq(22, "white", "black", "firstGame", token));
        assertEquals(22, result.gameID());
        GameData stored = dao.getGame(22);
        assertEquals("white", stored.whiteUsername());
        assertEquals("black", stored.blackUsername());
        assertEquals("firstGame", stored.gameName());
        assertNotNull(stored.game());
    }

    @Test
    void getGame_notFound() {
        assertThrows(DataAccessException.class, () -> service.getGame(99));
    }

    @Test
    void listGames_returnsAll() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(token, "white"));
        service.createGame(new CreateGameReq(1, "white", "black", "g1", token));
        service.createGame(new CreateGameReq(2, "white", "black", "g2", token));
        service.createGame(new CreateGameReq(3, "white", "black", "g3", token));
        assertEquals(3, service.listGames().size());
    }

    @Test
    void success_joinGame() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(token, "rick"));

        String mortyToken = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(mortyToken, "morty"));

        service.createGame(new CreateGameReq(1, "morty", null, "g", mortyToken));

        service.joinGame(new JoinGameReq(1, JoinGameReq.Color.BLACK, token));

        GameData updated = dao.getGame(1);
        assertEquals("morty", updated.whiteUsername());
        assertEquals("rick", updated.blackUsername());
    }

    @Test
    void fail_joinGame_NULL_ID() {
        assertThrows(DataAccessException.class, () -> service.joinGame(new JoinGameReq(null, JoinGameReq.Color.WHITE, "token")));
    }

    @Test
    void fail_joinGame_NULL_COLOR() throws DataAccessException{
        String token = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(token,"u"));
        assertThrows(DataAccessException.class, () -> service.joinGame(new JoinGameReq(1, null, token)));
    }

    @Test
    void fail_joinGame_NULL_TOKEN() {
        assertThrows(DataAccessException.class, () -> service.joinGame(new JoinGameReq(1, JoinGameReq.Color.WHITE, null)));
    }

    @Test
    void fail_joinGame_invalid_TOKEN() {
        String bad = "def-not-a-token";
        assertThrows(DataAccessException.class, () -> service.joinGame(new JoinGameReq(1, JoinGameReq.Color.WHITE, bad)));
    }

    @Test
    void fail_joinGame_gameNotFound() throws DataAccessException{
        String token = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(token,"u"));
        assertThrows(DataAccessException.class, () -> service.joinGame(new JoinGameReq(101, JoinGameReq.Color.WHITE, token)));
    }

    @Test
    void fail_joinGame_whiteAlreadyTaken() throws DataAccessException {
        String t1 = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(t1,"alice"));
        service.createGame(new CreateGameReq(1,"alice",null,"wonderland",t1));

        String t2 = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(t2,"bob"));
        assertThrows(DataAccessException.class, () -> service.joinGame(new JoinGameReq(1,JoinGameReq.Color.WHITE,t2)));
    }

    @Test
    void fail_joinGame_blackAlreadyTaken_whenBlackPreassigned() throws DataAccessException {
        String aliceToken = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(aliceToken, "alice"));
        service.createGame(new CreateGameReq(1, null, "alice", "wonderland", aliceToken));
        String bobToken = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(bobToken, "bob"));
        assertThrows(DataAccessException.class, () -> service.joinGame(new JoinGameReq(1, JoinGameReq.Color.BLACK, bobToken))
        );
    }



}