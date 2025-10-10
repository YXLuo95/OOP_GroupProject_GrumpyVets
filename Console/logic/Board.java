package logic;

import java.util.ArrayList;
import java.util.List;
import objects.*;

/**
 * Board : contains the state of the chess board and provides methods to manipulate it. 
 * does not handle game rules (those are in GameSession and Rules).
 */
public class Board {

    // ====== promotion selector  ======
    private PromotionSelector promotionSelector;
    @FunctionalInterface
    public interface PromotionSelector {
        PromotionChoice select(PieceColor color, int r, int c);
    }
    public void setPromotionSelector(PromotionSelector selector) {
        this.promotionSelector = selector;
    }

    // ====== board state ======
    private static final int SIZE = 8;
    private final Piece[][] squares = new Piece[SIZE][SIZE];

    /** Constructor: initializes the board to the standard starting position.    */
    public Board() { resetToStandard(); }

    /** Resets the board to the standard starting position. */
    public final void resetToStandard() {
        // Clear the board first
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                setPieceAt(r, c, null);
            }
        }

        // Black side
        setPieceAt(0, 0, new Rook  (PieceColor.BLACK, 0, 0));
        setPieceAt(0, 1, new Knight(PieceColor.BLACK, 0, 1));
        setPieceAt(0, 2, new Bishop(PieceColor.BLACK, 0, 2));
        setPieceAt(0, 3, new Queen (PieceColor.BLACK, 0, 3));
        setPieceAt(0, 4, new King  (PieceColor.BLACK, 0, 4));
        setPieceAt(0, 5, new Bishop(PieceColor.BLACK, 0, 5));
        setPieceAt(0, 6, new Knight(PieceColor.BLACK, 0, 6));
        setPieceAt(0, 7, new Rook  (PieceColor.BLACK, 0, 7));
        // Black pawns
        for (int c = 0; c < SIZE; c++) setPieceAt(1, c, new Pawn(PieceColor.BLACK, 1, c));

        // White pawns
        for (int c = 0; c < SIZE; c++) setPieceAt(6, c, new Pawn(PieceColor.WHITE, 6, c));

        // White side
        setPieceAt(7, 0, new Rook  (PieceColor.WHITE, 7, 0));
        setPieceAt(7, 1, new Knight(PieceColor.WHITE, 7, 1));
        setPieceAt(7, 2, new Bishop(PieceColor.WHITE, 7, 2));
        setPieceAt(7, 3, new Queen (PieceColor.WHITE, 7, 3));
        setPieceAt(7, 4, new King  (PieceColor.WHITE, 7, 4));
        setPieceAt(7, 5, new Bishop(PieceColor.WHITE, 7, 5));
        setPieceAt(7, 6, new Knight(PieceColor.WHITE, 7, 6));
        setPieceAt(7, 7, new Rook  (PieceColor.WHITE, 7, 7));
    }

    // ---------- Utility methods ----------
    private boolean inBounds(int r, int c) {
        return r >= 0 && r < SIZE && c >= 0 && c < SIZE;
    }

    public Piece getPieceAt(int r, int c) {
        return inBounds(r, c) ? squares[r][c] : null;
    }

    /** Setter for piece at (r,c) */
    public void setPieceAt(int r, int c, Piece p) {
        if (!inBounds(r, c)) return;
        squares[r][c] = p;
        if (p != null) p.setPosition(r, c);
    }

    public void clearSquare(int r, int c) {
        if (inBounds(r, c)) squares[r][c] = null;
    }

    /**
     * moves a piece from (sr,sc) to (er,ec) without validation.
     * Handles captures and pawn promotion.
     * Returns the captured piece (if any)
     */
    public Piece movePiece(int sr, int sc, int er, int ec) {
        if (!inBounds(sr, sc) || !inBounds(er, ec)) return null;

        Piece moving   = getPieceAt(sr, sc);
        if (moving == null) return null;
        Piece captured = getPieceAt(er, ec);

        // move the piece
        setPieceAt(sr, sc, null);
        setPieceAt(er, ec, moving);

        // Pawn promotion: if the pawn reaches the last rank, replace it with a new piece
        if (moving instanceof Pawn pawn && pawn.canPromote(er)) {
            PromotionChoice choice = (promotionSelector != null)
                    ? promotionSelector.select(pawn.getColor(), er, ec)
                    : PromotionChoice.QUEEN; // Default to queen if no UI

            Piece promoted = switch (choice) {
                case QUEEN  -> new Queen (pawn.getColor(), er, ec);
                case ROOK   -> new Rook  (pawn.getColor(), er, ec);
                case BISHOP -> new Bishop(pawn.getColor(), er, ec);
                case KNIGHT -> new Knight(pawn.getColor(), er, ec);
            };
            setPieceAt(er, ec, promoted);
        }

        return captured;
    }

    // ---------- Deep copy ----------
    public Board deepCopy() {
        Board copy = new Board();
        // Clear the default starting position of the copy
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                copy.setPieceAt(r, c, null);
            }
        }
        // Copy the current pieces
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Piece p = this.getPieceAt(r, c);
                if (p != null) copy.setPieceAt(r, c, clonePiece(p));
            }
        }
        return copy;
    }

    public void copyFrom(Board src) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Piece p = src.getPieceAt(r, c);
                setPieceAt(r, c, (p == null) ? null : clonePiece(p));
            }
        }
    }

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

    // ---------- Helper methods for Rules.java ----------
    public List<Piece> getPiecesByColor(PieceColor color) {
        List<Piece> list = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Piece p = squares[r][c];
                if (p != null && p.getColor() == color) list.add(p);
            }
        }
        return list;
    }

    public int[] findKing(PieceColor color) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Piece p = squares[r][c];
                if (p != null && p.getColor() == color && p.getType() == PieceType.KING) {
                    return new int[]{r, c};
                }
            }
        }
        return null; 
    }
}
