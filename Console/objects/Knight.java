package objects;

import logic.Board;
/** Represents a knight chess piece. */
public class Knight extends Piece {
    public Knight(PieceColor color, int x, int y) {
        super(color, PieceType.KNIGHT, x, y);
    }
    
    /**
     * Determines if a knight can move from (sr, sc) to (er, ec).
     * Knights move in an "L" shape (2 by 1 or 1 by 2) and can jump over other pieces.
     *
     * @param board the current game board
     * @param sr starting row
     * @param sc starting column
     * @param er ending row
     * @param ec ending column
     * @return true if the knight can legally move to the target square
     */
    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        int dr = Math.abs(er - sr);
        int dc = Math.abs(ec - sc);
        if ((dr == 2 && dc == 1) || (dr == 1 && dc == 2)) {
            // knights can jump over other pieces
            return canCaptureOrMove(board, er, ec);
        }
        return false;
    }
}
