package objects;

import logic.Board;

/** Represents a rook chess piece. */

public class Rook extends Piece {
    public Rook(PieceColor color, int x, int y) {
        super(color, PieceType.ROOK, x, y);
    }

    /** @param board The game board
     *  @param sr   The starting row
     *  @param sc   The starting column
     *  @param er   The ending row
     *  @param ec   The ending column
     *  @return true if the rook can move from (sr, sc) to (er, ec), false otherwise
     */
    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        
        // Rooks move any number of squares along a rank or file
        if (sr != er && sc != ec) return false; 
        // Check path is clear
        if (!isPathClear(board, sr, sc, er, ec)) return false;
        // Check destination square
        return canCaptureOrMove(board, er, ec);
    }
}
