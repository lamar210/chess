package service;

public record JoinGameReq(
        Integer     gameID,
        Color       playerColor,
        String      authToken
) {
    public enum Color {
        WHITE,
        BLACK
    }
}
