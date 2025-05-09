package service;

public record CreateGameReq(
        Integer gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        String authToken
) {}