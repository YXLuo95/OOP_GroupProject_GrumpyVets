package logic;

import objects.Piece;
import objects.PieceColor;
import objects.PieceType;

public class MoveLogic {

    /**
     * Validate a move on the current board for the given side to move.
     * Coordinates are 0-based: row 0 is the top, row 7 is the bottom.
     * White starts on rows 0–1 and moves "down" (row +1).
     * Black starts on rows 6–7 and moves "up" (row -1).
     */
    public static boolean isValidMove(Board board, int sr, int sc, int er, int ec, PieceColor sideToMove) {
        if (!inBounds(sr, sc) || !inBounds(er, ec)) return false;
        if (sr == er && sc == ec) return false;

        Piece from = board.getPieceAt(sr, sc);
        if (from == null) return false;
        if (from.getColor() != sideToMove) return false;

        Piece to = board.getPieceAt(er, ec);
        if (to != null && to.getColor() == from.getColor()) return false; // can’t capture own piece

        PieceType type = from.getType();
        switch (type) {
            case PAWN:   return pawnValid(board, from.getColor(), sr, sc, er, ec);
            case ROOK:   return rookValid(board, sr, sc, er, ec);
            case BISHOP: return bishopValid(board, sr, sc, er, ec);
            case QUEEN:  return queenValid(board, sr, sc, er, ec);
            case KNIGHT: return knightValid(sr, sc, er, ec);
            case KING:   return kingValid(sr, sc, er, ec);
            default:     return false;
        }
    }

    private static boolean pawnValid(Board board, PieceColor color, int sr, int sc, int er, int ec) {
        int dir = (color == PieceColor.WHITE) ? +1 : -1; // white moves down the board
        int startRow = (color == PieceColor.WHITE) ? 1 : 6;

        int dr = er - sr;
        int dc = ec - sc;

        Piece dest = board.getPieceAt(er, ec);

        // forward move by 1: same column, empty destination
        if (dc == 0 && dr == dir && dest == null) return true;

        // forward move by 2 from starting rank: path must be clear
        if (dc == 0 && dr == 2 * dir && sr == startRow && dest == null) {
            int midRow = sr + dir;
            if (board.getPieceAt(midRow, sc) == null) return true;
        }

        // capture: one step diagonally forward, must capture opponent
        if (Math.abs(dc) == 1 && dr == dir && dest != null && dest.getColor() != color) return true;

        // (No en passant implemented yet)
        return false;
    }

    private static boolean rookValid(Board board, int sr, int sc, int er, int ec) {
        if (sr != er && sc != ec) return false;
        return pathClear(board, sr, sc, er, ec);
    }

    private static boolean bishopValid(Board board, int sr, int sc, int er, int ec) {
        if (Math.abs(er - sr) != Math.abs(ec - sc)) return false;
        return pathClear(board, sr, sc, er, ec);
    }

    private static boolean queenValid(Board board, int sr, int sc, int er, int ec) {
        boolean straight = (sr == er || sc == ec);
        boolean diagonal = (Math.abs(er - sr) == Math.abs(ec - sc));
        if (!straight && !diagonal) return false;
        return pathClear(board, sr, sc, er, ec);
    }

    private static boolean knightValid(int sr, int sc, int er, int ec) {
        int dr = Math.abs(er - sr);
        int dc = Math.abs(ec - sc);
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }

    private static boolean kingValid(int sr, int sc, int er, int ec) {
        int dr = Math.abs(er - sr);
        int dc = Math.abs(ec - sc);
        // (Castling not implemented yet)
        return (dr <= 1 && dc <= 1);
    }

    /**
     * Ensure all squares strictly between (sr,sc) -> (er,ec) are empty.
     * Works for straight and diagonal lines.
     */
    private static boolean pathClear(Board board, int sr, int sc, int er, int ec) {
        int stepR = Integer.compare(er, sr);
        int stepC = Integer.compare(ec, sc);

        int r = sr + stepR;
        int c = sc + stepC;

        while (r != er || c != ec) {
            if (board.getPieceAt(r, c) != null) return false;
            r += stepR;
            c += stepC;
        }
        return true;
    }

    private static boolean inBounds(int r, int c) {
        return 0 <= r && r < 8 && 0 <= c && c < 8;
    }
}
