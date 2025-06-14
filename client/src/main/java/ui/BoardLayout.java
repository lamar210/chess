package ui;

import chess.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static ui.EscapeSequences.*;

public class BoardLayout {

    private ChessGame game;

    public BoardLayout(ChessGame game) {
        this.game = game;
    }

    public void displayBoard(ChessGame.TeamColor perspective, ChessPosition highlight) {
        boolean isWhiteBottom = perspective == ChessGame.TeamColor.WHITE;

        int[] rows = isWhiteBottom ? new int[]{8,7,6,5,4,3,2,1} : new int[]{1,2,3,4,5,6,7,8};
        int[] cols = isWhiteBottom ? new int[]{1,2,3,4,5,6,7,8} : new int[]{8,7,6,5,4,3,2,1};


        Set<ChessPosition> dest = Collections.emptySet();
        if (highlight != null) {
            dest = game.validMoves(highlight).stream()
                    .map(ChessMove::getEndPosition)
                    .collect(Collectors.toSet());
        }

        String startSpot = "\u001B[48;5;215m";
        String destSpot = "\u001B[48;5;229m";

        String headerWhite = "  a     b     c    d     e     f     g     h";
        String headerBlack = "  h     g     f    e     d     c     b     a";

        System.out.print("\n   ");
        System.out.println(isWhiteBottom ? headerWhite : headerBlack);

        for (int row : rows) {
            System.out.print(" " + row + " ");
            for (int col : cols) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = game.getBoard().getPiece(pos);

                boolean darkSquare = (row + col) % 2 == 0;
                String squareColor = darkSquare ? SET_BG_COLOR_BABY_GREEN : SET_BG_COLOR_WHITE;

                String squareBg;
                if (pos.equals(highlight)) {
                    squareBg = startSpot;
                } else if (dest.contains(pos)){
                    squareBg = destSpot;
                } else {
                    squareBg = squareColor;
                }

                String pieceColor = SET_TEXT_COLOR_BLACK;

                if (piece != null) {
                    pieceColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                            ? SET_TEXT_COLOR_BEIGE + SET_TEXT_BOLD
                            : SET_TEXT_COLOR_DARK_GREEN + SET_TEXT_BOLD;
                }

                System.out.print(squareBg + pieceColor);
                System.out.print(" " + pieceSymbol(piece) + " ");
                System.out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
            }
            System.out.println(" " + row);
        }
        System.out.print("   ");
        System.out.println(isWhiteBottom ? headerWhite : headerBlack);
    }

    private static String pieceSymbol(ChessPiece piece) {
        if (piece == null)
        {
            return EMPTY;
        }

        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        return switch (piece.getPieceType()) {
            case KING -> isWhite ? WHITE_KING : BLACK_KING;
            case QUEEN -> isWhite ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK -> isWhite ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP -> isWhite ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> isWhite ? WHITE_PAWN : BLACK_PAWN;
        };
    }

    public void updateBoard(ChessBoard newBoard) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = newBoard.getPiece(pos);
                this.game.getBoard().addPiece(pos, piece);
            }
        }
    }

}
