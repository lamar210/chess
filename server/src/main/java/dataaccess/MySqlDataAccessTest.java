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
    void setup() throws DataAccessException {
        dao = new MySqlDataAccess();
        dao.clear();
    }

    private void assertUserMatch(UserData expected, UserData actual) {
        assertNotNull(actual);
        assertEquals(expected.username(), actual.username());
        assertEquals(expected.email(), actual.email());
        assertTrue(BCrypt.checkpw(expected.password(), actual.password()));
    }

    @Test
    void validCreateUser() throws DataAccessException {
        dao.createUser(user);
        UserData stored = dao.getUser(user.username());
        assertUserMatch(user, stored);
    }

    @Test
    void validGetUser() throws DataAccessException {
        dao.createUser(user);
        UserData stored = dao.getUser(user.username());
        assertUserMatch(user, stored);
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
    void invalidDeleteAuthDoesNotThrow() {
        assertDoesNotThrow(() -> dao.deleteAuth("nonexistent"));
    }

    @Test
    void createAuthTwiceThrows() throws DataAccessException {
        dao.createUser(user);
        dao.createAuth(auth);
        assertThrows(DataAccessException.class, () -> dao.createAuth(auth));
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
    void listGamesEmptyWhenNoneCreated() throws DataAccessException {
        List<GameData> games = dao.listGames();
        assertTrue(games.isEmpty());
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
    void updateGameNotExistingFails() {
        GameData updated = new GameData(9999, "nope", "stillNope", "ghostGame", null);
        assertThrows(DataAccessException.class, () -> dao.updateGame(updated));
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

    @Test
    void clearRemovesGames() throws DataAccessException {
        dao.createGame(game);
        dao.clear();
        assertTrue(dao.listGames().isEmpty());
    }

    @Test
    void clearRemovesAuth() throws DataAccessException {
        dao.createUser(user);
        dao.createAuth(auth);
        dao.clear();
        assertNull(dao.getAuth(auth.authToken()));
    }
}
