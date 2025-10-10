package logic;

import objects.*;

public final class Rules {
    private Rules() {}

    /* check if a square is attacked */
    public static boolean isSquareAttacked(Board board, int r, int c, PieceColor byColor) {
        for (Piece p : board.getPiecesByColor(byColor)) {
            if (p.attacks(board, p.getX(), p.getY(), r, c)) return true;
        }
        return false;
    }

    /* check if a king is in check */
    public static boolean isInCheck(Board board, PieceColor color) {
        int[] king = board.findKing(color);
        if (king == null) return false; // no king found, should not happen in a valid game
        PieceColor opp = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        return isSquareAttacked(board, king[0], king[1], opp);
    }

    /* check if there are any legal moves (to escape check) */
    public static boolean hasAnyLegalMove(Board board, PieceColor color) {
        for (Piece p : board.getPiecesByColor(color)) {
            int sr = p.getX(), sc = p.getY();
            for (int er = 0; er < 8; er++) {
                for (int ec = 0; ec < 8; ec++) {
                    // check if the piece can move there via interface
                    if (!p.canMove(board, sr, sc, er, ec)) continue;

                    // try the move (your Board.movePiece has handled promotion)
                    Board copy = board.deepCopy();
                    copy.movePiece(sr, sc, er, ec);

                    // check if the king is still in check after the move
                    if (!isInCheck(copy, p.getColor())) return true;
                }
            }
        }
        return false;
    }

    /* check if a king is in checkmate */
    public static boolean isCheckmate(Board board, PieceColor color) {
        return isInCheck(board, color) && !hasAnyLegalMove(board, color);
    }

    public static boolean isStalemate(Board board, PieceColor color) {
        return !isInCheck(board, color) && !hasAnyLegalMove(board, color);
    }
}
