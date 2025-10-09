package objects;

import logic.Board;
/** Represents a queen chess piece. */

public class Queen extends Piece {
    public Queen(PieceColor color, int x, int y) {
        super(color, PieceType.QUEEN, x, y);
    }
    /** 
     *  @param board The game board
     *  @param sr   The starting row
     *  @param sc   The starting column
     *  @param er   The ending row
     *  @param ec   The ending column
     *  @return true if the queen can move from (sr, sc) to (er, ec), false otherwise
     */
    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        // Queens move any number of squares along a rank, file, or diagonal

        // check move type
        boolean straight = (sr == er || sc == ec);
        boolean diagonal = Math.abs(er - sr) == Math.abs(ec - sc);
        if (!straight && !diagonal) return false;

        // check path is clear
        if (!isPathClear(board, sr, sc, er, ec)) return false;
        // check destination square
        return canCaptureOrMove(board, er, ec);
    }
}
