package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();

        if (type == PieceType.KNIGHT) {
            int[][] knightOffsets = {
                    {2, 1},
                    {2, -1},
                    {-2, 1},
                    {-2, -1},
                    {1, 2},
                    {1, -2},
                    {-1, 2},
                    {-1, -2}
            };
            for (int[] off : knightOffsets) {

                int nextRow = myPosition.getRow() + off[0];
                int nextCol = myPosition.getColumn() + off[1];

                if (!onBoard(nextRow, nextCol)) continue;

                ChessPiece occupant = board.getPiece(new ChessPosition(nextRow, nextCol));
                if (occupant == null || occupant.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                }
            }
        }
        if (type == PieceType.QUEEN) {
            int[][] queenOffsets = {
                    {0, 1},
                    {0, -1},
                    {1, 0},
                    {-1, 0},
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1}
            };
            for (int[] off : queenOffsets) {
                int nextRow = myPosition.getRow() + off[0];
                int nextCol = myPosition.getColumn() + off[1];

                while (onBoard(nextRow, nextCol)) {
                    ChessPiece occupant = board.getPiece(new ChessPosition(nextRow, nextCol));

                    if (occupant == null) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                    } else if (occupant.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                        break;
                    } else {
                        break;
                    }
                    nextRow += off[0];
                    nextCol += off[1];
                }
            }
        }
        if (type == PieceType.KING) {
            int[][] kingOffsets = {
                    {0, 1},
                    {0, -1},
                    {1, 0},
                    {-1, 0},
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1}
            };
            for (int[] off : kingOffsets) {
                int nextRow = myPosition.getRow() + off[0];
                int nextCol = myPosition.getColumn() + off[1];

                if (!onBoard(nextRow, nextCol)) continue;
                ChessPiece occupant = board.getPiece(new ChessPosition(nextRow, nextCol));
                if (occupant == null || occupant.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                }
            }
        }
        if (type == PieceType.BISHOP) {
            int[][] bishopOffsets = {
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1},

            };
            for (int[] off : bishopOffsets) {
                int nextRow = myPosition.getRow() + off[0];
                int nextCol = myPosition.getColumn() + off[1];

                while (onBoard(nextRow, nextCol)) {
                    ChessPiece occupant = board.getPiece(new ChessPosition(nextRow, nextCol));

                    if (occupant == null) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                    } else if (occupant.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                        break;
                    } else {
                        break;
                    }
                    nextRow += off[0];
                    nextCol += off[1];
                }
            }
        }
        if (type == PieceType.ROOK) {
            int[][] rookOffsets = {
                    {1, 0},
                    {0, -1},
                    {-1, 0},
                    {0, 1},

            };
            for (int[] off : rookOffsets) {
                int nextRow = myPosition.getRow() + off[0];
                int nextCol = myPosition.getColumn() + off[1];

                while (onBoard(nextRow, nextCol)) {
                    ChessPiece occupant = board.getPiece(new ChessPosition(nextRow, nextCol));

                    if (occupant == null) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                    } else if (occupant.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(nextRow, nextCol), null));
                        break;
                    } else {
                        break;
                    }
                    nextRow += off[0];
                    nextCol += off[1];

                }
            }
        }
        if (type == PieceType.PAWN) {
            int forward = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
            int[][] pawnOffsets = {
                    {forward, 0},
                    {forward, -1},
                    {forward, 1},
            };
            for (int step = 1; step <= 2; step++) {
                int nextRow = myPosition.getRow() + forward * step;
                int newCol = myPosition.getColumn();

                if (step == 2 && myPosition.getRow() != (pieceColor == ChessGame.TeamColor.WHITE ? 2 : 7))
                    break;

                if (!onBoard(nextRow, newCol)) break;

            }
        }
        return moves;
    }
}


