package ui;

import client.ServerFacade;
import chess.ChessGame;
import client.ServerMessageObserver;
import client.WebSocket;
import model.GameData;
import websocket.messages.ServerMessage;

import java.util.Scanner;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class PostLogin {

    ServerFacade facade;
    Collection<GameData> games;

    public PostLogin(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() throws Exception {
        boolean loggedIn = true;

        while (loggedIn) {
            String[] input = getInput();
            String command = input[0].toLowerCase();

            switch (command) {
                case "help" -> helpMenu();
                case "logout" -> {
                    if (facade.logout()) {
                        System.out.print("Goodbye! („• ֊ •„)੭");
                        loggedIn = false;
                    }
                }
                case "create" -> {
                    if (input.length != 2) {
                        System.out.println("Usage: create <NAME>");
                    } else {
                        facade.createGame(input[1]);
                        System.out.printf("Game '%s' created!\n", input[1]);
                    }
                }
                case "list" -> {
                    games = facade.listGames();
                    int i = 1;
                    for (GameData game : games) {
                        System.out.printf("%d. Game: %s, White: %s, Black: %s\n",
                                i++, game.gameName(), game.whiteUsername(), game.blackUsername());
                    }
                }
                case "join" -> {

                    if (input.length != 3 || !input[1].matches("\\d+") ||
                            !input[2].equalsIgnoreCase("white") && !input[2].equalsIgnoreCase("black")) {
                        System.out.println("Usage: join <ID> [WHITE|BLACK]");
                        break;
                    }

                    games = facade.listGames();
                    List<GameData> gameList = new ArrayList<>(games);
                    int index = Integer.parseInt(input[1]) - 1;

                    if (index < 0 || index >= gameList.size()) {
                        System.out.println("Invalid game ID.");
                        break;
                    }

                    GameData game = gameList.get(index);
                    ChessGame.TeamColor color = input[2].equalsIgnoreCase("white")
                            ? ChessGame.TeamColor.WHITE
                            : ChessGame.TeamColor.BLACK;

                    if (facade.joinGame(color, game.gameID())) {
                        GamePlayUI gameplay = new GamePlayUI(facade, game, color);

                        ChessGame joinedGame = game.game();
                        joinedGame.getBoard().resetBoard();

                        gameplay.run();
                    } else {
                        System.out.println("Join failed. Color may be taken :/");
                    }
                }
                case "observe" -> {
                    if (games == null || games.isEmpty()) {
                        System.out.println("No games available to observe. Use 'list' first.");
                        break;
                    }

                    String inputStr;
                    if (input.length == 2) {
                        inputStr = input[1];
                    } else {
                        System.out.println("Enter the number of the game you'd like to observe:");
                        Scanner scanner = new Scanner(System.in);
                        inputStr = scanner.nextLine();
                    }

                    if (!inputStr.matches("\\d+")) {
                        System.out.println("Invalid input. Please enter a number.");
                        break;
                    }

                    int gameIndex = Integer.parseInt(inputStr) - 1;
                    List<GameData> gameList = new ArrayList<>(games);

                    if (gameIndex < 0 || gameIndex >= gameList.size()) {
                        System.out.println("Invalid game number.");
                        break;
                    }

                    GameData game = gameList.get(gameIndex);
                    ChessGame observedGame = game.game();
                    observedGame.getBoard().resetBoard();

                    BoardLayout layout = new BoardLayout(observedGame);
                    layout.displayBoard(ChessGame.TeamColor.WHITE, null);
                }

                default -> {
                    System.out.println("Command not recognized.");
                    helpMenu();
                }
            }
        }
        new PreLogin(facade).run();
    }

    private String[] getInput() {
        System.out.print("\n[LOGGED_IN] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    private void helpMenu() {
        System.out.println("create <NAME>             - Create a new game");
        System.out.println("list                      - List available games");
        System.out.println("join <ID> [WHITE|BLACK]   - Join a game as a player");
        System.out.println("logout                    - Log out of your account");
        System.out.println("observe                   - Observe a game");
        System.out.println("help                      - Show available commands");
    }
}
