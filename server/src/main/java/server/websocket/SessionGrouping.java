package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

public record SessionGrouping(Session session, ChessGame.TeamColor playerColor) { }
