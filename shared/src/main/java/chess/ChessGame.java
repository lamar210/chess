package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", teamTurn=" + teamTurn +
                '}';
    }

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {

        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null)
            return null;
        if (piece.getTeamColor() != teamTurn)
            return null;

        Collection<ChessMove> raw = piece.pieceMoves(board, startPosition);

        List<ChessMove> legal = new ArrayList<>();
        for (ChessMove m : raw){
            ChessPiece destBefore = board.getPiece(m.getEndPosition());
            board.addPiece(startPosition, null);
            ChessPiece moved = piece;

            if (m.getPromotionPiece() != null) {
                moved = new ChessPiece(piece.getTeamColor(), m.getPromotionPiece());
            }
            board.addPiece(m.getEndPosition(), moved);

            boolean inCheck = isInCheck(teamTurn);

            board.addPiece(startPosition, piece);
            board.addPiece(m.getEndPosition(), destBefore);
            if (!inCheck){
                legal.add(m);
            }

        }
        return legal;
    }

    private Collection<ChessMove> validMovesForColor(ChessPosition startPosition, TeamColor colorToCheck) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null || piece.getTeamColor() != colorToCheck)
            return new ArrayList<>();

        Collection<ChessMove> raw = piece.pieceMoves(board, startPosition);
        List<ChessMove> legal = new ArrayList<>();

        for (ChessMove m : raw) {
            ChessPiece destBefore = board.getPiece(m.getEndPosition());

            board.addPiece(startPosition, null);

            ChessPiece moved = piece;
            if (m.getPromotionPiece() != null) {
                moved = new ChessPiece(piece.getTeamColor(), m.getPromotionPiece());
            }
            board.addPiece(m.getEndPosition(), moved);

            boolean inCheck = isInCheck(colorToCheck);

            board.addPiece(startPosition, piece);
            board.addPiece(m.getEndPosition(), destBefore);

            if (!inCheck) {
                legal.add(m);
            }
        }
        return legal;
    }

    private boolean noValidMoves(TeamColor colorToCheck) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition pos = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == colorToCheck) {
                    Collection<ChessMove> moves = validMovesForColor(pos, colorToCheck);
                    if (moves != null && !moves.isEmpty()) {
                        return false; // there's at least one legal move!
                    }
                }
            }
        }
        return true;
    }



    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> options = validMoves(move.getStartPosition());

        if (options == null || !options.contains(move)) {
            throw new InvalidMoveException("Illegal move: " + move);
        }

        ChessPiece mover = board.getPiece(move.getStartPosition());
        board.addPiece(move.getStartPosition(), null);
        ChessPiece placed = mover;

        if (move.getPromotionPiece() != null) {
            placed = new ChessPiece(mover.getTeamColor(), move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), placed);

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;



    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition kingP = null;
        boolean foundKing = false;
        for (int r = 1; r <= 8 && !foundKing; r++) {
            for (int c = 1; c <= 8; c++){
                ChessPosition p = new ChessPosition(r, c);
                ChessPiece occupant = board.getPiece(p);

                if (occupant != null && occupant.getTeamColor() == teamColor && occupant.getPieceType() == ChessPiece.PieceType.KING){
                    kingP = p;
                    foundKing = true;
                    break;
                }
            }
        }
        if (kingP == null){
            return false;
        }
        for (int r = 1; r <= 8; r++){
            for (int c = 1; c <= 8; c++){
                ChessPosition from = new ChessPosition(r, c);
                ChessPiece attacker = board.getPiece(from);

                if (attacker != null && attacker.getTeamColor() != teamColor){

                    for (ChessMove mv : attacker.pieceMoves(board, from)) {
                        if (mv.getEndPosition().equals(kingP)) {
                            return true;
                        }
                    }
                }

            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
//        if (!isInCheck(teamColor))
//            return false;
//
//        for (int r = 1; r <= 8; r++){
//            for (int c = 1; c <= 8; c++){
//                ChessPosition pos = new ChessPosition(r,c);
//                ChessPiece p = board.getPiece(pos);
//
//                if (p != null && p.getTeamColor() == teamColor){
//                    Collection<ChessMove> moves = validMoves(pos);
//                    if (moves != null && !moves.isEmpty()){
//                        return false;
//                    }
//                }
//            }
//        }
//        return true;
        return isInCheck(teamColor) && noValidMoves(teamColor);

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        if (teamColor != teamTurn) {
            return false;
        }

        if (isInCheck(teamColor))
            return false;

        for (int r = 1; r <= 8; r++){
            for (int c = 1; c <= 8; c++){
                ChessPosition pos = new ChessPosition(r,c);
                ChessPiece p = board.getPiece(pos);

                if (p != null && p.getTeamColor() == teamColor){
                    Collection<ChessMove> moves = validMovesForColor(pos, teamColor);
                    if (moves != null && !moves.isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {

        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {

        return board;
    }
}
