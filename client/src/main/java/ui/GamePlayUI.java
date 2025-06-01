package ui;

import client.ServerFacade;
import client.ServerMessageObserver;
import client.WebSocketCommunicator;
import websocket.messages.ServerMessage;

public class GamePlayUI implements ServerMessageObserver {

    private final ServerFacade serverFacade;
    private final String authToken;
    private final int gameID;

    public GamePlayUI(ServerFacade serverFacade, String authToken, int gameID) {
        this.serverFacade = serverFacade;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public void run() {

        try {
            ws = new WebSocketCommunicator(this, authToken, gameID);
        } catch (Exception ex) {
            System.out.print("Couldn't connect to game: " + ex.getMessage());
            return;
        }
        System.out.print("Connected to game successfully! Type 'help' too see gameplay commands");
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                System.out.print("[LOAD_GAME] Redraw board");
            }
            case NOTIFICATION -> {
                System.out.print("[NOTIFICATION]");
            }
            case ERROR -> {
                System.out.print("[ERROR]");
            }
        }
    }
}
