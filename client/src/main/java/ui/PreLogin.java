package ui;

import client.ServerFacade;


import java.io.IOException;
import java.util.Scanner;

public class PreLogin {

    ServerFacade facade;

    public PreLogin(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() throws IOException {
        boolean loggedIn = false;

        while (!loggedIn) {
            String[] input = getInput();
            String command = input[0];
            switch (command) {
                case "help":
                    helpMenu();
                    break;
                case "quit":
                    System.exit(0);
                case "login":
                    if (input.length != 3) {
                        System.out.println("please provide a username and password");
                        System.out.println("login <USERNAME> <PASSWORD> - login with an existing user");
                    } else if (facade.login(input[1], input[2])) {
                        System.out.printf("Logged in as %s\n", input[1]);
                        loggedIn = true;
                    } else {
                        System.out.println("Invalid username/password");
                    }
                    break;
                case "register":
                    if (input.length != 4) {
                        System.out.println("please provide a username, password, and an email");
                        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                    } else if (facade.register(input[1], input[2], input[3])) {
                        facade.login(input[1], input[2]);
                        System.out.printf("Logged in as %s\n", input[1]);
                        loggedIn = true;
                    } else {
                        System.out.println("Username already in use");
                        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                    }
                    break;
                default:
                    System.out.println("Command not recognized");
                    helpMenu();
                    break;
            }

        }
        PostLogin postLogin = new PostLogin(facade);
        postLogin.run();
    }

    private String[] getInput() {
        System.out.print("\n[LOGGED_OUT] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    public void helpMenu(){
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("login <USERNAME> <PASSWORD> - to play chess");
        System.out.println("quit - playing chess");
        System.out.println("help - with possible commands");
    }


}