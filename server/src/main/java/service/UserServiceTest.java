package service;

import dataaccess.DataAccessException;
import dataaccess.InMemoryDAO;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private InMemoryDAO dao;
    private UserService service;

    @BeforeEach
    void setup() {
        dao = new InMemoryDAO();
        service = new UserService(dao);
    }

    @Test
    void fail_register() throws DataAccessException{
        service.register(new RegisterRequest("lamar", "pw", "hell@.com"));
        assertThrows(DataAccessException.class, () -> service.register(new RegisterRequest("lamar", "pw", "hell@.com")));
    }

    @Test
    void success_register() throws DataAccessException{
        RegisterResult result = service.register(new RegisterRequest("lamar", "pw", "hell@.com"));

        assertEquals("lamar", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void fail_login() {
        assertThrows(DataAccessException.class, () -> service.login(new LoginRequest("lamar", "pw")));
    }

    @Test
    void success_login() throws DataAccessException{
        service.register(new RegisterRequest("lamar","pw","hell@.com"));
        LoginResult result = service.login(new LoginRequest("lamar","pw"));

        assertEquals("lamar", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void fail_logout(){
        assertThrows(DataAccessException.class, () -> service.logout(new LogoutRequest("lamar")));
    }

    @Test
    void success_logout() throws DataAccessException{
        service.register(new RegisterRequest("lamar", "pw", "hell@.com"));
        LoginResult result = service.login(new LoginRequest("lamar", "pw"));
        assertDoesNotThrow(() -> dao.getAuth(result.authToken()));
        service.logout(new LogoutRequest(result.authToken()));
        assertThrows(DataAccessException.class, () -> dao.getAuth(result.authToken()));
    }


}
