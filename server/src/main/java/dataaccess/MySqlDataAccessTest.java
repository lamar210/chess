package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class MySqlDataAccessTest {

    private MySqlDataAccess dao;
    private final UserData user = new UserData("tester", "password123", "test@example.com");
    private final AuthData auth = new AuthData("token123", "tester");
    private final GameData game = new GameData(1, null, null, "Test Game", null);

    @BeforeEach
    void setup() throws DataAccessException{
        dao = new MySqlDataAccess();
        dao.clear();
    }

    @Test
    void validCreateUser() throws DataAccessException {
        dao.createUser(user);
        UserData stored = dao.getUser(user.username());
        assertNotNull(stored);
        assertEquals(user.username(), stored.username());
        assertEquals(user.email(), stored.email());
        assertTrue(BCrypt.checkpw(user.password(), stored.password()));
    }

    @Test
    void validGetUser() throws DataAccessException {
        dao.createUser(user);
        UserData stored = dao.getUser(user.username());
        assertNotNull(stored);
        assertEquals(user.username(), stored.username());
        assertEquals(user.email(), stored.email());
        assertTrue(BCrypt.checkpw(user.password(), stored.password()));
    }

    @Test
    void createUserTwice() throws DataAccessException {
        dao.createUser(user);
        assertThrows(DataAccessException.class, () -> dao.createUser(user));
    }

    @Test
    void validCreateAuth() throws DataAccessException {
        dao.createUser(user);
        dao.createAuth(auth);
        AuthData stored = dao.getAuth(auth.authToken());
        assertEquals(auth, stored);
    }

    @Test
    void invalidGetAuth() throws DataAccessException {
        assertNull(dao.getAuth("nonexistent"));
    }

    @Test
    void validDeleteAuth() throws DataAccessException {
        dao.createUser(user);
        dao.createAuth(auth);
        dao.deleteAuth(auth.authToken());
        assertNull(dao.getAuth(auth.authToken()));
    }

    @Test
    void validCreateGame() throws DataAccessException {
        dao.createGame(game);
        GameData stored = dao.getGame(game.gameID());
        assertNotNull(stored);
        assertEquals(game.gameID(), stored.gameID());
        assertEquals(game.gameName(), stored.gameName());
    }

    @Test
    void invalidGetGame() throws DataAccessException {
        assertNull(dao.getGame(9999));
    }

    @Test
    void validListGames() throws DataAccessException {
        dao.createGame(game);
        List<GameData> games = dao.listGames();
        assertEquals(1, games.size());
        assertEquals(game.gameName(), games.get(0).gameName());
    }

    @Test
    void validUpdateGame() throws DataAccessException {
        dao.createGame(game);
        GameData updated = new GameData(game.gameID(), "tester", "opponent", game.gameName(), game.game());
        dao.updateGame(updated);
        GameData result = dao.getGame(game.gameID());
        assertEquals("tester", result.whiteUsername());
        assertEquals("opponent", result.blackUsername());
    }

    @Test
    void validNextGameID() throws DataAccessException {
        int id1 = dao.nextGameID();
        dao.createGame(new GameData(id1, null, null, "game 1", null));
        int id2 = dao.nextGameID();
        assertEquals(id1 + 1, id2);
    }

    @Test
    void validClear() throws DataAccessException {
        dao.createUser(user);
        dao.clear();
        assertNull(dao.getUser(user.username()));
    }
}
