package objects;

import logic.Board;
/** Represents a king chess piece. */

public class King extends Piece {
    public King(PieceColor color, int x, int y) {
        super(color, PieceType.KING, x, y);
    }
    /** @param board The game board
     *  @param sr   The starting row
     *  @param sc   The starting column
     *  @param er   The ending row
     *  @param ec   The ending column
     *  @return true if the king can move from (sr, sc) to (er, ec), false otherwise
     */
    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        // Kings move one square in any direction
        int dr = Math.abs(er - sr);
        int dc = Math.abs(ec - sc);

        // Ensure the move is within one square
        if (dr <= 1 && dc <= 1) {
            return canCaptureOrMove(board, er, ec);
        }
        return false;
    }

    @Override
    public boolean attacks(Board board, int sr, int sc, int er, int ec) {
        //check boundary    
        if (!inside(er, ec)) return false;

        // Kings attack one square in any direction
        return Math.max(Math.abs(er - sr), Math.abs(ec - sc)) == 1;
    }

}
