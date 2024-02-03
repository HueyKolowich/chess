package chess;

import java.util.*;

public interface ChessPieceCalculator {
    List<int[]> possibleEndPositionCalculator(ChessBoard board, ChessPosition position, ChessGame.TeamColor pieceColor);
}