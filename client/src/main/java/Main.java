import client.ServerFacade;
import ui.PreLogin;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws Exception {

        ServerFacade facade = new ServerFacade("localhost:8080");
        System.out.println("Welcome to 240 chess. Type Help to get started.");
        PreLogin preLogin = new PreLogin(facade);
        preLogin.run();

    }
}