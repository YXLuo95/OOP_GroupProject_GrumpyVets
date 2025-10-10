package objects;

import logic.Board;

/** Abstract base class for chess pieces. */
public abstract class Piece implements Moveable {
    /* Piece attributes:
     * color - color of the piece (white or black)
     * type  - type of the piece (pawn, rook, knight, bishop, queen, king)
     * x, y  - current position (row, col)
     */
    protected PieceColor color;
    protected PieceType type;
    protected int x;
    protected int y;

    /** Initialize a chess piece with its color, type, and position. */
    public Piece(PieceColor color, PieceType type, int x, int y) {
        this.color = color;
        this.type  = type;
        this.x     = x;
        this.y     = y;
    }

    // Getters
    public PieceColor getColor() { return color; }
    public PieceType  getType()  { return type; }
    public int        getX()     { return x; }
    public int        getY()     { return y; }

    // Position setter
    public void setPosition(int x, int y) { this.x = x; this.y = y; }

    /**
     * Determines if a piece can move from (startRow,startCol) to (endRow,endCol).
     */
    @Override
    public abstract boolean canMove(Board board, int startRow, int startCol, int endRow, int endCol);
    
    /**
     * Target square is allowed iff it's on-board and empty or occupied by opponent.
     * NOTE: Always call this after your own inside(...) check for (endRow,endCol),
     *       or rely on this method's inside check.
     */
    protected boolean canCaptureOrMove(Board board, int er, int ec) {
        if (!inside(er, ec)) return false; // ✅ prevent off-board treated as empty
        Piece target = board.getPieceAt(er, ec);
        return target == null || target.getColor() != this.color;
    }

    /**
     * Path clearance helper for sliding pieces (rook/bishop/queen).
     * Returns true if every intermediate square is empty AND the geometry matches
     * straight (rank/file) or diagonal movement.
     */
    protected boolean isPathClear(Board board, int sr, int sc, int er, int ec) {
        // ✅ basic guards
        if (!inside(sr, sc) || !inside(er, ec)) return false;
        if (sr == er && sc == ec) return false; // zero-displacement is not a move

        int dr = Integer.compare(er, sr); // -1,0,1
        int dc = Integer.compare(ec, sc); // -1,0,1

        boolean straight = (sr == er) || (sc == ec);
        boolean diagonal = Math.abs(er - sr) == Math.abs(ec - sc);
        if (!straight && !diagonal) return false;

        int r = sr + dr, c = sc + dc;
        while (r != er || c != ec) {
            if (!inside(r, c)) return false;            // ✅ safety for completeness
            if (board.getPieceAt(r, c) != null) return false;
            r += dr; c += dc;
        }
        return true;
    }

    /** String representation: uppercase for white, lowercase for black. */
    @Override
    public String toString() {
        String s = type.symbol(); // ensure PieceType has symbol(), or switch if not
        return (color == PieceColor.WHITE) ? s : s.toLowerCase();
    }

    /**
     * Attacks predicate used by check/checkmate detection.
     * Default: same as canMove (good for rook/bishop/queen/knight).
     * Pawn and King should override: pawn's attack ≠ move; king's attack excludes castling.
     */
    public boolean attacks(Board board, int sr, int sc, int er, int ec) {
        return canMove(board, sr, sc, er, ec);
    }

    /** Board boundary check (0..7). */
    protected boolean inside(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}
