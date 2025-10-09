package objects;

import logic.Board;

/**
 * Represents a pawn chess piece.
 * Pawns move forward but capture diagonally.
 * This class uses canCaptureOrMove() from the Piece base class to check target squares.
 */
public class Pawn extends Piece {

    public Pawn(PieceColor color, int x, int y) {
        super(color, PieceType.PAWN, x, y);
    }

    /**
     * Determines if the pawn can move from (sr, sc) to (er, ec).
     * 
     * @param board the current board
     * @param sr starting row
     * @param sc starting column
     * @param er ending row
     * @param ec ending column
     * @return true if the pawn can legally move to (er, ec)
     */
    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        int direction = (this.color == PieceColor.WHITE) ? -1 : 1;  // white moves up, black moves down
        int dr = er - sr;
        int dc = Math.abs(ec - sc);

        Piece target = board.getPieceAt(er, ec);

        // --- Case 1: Forward move (no capture) ---
        if (dc == 0) {
            // must move exactly one forward
            if (dr == direction && target == null) {
                return true;
            }

            // first-move double advance (2 steps)
            int startRow = (this.color == PieceColor.WHITE) ? 6 : 1;
            if (sr == startRow && dr == 2 * direction) {
                // both squares ahead must be empty
                int midRow = sr + direction;
                if (board.getPieceAt(midRow, sc) == null && target == null) {
                    return true;
                }
            }
            return false;
        }

        // --- Case 2: Diagonal capture ---
        if (dc == 1 && dr == direction) {
            // ✅ reuse the helper from Piece
            return canCaptureOrMove(board, er, ec) && target != null;
        }

        // --- Otherwise invalid ---
        return false;
    }
}
