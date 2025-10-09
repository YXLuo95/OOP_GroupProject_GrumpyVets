package objects;

import logic.Board;
/** Abstract base class for chess pieces. */

public abstract class Piece implements Moveable {
    /*declaration of piece attributes
     * color - color of the piece (white or black)
     * type - type of the piece (pawn, rook, knight, bishop, queen, king)
     * x, y - current position of the piece on the board
     */
    protected PieceColor color;
    protected PieceType type;
    protected int x;  
    protected int y;  

    /** Constructor to initialize a chess piece with its color, type, and position.
     *  @param color The color of the piece (white or black)
     *  @param type  The type of the piece (pawn, rook, knight, bishop, queen, king)
     *  @param x     The initial x-coordinate (row) of the piece
     *  @param y     The initial y-coordinate (column) of the piece
     */
    public Piece(PieceColor color, PieceType type, int x, int y) {
        this.color = color;
        this.type = type;
        this.x = x;
        this.y = y;
    }

    // Getters for piece attributes
    public PieceColor getColor() { return color; }
    public PieceType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }

    // Setters for piece position
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Determines if a piece can move from a starting position to an ending position on the board.
     *
     * @param board the current game board
     * @param startRow starting row
     * @param startCol starting column
     * @param endRow ending row
     * @param endCol ending column
     * @return true if the piece can legally move to the target square
     */
    public abstract boolean canMove(Board board, int startRow, int startCol, int endRow, int endCol);

    /**
     * Checks if the destination square can be captured or occupied.
     * A move is allowed only if the target square is empty or occupied by an opponent.
     *
     * @param board the current board
     * @param er ending row
     * @param ec ending column
     * @return true if the target square is empty or contains an enemy piece
     */
    protected boolean canCaptureOrMove(Board board, int er, int ec) {
        Piece target = board.getPieceAt(er, ec);
        return target == null || target.getColor() != this.color;
    }

    /**
     * Checks if the path is clear for the piece to move.
     *
     * @param board the current game board
     * @param sr    the starting row
     * @param sc    the starting column
     * @param er    the ending row
     * @param ec    the ending column
     * @return true if the path is clear, false otherwise
     */
    protected boolean isPathClear(Board board, int sr, int sc, int er, int ec) {
        int dr = Integer.compare(er, sr);
        int dc = Integer.compare(ec, sc);


        boolean straight = (sr == er) || (sc == ec);
        boolean diagonal = Math.abs(er - sr) == Math.abs(ec - sc);
        if (!straight && !diagonal) return false;

        int r = sr + dr, c = sc + dc;
        while (r != er || c != ec) {
            if (board.getPieceAt(r, c) != null) return false;
            r += dr; c += dc;
        }
        return true;
    }


    /** Returns a string representation of the piece, using uppercase for white and lowercase for black. */
   @Override
    public String toString() {
        String s = type.symbol();
        return (color == PieceColor.WHITE) ? s : s.toLowerCase();
    }
}
