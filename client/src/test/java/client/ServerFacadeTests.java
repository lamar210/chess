package client;

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

        boolean success = facade.register(username, password, email);

        Assertions.assertTrue(success, "Registration failed");
    }

    @Test
    public void registerTwice() {
        var u = "sameUser";
        var p = "pass2";
        var e = "email";

        boolean SuccessOne = facade.register(u, p ,e);
        Assertions.assertTrue(SuccessOne, "Registration failed");

        boolean SuccessTwo = facade.register(u, p ,e);
        Assertions.assertFalse(SuccessTwo, "Duplicate user");
    }

    @Test
    public void validLogin() {
        var u = "User";
        var p = "pass";
        var e = "email";

        boolean SuccessOne = facade.register(u, p ,e);
        Assertions.assertTrue(SuccessOne, "Registration failed");

        boolean loggedIn = facade.login(u, p);
        Assertions.assertTrue(loggedIn, "Login failed");
    }

    @Test
    public void invalidLogin() {
        var u = "nonexistentUser";
        var p = "wrongPass";

        boolean loggedIn = facade.login(u, p);
        Assertions.assertFalse(loggedIn, "Login failed");
    }

    @Test
    public void validLogout() {
        var u = "User";
        var p = "pass";
        var e = "email";

        boolean SuccessOne = facade.register(u, p ,e);
        Assertions.assertTrue(SuccessOne, "Registration failed");

        boolean loggedIn = facade.login(u, p);
        Assertions.assertTrue(loggedIn, "Login failed");
        boolean loggedOut = facade.logout();
        Assertions.assertTrue(loggedOut, "Logout failed");

    }

    @Test
    public void invalidLogout() {
        boolean loggedOut = facade.logout();
        Assertions.assertFalse(loggedOut, "Logout failed");
    }

    @Test
    public void validCreateGame() {
        var gameName = "gameName";
        var u = "User";
        var p = "pass";
        var e = "email";

        boolean registered = facade.register(u, p ,e);
        Assertions.assertTrue(registered, "Registration failed");

        boolean loggedIn = facade.login(u, p);
        Assertions.assertTrue(loggedIn, "Login failed");

        int gameID = facade.createGame(gameName);
        Assertions.assertTrue(gameID > 0, "Couldn't create game ");
    }
}
