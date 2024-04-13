package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;

import static ui.EscapeSequences.*;

public class ChessUI {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 3;
    private static final String[] horizontalHeadersOrientation2 = {" ", "h", "g", "f", "e", "d", "c", "b", "a", " "};
    private static final String[] horizontalHeadersOrientation1 = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
    private static final String[] verticalHeadersOrientation2 = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private static final String[] verticalHeadersOrientation1 = {"8", "7", "6", "5", "4", "3", "2", "1"};
    private static final String EMPTY = " ";

    public static void draw(ChessBoard board, ChessGame.TeamColor playerColor, Collection<ChessMove> highlightedMoves) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        String[] horizontalOrientation;
        String[] verticalOrientation;
        int orientationNumber;
        if (playerColor == null || playerColor.equals(ChessGame.TeamColor.WHITE)) {
            horizontalOrientation = horizontalHeadersOrientation1;
            verticalOrientation = verticalHeadersOrientation1;
            orientationNumber = 1;
        } else {
            horizontalOrientation = horizontalHeadersOrientation2;
            verticalOrientation = verticalHeadersOrientation2;
            orientationNumber = 2;
        }

        out.print(ERASE_SCREEN);

        out.println();

        drawHeaders(out, horizontalOrientation);
        drawChessBoard(out, verticalOrientation, orientationNumber, board, highlightedMoves);
        drawHeaders(out, horizontalOrientation);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

        out.print("[COMMAND] >>> ");
    }

    private static void drawHeaders(PrintStream out, String[] headers) {
        setBlack(out);

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES + 2; ++boardCol) {
            drawHeader(out, headers[boardCol]);

        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_CHARS - prefixLength - 1;

        out.print(SET_BG_COLOR_DARK_GREY);

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));

        setBlack(out);
    }

    private static void printHeaderText(PrintStream out, String header) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);

        out.print(header);
    }

    private static void printVerticalHeaderText(PrintStream out, String verticalHeader) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(EMPTY);
        out.print(verticalHeader);
        out.print(EMPTY);
    }

    private static void drawChessBoard(PrintStream out, String[] verticalHeaders, int orientationNumber, ChessBoard board, Collection<ChessMove> highlightedMoves) {
        int alternation;
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            printVerticalHeaderText(out, verticalHeaders[boardRow]);

            alternation = boardRow;
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if (alternation % 2 == 0) {
                    setWhite(out);
                } else {
                    setBlack(out);
                }
                alternation++;

                if (highlightedMoves != null) {
                    for (ChessMove move : highlightedMoves) {
                        if ((orientationNumber == 1) && (move.getEndPosition().getRow() == reversedRowNumberingSeriesConversion.get(boardRow)) && (move.getEndPosition().getColumn() == reversedColumnNumberingSeriesConversion.get(boardCol))) {
                            if (alternation % 2 == 0) {
                                setDarkGreen(out);
                            } else {
                                setGreen(out);
                            }
                        } else if ((orientationNumber == 2) && (move.getEndPosition().getRow() == reversedRowNumberingSeriesConversion.get(7 - boardRow)) && (move.getEndPosition().getColumn() == reversedColumnNumberingSeriesConversion.get(7 - boardCol))) {
                            if (alternation % 2 == 0) {
                                setDarkGreen(out);
                            } else {
                                setGreen(out);
                            }
                        }
                    }
                }
                out.print(EMPTY);
                checkSquareForPiece(out, boardRow, boardCol, orientationNumber, board);
                out.print(EMPTY);

                setBlack(out);
            }

            printVerticalHeaderText(out, verticalHeaders[boardRow]);

            setBlack(out);
            out.println();
        }
    }

    private static void checkSquareForPiece(PrintStream out, int row, int column, int orientationNumber, ChessBoard board) {
        ChessPosition currentPosition;
        if (orientationNumber == 1) {
            currentPosition = new ChessPosition(reversedRowNumberingSeriesConversion.get(row), reversedColumnNumberingSeriesConversion.get(column));
        } else {
            currentPosition = new ChessPosition(reversedRowNumberingSeriesConversion.get(7 - row), reversedColumnNumberingSeriesConversion.get(7 - column));
        }

        if (board.isPiece(currentPosition)) {
            if (board.getPiece(currentPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                out.print(SET_TEXT_COLOR_RED);
            } else {
                out.print(SET_TEXT_COLOR_BLUE);
            }

            out.print(pieceToRepresentation.get(board.getPiece(currentPosition).getPieceType()));
        } else {
            out.print(EMPTY);
        }

        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_GREEN);
    }

    private static void setDarkGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_GREEN);
    }

    private static final HashMap<Integer, Integer> reversedRowNumberingSeriesConversion = new HashMap<>() {{
        put(7, 1);
        put(6, 2);
        put(5, 3);
        put(4, 4);
        put(3, 5);
        put(2, 6);
        put(1, 7);
        put(0, 8);
    }};

    private static final HashMap<Integer, Integer> reversedColumnNumberingSeriesConversion = new HashMap<>() {{
        put(0, 1);
        put(1, 2);
        put(2, 3);
        put(3, 4);
        put(4, 5);
        put(5, 6);
        put(6, 7);
        put(7, 8);
    }};

    private static final HashMap<ChessPiece.PieceType, String> pieceToRepresentation = new HashMap<>() {{
        put(ChessPiece.PieceType.KING, "K");
        put(ChessPiece.PieceType.QUEEN, "Q");
        put(ChessPiece.PieceType.BISHOP, "B");
        put(ChessPiece.PieceType.KNIGHT, "N");
        put(ChessPiece.PieceType.ROOK, "R");
        put(ChessPiece.PieceType.PAWN, "P");
    }};
}