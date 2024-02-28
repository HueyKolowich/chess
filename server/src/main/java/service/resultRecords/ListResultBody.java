package service.resultRecords;

import chess.ChessGame;

public record ListResultBody(int gameID, String whiteUsername, String blackUsername, String gameName) {
}
