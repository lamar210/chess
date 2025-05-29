package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import server.Server;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clearDB() {
        var res = facade.request("DELETE", "/db", null);
        Assertions.assertFalse(res.containsKey("Error"), "Failed to clear database before test");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void validRegister() {
        var username = "testUser";
        var password = "somePass";
        var email = "anEmail@q.com";

        Assertions.assertTrue(facade.register(username, password, email));

    }

    @Test
    public void registerTwice() {
        var u = "sameUser";
        var p = "pass2";
        var e = "email";

        facade.register(u, p ,e);
        Assertions.assertFalse(facade.register(u, p, e));
    }

    @Test
    public void validLogin() {
        var u = "User";
        var p = "pass";
        var e = "email";

        facade.register(u, p ,e);
        Assertions.assertTrue(facade.login(u, p));
    }

    @Test
    public void invalidLogin() {
        var u = "nonexistentUser";
        var p = "wrongPass";

        Assertions.assertFalse(facade.login(u, p));
    }

    @Test
    public void validLogout() {
        var u = "User";
        var p = "pass";
        var e = "email";

        facade.register(u, p ,e);
        facade.login(u, p);
        Assertions.assertTrue(facade.logout());
    }

    @Test
    public void invalidLogout() {
        Assertions.assertFalse(facade.logout());
    }

    @Test
    public void validCreateGame() {
        var gameName = "gameName";
        var u = "User";
        var p = "pass";
        var e = "email";

        facade.register(u, p ,e);
        facade.login(u, p);

        int gameID = facade.createGame(gameName);
        Assertions.assertTrue(gameID > 0, "Couldn't create game ");
    }

    @Test
    public void invalidCreateGame() {
        var gameName = "whatIsThisGameBebzi";
        Assertions.assertEquals(-1, facade.createGame(gameName));
    }

    @Test
    public void validListGames() {
        var u = "User";
        var p = "pass";
        var e = "email";

        facade.register(u, p, e);
        facade.login(u, p);
        facade.createGame("BYU");
        facade.createGame("UVU");
        Assertions.assertEquals(2, facade.listGames().size());
    }

    @Test
    public void validJoinGame(){
        var u = "User";
        var p = "pass";
        var e = "email";

        facade.register(u, p, e);
        facade.login(u, p);
        int gameID = facade.createGame("BYU");
        Assertions.assertTrue(facade.joinGame(ChessGame.TeamColor.WHITE, gameID));
    }

    @Test
    public void invalidJoinGame(){
        Assertions.assertFalse(facade.joinGame(ChessGame.TeamColor.WHITE, 0));
    }
}
