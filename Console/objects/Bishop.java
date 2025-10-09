package objects;

import logic.Board;
/** Represents a bishop chess piece.    */

public class Bishop extends Piece {
    public Bishop(PieceColor color, int x, int y) {
        super(color, PieceType.BISHOP, x, y);
    }

    /** @param board The game board
     *  @param sr   The starting row
     *  @param sc   The starting column
     *  @param er   The ending row
     *  @param ec   The ending column
     *  @return true if the bishop can move from (sr, sc) to (er, ec), false otherwise
     */
    
    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        // bishops move any number of squares diagonally
        if (Math.abs(er - sr) != Math.abs(ec - sc)) return false;

        // path must be clear (not including start and end)
        if (!isPathClear(board, sr, sc, er, ec)) return false;

        // destination must be empty or contain an opponent's piece
        return canCaptureOrMove(board, er, ec);
    }

        
    
}
