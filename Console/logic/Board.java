package logic;

import objects.*;

/**
 * Board class representing an 8x8 chess board with pieces.
 * Provides methods to reset, move pieces, and print the board.
 * Includes deep copy functionality for undo/redo features. 
 * Supports standard chess starting position.
 */
public class Board {

    // create an 8x8 array to hold the pieces
    private final Piece[][] squares = new Piece[8][8];

    /** Constructor: Create the standard starting position */
    public Board() {
        resetToStandard();
    }

    /**
     * Reset the board to the standard starting position (Black on top, White on bottom)
     */
    public final void resetToStandard() {
        // Clear all squares
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squares[r][c] = null;
            }
        }

        // Place pieces for Black (top)
        squares[0][0] = new Rook  (PieceColor.BLACK, 0, 0);
        squares[0][1] = new Knight(PieceColor.BLACK, 0, 1);
        squares[0][2] = new Bishop(PieceColor.BLACK, 0, 2);
        squares[0][3] = new Queen (PieceColor.BLACK, 0, 3);
        squares[0][4] = new King  (PieceColor.BLACK, 0, 4);
        squares[0][5] = new Bishop(PieceColor.BLACK, 0, 5);
        squares[0][6] = new Knight(PieceColor.BLACK, 0, 6);
        squares[0][7] = new Rook  (PieceColor.BLACK, 0, 7);

        for (int c = 0; c < 8; c++) {
            squares[1][c] = new Pawn(PieceColor.BLACK, 1, c);
        }

        // Place pieces for White (bottom)
        for (int c = 0; c < 8; c++) {
            squares[6][c] = new Pawn(PieceColor.WHITE, 6, c);
        }

        squares[7][0] = new Rook  (PieceColor.WHITE, 7, 0);
        squares[7][1] = new Knight(PieceColor.WHITE, 7, 1);
        squares[7][2] = new Bishop(PieceColor.WHITE, 7, 2);
        squares[7][3] = new Queen (PieceColor.WHITE, 7, 3);
        squares[7][4] = new King  (PieceColor.WHITE, 7, 4);
        squares[7][5] = new Bishop(PieceColor.WHITE, 7, 5);
        squares[7][6] = new Knight(PieceColor.WHITE, 7, 6);
        squares[7][7] = new Rook  (PieceColor.WHITE, 7, 7);
    }

    /* ---------- Auxiliary Methods ---------- */

    /** Check if the coordinates are within the 8x8 bounds */
    private boolean inBounds(int r, int c) {
        return 0 <= r && r < 8 && 0 <= c && c < 8;
    }

    /** Get the piece at a specific square */
    public Piece getPieceAt(int r, int c) {
        return inBounds(r, c) ? squares[r][c] : null;
    }

    /** Set the piece at a specific square (and update the piece's coordinates) */
    public void setPieceAt(int r, int c, Piece p) {
        if (!inBounds(r, c)) return;
        squares[r][c] = p;
        if (p != null) p.setPosition(r, c); // 🔥 Sync coordinates
    }

    /** Clear a specific square */
    public void clearSquare(int r, int c) {
        if (inBounds(r, c)) squares[r][c] = null;
    }

    /**
     * Executes a move (does not check legality)
     * Syncs the piece's coordinates and returns the captured piece (if any)
     */
    public Piece movePiece(int sr, int sc, int er, int ec) {
        if (!inBounds(sr, sc) || !inBounds(er, ec)) return null;

        Piece moving = squares[sr][sc];
        Piece captured = squares[er][ec];

        squares[er][ec] = moving;
        squares[sr][sc] = null;

        if (moving != null) moving.setPosition(er, ec); //  Sync coordinates

        return captured;
    }

    /* ---------- Print the Board ---------- */

    /** Print the board (rank 8 at the top, 1 at the bottom, files a..h) */
    public void printBoard() {
        System.out.println("   a b c d e f g h");
        for (int r = 0; r < 8; r++) {
            int rank = 8 - r;
            System.out.print(rank + "  ");
            for (int c = 0; c < 8; c++) {
                Piece p = squares[r][c];
                System.out.print((p == null ? ". " : p.toString() + " "));
            }
            System.out.println(" " + rank);
        }
        System.out.println("   a b c d e f g h");
    }

    /* ---------- Coordinate Utilities ---------- */

    /** Converts "e2" to array indices (6,4) (White at the bottom) */
    /*  since array index is [row][col] but chess notation is [file][rank]
    /*  e.g., 'a'->0, 'b'->1, ..., 'h'->7; '1'->7, '2'->6, ..., '8'->0
    */
    public static int[] fromAlg(String alg) {
        if (alg == null || alg.length() != 2) return null;
        char file = Character.toLowerCase(alg.charAt(0)); // a..h
        char rank = alg.charAt(1);                        // 1..8
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') return null;
        int col = file - 'a';
        int row = 8 - (rank - '0');
        return new int[]{row, col};
    }

    /** returns (row,col) as "e2" (White at the bottom) */
    public static String toAlg(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) return "??";
        char file = (char) ('a' + col);
        char rank = (char) ('0' + (8 - row));
        return "" + file + rank;
    }



    /* ---------- Deep Copy & Restore for Undo/Redo ---------- */
    /** Deep copies the entire board, duplicating all pieces (independent new objects) */
    public Board deepCopy() {
        Board copy = new Board();
        // Clear the default starting position
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                copy.setPieceAt(r, c, null);
            }
        }

        // Copy the current pieces
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = this.getPieceAt(r, c);
                if (p != null) {
                    copy.setPieceAt(r, c, clonePiece(p));
                }
            }
        }
        return copy;
    }

    /** Copies the board state from src to this board (references remain unchanged) */
    public void copyFrom(Board src) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = src.getPieceAt(r, c);
                this.setPieceAt(r, c, (p == null) ? null : clonePiece(p));
            }
        }
    }

    /** Helper to clone a piece (creates a new instance of the same type and color) */
    private static Piece clonePiece(Piece p) {
        return switch (p.getType()) {
            case PAWN   -> new Pawn  (p.getColor(), p.getX(), p.getY());
            case ROOK   -> new Rook  (p.getColor(), p.getX(), p.getY());
            case KNIGHT -> new Knight(p.getColor(), p.getX(), p.getY());
            case BISHOP -> new Bishop(p.getColor(), p.getX(), p.getY());
            case QUEEN  -> new Queen (p.getColor(), p.getX(), p.getY());
            case KING   -> new King  (p.getColor(), p.getX(), p.getY());
        };
    }

}
