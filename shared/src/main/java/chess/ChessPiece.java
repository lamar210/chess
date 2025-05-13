package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import static chess.ChessPiece.PieceType.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private boolean onBoard(int r, int c) {
        return r >= 1 && r <= 8 && c >= 1 && c <= 8;
    }

    private void addSlidingMoves(ChessBoard board, ChessPosition startPos, List<ChessMove> moves, int[][] directions) {
        for (int[] dir : directions) {
            int row = startPos.getRow() + dir[0];
            int col = startPos.getColumn() + dir[1];

            while (onBoard(row, col)) {
                ChessPosition nextPos = new ChessPosition(row, col);
                ChessPiece occupant = board.getPiece(nextPos);

                if (occupant == null) {
                    moves.add(new ChessMove(startPos, nextPos, null));
                } else {
                    if (occupant.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(startPos, nextPos, null));
                    }
                    break;
                }

                row += dir[0];
                col += dir[1];
            }
        }
    }

    private void tryAddMove(ChessBoard board, ChessPosition from, int row, int col, List<ChessMove> moves) {
        if (onBoard(row, col)) {
            ChessPosition dest = new ChessPosition(row, col);
            ChessPiece occupant = board.getPiece(dest);
            if (occupant == null || occupant.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(from, dest, null));
            }
        }
    }

    private void generateRookMoves(ChessBoard board, ChessPosition pos, List<ChessMove> moves) {
        int[][] dirs = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        addSlidingMoves(board, pos, moves, dirs);
    }

    private void generateBishopMoves(ChessBoard board, ChessPosition pos, List<ChessMove> moves) {
        int[][] dirs = {{1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
        addSlidingMoves(board, pos, moves, dirs);
    }

    private void generateQueenMoves(ChessBoard board, ChessPosition pos, List<ChessMove> moves) {
        int[][] dirs = {
                {1, 0}, {0, 1}, {-1, 0}, {0, -1},
                {1, 1}, {-1, -1}, {-1, 1}, {1, -1}
        };
        addSlidingMoves(board, pos, moves, dirs);
    }

    private void generateKingMoves(ChessBoard board, ChessPosition pos, List<ChessMove> moves) {
        int[][] offsets = {
                {0, 1}, {0, -1}, {1, 0}, {-1, 0},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] off : offsets) {
            tryAddMove(board, pos, pos.getRow() + off[0], pos.getColumn() + off[1], moves);
        }
    }


    private void generateKnightMoves(ChessBoard board, ChessPosition pos, List<ChessMove> moves) {
        int[][] offsets = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] off : offsets) {
            tryAddMove(board, pos, pos.getRow() + off[0], pos.getColumn() + off[1], moves);
        }
    }


    private void generatePawnMoves(ChessBoard board, ChessPosition pos, List<ChessMove> moves) {
        int forward = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int r = pos.getRow(), c = pos.getColumn();
        int r1 = r + forward;

        ChessPosition oneAhead = new ChessPosition(r1, c);
        if (onBoard(r1, c) && board.getPiece(oneAhead) == null) {
            if (r1 == promotionRow) {
                for (PieceType promo : new PieceType[]{KNIGHT, QUEEN, BISHOP, ROOK}) {
                    moves.add(new ChessMove(pos, oneAhead, promo));
                }
            } else {
                moves.add(new ChessMove(pos, oneAhead, null));
                if (r == startRow) {
                    int r2 = r + 2 * forward;
                    ChessPosition twoAhead = new ChessPosition(r2, c);
                    ChessPosition between = new ChessPosition(r + forward, c);
                    if (onBoard(r2, c) && board.getPiece(twoAhead) == null && board.getPiece(between) == null) {
                        moves.add(new ChessMove(pos, twoAhead, null));
                    }
                }
            }
        }

        for (int dc : new int[]{-1, 1}) {
            int nc = c + dc, nr = r + forward;
            if (!onBoard(nr, nc)) {
                continue;
            }

            ChessPosition target = new ChessPosition(nr, nc);
            ChessPiece victim = board.getPiece(target);

            if (victim != null && victim.getTeamColor() != pieceColor) {
                if (nr == promotionRow) {
                    for (PieceType promo : new PieceType[]{KNIGHT, QUEEN, BISHOP, ROOK}) {
                        moves.add(new ChessMove(pos, target, promo));
                    }
                } else {
                    moves.add(new ChessMove(pos, target, null));
                }
            }
        }
    }


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();

        switch (type) {
            case ROOK -> generateRookMoves(board, myPosition, moves);
            case BISHOP -> generateBishopMoves(board, myPosition, moves);
            case QUEEN -> generateQueenMoves(board, myPosition, moves);
            case KING -> generateKingMoves(board, myPosition, moves);
            case KNIGHT -> generateKnightMoves(board, myPosition, moves);
            case PAWN -> generatePawnMoves(board, myPosition, moves);
        }

        return moves;
    }
}