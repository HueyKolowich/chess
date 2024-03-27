package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessUI {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 3;
    private static final String[] horizontalHeadersOrientation1 = {" ", "h", "g", "f", "e", "d", "c", "b", "a", " "};
    private static final String[] horizontalHeadersOrientation2 = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
    private static final String[] verticalHeadersOrientation1 = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private static final String[] verticalHeadersOrientation2 = {"8", "7", "6", "5", "4", "3", "2", "1"};
    private static final String EMPTY = " ";

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out, horizontalHeadersOrientation1);
        drawChessBoard(out, verticalHeadersOrientation1);
        drawHeaders(out, horizontalHeadersOrientation1);

        out.println();

        drawHeaders(out, horizontalHeadersOrientation2);
        drawChessBoard(out, verticalHeadersOrientation2);
        drawHeaders(out, horizontalHeadersOrientation2);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
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

    private static void drawChessBoard(PrintStream out, String[] verticalHeaders) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            drawRowOfSquares(out, boardRow, verticalHeaders[boardRow]);
        }
    }

    private static void drawRowOfSquares(PrintStream out, int alternation, String verticalHeader) {
        for (int squareRow = 0; squareRow < 1; ++squareRow) {
            printVerticalHeaderText(out, verticalHeader);

            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if (alternation % 2 == 0) {
                    setWhite(out);
                } else {
                    setBlack(out);
                }
                alternation++;

                out.print(EMPTY.repeat(SQUARE_SIZE_IN_CHARS));

                setBlack(out);
            }

            printVerticalHeaderText(out, verticalHeader);

            setBlack(out);
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}