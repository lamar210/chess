package ui;

import chess.*;

import static ui.EscapeSequences.*;

public class BoardLayout {

    private final ChessGame game;

    public BoardLayout(ChessGame game) {
        this.game = game;
    }

    public void displayBoard(ChessGame.TeamColor perspective) {
        boolean isWhiteBottom = perspective == ChessGame.TeamColor.WHITE;

        int[] rows = isWhiteBottom ? new int[]{8,7,6,5,4,3,2,1} : new int[]{1,2,3,4,5,6,7,8};
        int[] cols = isWhiteBottom ? new int[]{1,2,3,4,5,6,7,8} : new int[]{8,7,6,5,4,3,2,1};

        System.out.print("\n   ");
        for (int col : cols) {
            System.out.print(" " + colToLetter(col) + " ");
        }
        System.out.println();

        for (int row : rows) {
            System.out.print(" " + row + " ");
            for (int col : cols) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = game.getBoard().getPiece(pos);

                boolean darkSquare = (row + col) % 2 == 0;
                String squareColor = darkSquare ? SET_BG_COLOR_BABY_GREEN : SET_BG_COLOR_WHITE;
                String pieceColor = SET_TEXT_COLOR_BLACK;

                if (piece != null) {
                    pieceColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                            ? SET_TEXT_COLOR_BEIGE + SET_TEXT_BOLD
                            : SET_TEXT_COLOR_DARK_GREEN;
                }

                System.out.print(squareColor + pieceColor);
                System.out.print(" " + pieceSymbol(piece) + " ");
                System.out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
            }
            System.out.println(" " + row);
        }

        System.out.print("   ");
        for (int col : cols) {
            System.out.print(" " + colToLetter(col) + " ");
        }
        System.out.println();
    }

    private String pieceSymbol(ChessPiece piece) {
        if (piece == null) return EMPTY;

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

    private String colToLetter(int col) {
        return String.valueOf((char) ('a' + col - 1));
    }
}
