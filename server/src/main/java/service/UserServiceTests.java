package service;

import dataaccess.DataAccessException;
import dataaccess.InMemoryDAO;
import dataaccess.MySqlDataAccess;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private MySqlDataAccess dao;
    private UserService service;

    @BeforeEach
    void setup() throws DataAccessException{
        dao = new MySqlDataAccess();
        dao.clear();
        service = new UserService(dao);
    }

    @Test
    void failRegister() throws DataAccessException{
        service.register(new RegisterRequest("lamar", "pw", "hell@.com"));
        assertThrows(DataAccessException.class, () -> service.register(new RegisterRequest("lamar", "pw", "hell@.com")));
    }

    @Test
    void successRegister() throws DataAccessException{
        RegisterResult result = service.register(new RegisterRequest("lamar", "pw", "hell@.com"));

        assertEquals("lamar", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void failLogin() {
        assertThrows(DataAccessException.class, () -> service.login(new LoginRequest("lamar", "pw")));
    }

    @Test
    void successLogin() throws DataAccessException{
        service.register(new RegisterRequest("lamar","pw","hell@.com"));
        LoginResult result = service.login(new LoginRequest("lamar","pw"));

        assertEquals("lamar", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void failLogout(){
        assertThrows(DataAccessException.class, () -> service.logout(new LogoutRequest("lamar")));
    }

    @Test
    void successLogout() throws DataAccessException{
        service.register(new RegisterRequest("lamar", "pw", "hell@.com"));
        LoginResult result = service.login(new LoginRequest("lamar", "pw"));
        assertDoesNotThrow(() -> dao.getAuth(result.authToken()));
        assertDoesNotThrow(() -> service.logout(new LogoutRequest(result.authToken())));
        assertNull(dao.getAuth(result.authToken()));
    }


}
